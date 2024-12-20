package ai.npc.Fisherman;

import static l2r.gameserver.model.base.AcquireSkillType.FISHING;
import static l2r.gameserver.network.SystemMessageId.DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN_S1;
import static l2r.gameserver.network.SystemMessageId.NO_MORE_SKILLS_TO_LEARN;

import java.util.List;

import l2r.Config;
import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.data.xml.impl.SkillTreesData;
import l2r.gameserver.instancemanager.FishingChampionshipManager;
import l2r.gameserver.model.L2SkillLearn;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2MerchantInstance;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.events.impl.character.player.OnPlayerLearnSkillRequested;
import l2r.gameserver.network.serverpackets.AcquireSkillList;
import l2r.gameserver.network.serverpackets.SystemMessage;

import ai.npc.AbstractNpcAI;

/**
 * Fisherman AI.
 * @author Adry_85
 * @since 2.6.0.0
 */
public class Fisherman extends AbstractNpcAI
{
	// NPC
	private static final int[] FISHERMAN =
	{
		31562,
		31563,
		31564,
		31565,
		31566,
		31567,
		31568,
		31569,
		31570,
		31571,
		31572,
		31573,
		31574,
		31575,
		31576,
		31577,
		31578,
		31579,
		31696,
		31697,
		31989,
		32007,
		32348
	};
	
	public Fisherman()
	{
		super(Fisherman.class.getSimpleName(), "ai/npc");
		addStartNpc(FISHERMAN);
		addTalkId(FISHERMAN);
		addFirstTalkId(FISHERMAN);
		bindPlayerLearnSkillRequested(FISHERMAN);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "fishing_championship.htm":
			{
				if (Config.ALT_FISH_CHAMPIONSHIP_ENABLED)
				{
					FishingChampionshipManager.getInstance().showChampScreen(player, npc);
					return null;
				}
				return "no_fish_event001.htm";
			}
			case "FishingReward":
			{
				if (Config.ALT_FISH_CHAMPIONSHIP_ENABLED)
				{
					if (FishingChampionshipManager.getInstance().isWinner(player.getName()))
					{
						FishingChampionshipManager.getInstance().getReward(player);
					}
					else
					{
						return "no_fish_event_reward001.htm";
					}
				}
				else
				{
					return "no_fish_event001.htm";
				}
				break;
			}
			case "BuySellRefund":
			{
				((L2MerchantInstance) npc).showBuyWindow(player, npc.getId() * 100, true);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if ((player.getKarma() > 0) && !Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP)
		{
			return npc.getId() + "-pk.htm";
		}
		return npc.getId() + ".htm";
	}
	
	@Override
	public void onLearnSkillRequested(OnPlayerLearnSkillRequested event)
	{
		showFishSkillList(event.getActiveChar());
	}
	
	/**
	 * Display the Fishing Skill list to the player.
	 * @param player the player
	 */
	public static void showFishSkillList(L2PcInstance player)
	{
		final List<L2SkillLearn> fishskills = SkillTreesData.getInstance().getAvailableFishingSkills(player);
		final AcquireSkillList asl = new AcquireSkillList(FISHING);
		int count = 0;
		
		for (L2SkillLearn s : fishskills)
		{
			if (SkillData.getInstance().getSkill(s.getSkillId(), s.getSkillLevel()) != null)
			{
				count++;
				asl.add(s);
			}
		}
		
		if (count > 0)
		{
			player.sendPacket(asl);
		}
		else
		{
			final int minlLevel = SkillTreesData.getInstance().getMinLevelForNewSkill(player, SkillTreesData.getInstance().getFishingSkillTree());
			if (minlLevel > 0)
			{
				final SystemMessage sm = SystemMessage.getSystemMessage(DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN_S1);
				sm.addInt(minlLevel);
				player.sendPacket(sm);
			}
			else
			{
				player.sendPacket(NO_MORE_SKILLS_TO_LEARN);
			}
		}
	}
}
