package ai.npc.Tolonis;

import static l2r.gameserver.model.base.AcquireSkillType.COLLECT;
import static l2r.gameserver.network.SystemMessageId.DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN_S1;
import static l2r.gameserver.network.SystemMessageId.NO_MORE_SKILLS_TO_LEARN;

import java.util.List;

import l2r.Config;
import l2r.gameserver.data.xml.impl.SkillTreesData;
import l2r.gameserver.model.L2SkillLearn;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.events.impl.character.player.OnPlayerLearnSkillRequested;
import l2r.gameserver.network.serverpackets.AcquireSkillList;
import l2r.gameserver.network.serverpackets.SystemMessage;

import ai.npc.AbstractNpcAI;

/**
 * Officer Tolonis.
 * @author Zoey76
 * @version 2.6.3.0
 */
public class Tolonis extends AbstractNpcAI
{
	
	private static final int TOLONIS_ID = 32611;
	
	private static final int MINIMUM_LEVEL = 75;
	
	public Tolonis()
	{
		super(Tolonis.class.getSimpleName(), "ai/npc");
		addFirstTalkId(TOLONIS_ID);
		bindPlayerLearnSkillRequested(TOLONIS_ID);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if ((player.getKarma() > 0) && !Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP)
		{
			return "officer_tolonis006.html";
		}
		return "officer_tolonis001.html";
	}
	
	@Override
	public void onLearnSkillRequested(OnPlayerLearnSkillRequested event)
	{
		if (event.getActiveChar().getLevel() >= MINIMUM_LEVEL)
		{
			showEtcSkillList(event.getActiveChar());
		}
		else
		{
			showPage(event.getActiveChar(), "officer_tolonis001a.html");
		}
	}
	
	// TODO(Zoey76): Generalize this function and move it to L2Npc class.
	private static void showEtcSkillList(L2PcInstance player)
	{
		final List<L2SkillLearn> skills = SkillTreesData.getInstance().getAvailableCollectSkills(player);
		final AcquireSkillList asl = new AcquireSkillList(COLLECT);
		for (L2SkillLearn skill : skills)
		{
			asl.addSkill(skill.getSkillId(), skill.getSkillLevel(), skill.getSkillLevel(), 0, 1);
		}
		
		if (skills.size() == 0)
		{
			final int minLevel = SkillTreesData.getInstance().getMinLevelForNewSkill(player, SkillTreesData.getInstance().getCollectSkillTree());
			if (minLevel > 0)
			{
				final SystemMessage sm = SystemMessage.getSystemMessage(DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN_S1);
				sm.addInt(minLevel);
				player.sendPacket(sm);
			}
			else
			{
				player.sendPacket(NO_MORE_SKILLS_TO_LEARN);
			}
		}
		else
		{
			player.sendPacket(asl);
		}
	}
}
