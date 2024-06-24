package quests.Q11000_GatherItems;

import java.util.Random;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;

import gr.sr.dailyquests.DailyDBcon;

/**
 * DAILY QUEST GATHER ITEMS (11000) DQ1 ON DATABASE
 * @author vmilon
 */
public final class Q11000_GatherItems extends Quest
{
	// Npc
	private static final int NPC = 576;
	// Monsters
	public static int MONSTER = 577;
	private static int ITEM = 1867;
	
	// Misc
	private static final int ITEMCOUNT = 100;
	
	public Q11000_GatherItems()
	{
		super(11000, Q11000_GatherItems.class.getSimpleName(), "Gather Items");
		addStartNpc(NPC);
		addTalkId(NPC);
		addKillId(MONSTER);
		registerQuestItems(ITEM);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if ((st != null) && (event.equalsIgnoreCase("bear-01.htm")))
		{
			st.startQuest();
			DailyDBcon.insertQuestRecord(player.getObjectId(), 11000);
			return event;
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState st = killer.getQuestState(getName());
		if ((st != null) && st.isCond(1))
		{
			int DROP = new Random().nextInt(3) + 1;
			st.giveItems(ITEM, DROP);
			if (st.getQuestItemsCount(ITEM) >= ITEMCOUNT)
			{
				st.setCond(2, true);
			}
			else
			{
				st.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (st == null)
		{
			return htmltext;
		}
		switch (st.getState())
		{
			case State.CREATED:
			{
				htmltext = "bear-01.html";
				break;
			}
			case State.STARTED:
			{
				switch (st.getCond())
				{
					case 1:
					{
						htmltext = "gather-04.html";
						break;
					}
					case 2:
					{
						if (st.getQuestItemsCount(ITEM) >= ITEMCOUNT)
						{
							st.giveItems(57, 1000000);
							st.exitQuest(true, true);
							DailyDBcon.updateQuestStatus(player.getObjectId(), 11000, 1);
							htmltext = "gather-05.html";
							break;
						}
					}
				}
				break;
			}
		}
		return htmltext;
	}
}
