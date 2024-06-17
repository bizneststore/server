package ai.npc.DailyManager;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;

import ai.npc.AbstractNpcAI;
import handlers.custom.dailyhandler;
import quests.Q11000_GatherItems.Q11000_GatherItems;

/**
 * @author vmilon
 */
public class DailyManager extends AbstractNpcAI
{
	private final static int NPC = 576;
	
	public DailyManager()
	{
		super(DailyManager.class.getSimpleName(), "ai/npc");
		addStartNpc(NPC);
		addFirstTalkId(NPC);
		addTalkId(NPC);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.startsWith("showMainWindow"))
		{
			showDaily(player, npc);
			return null;
		}
		else if (event.startsWith("gatheritems"))
		{
			QuestState questState = player.getQuestState(Q11000_GatherItems.class.getSimpleName());
			if (questState != null)
			{
				switch (questState.getState())
				{
					case State.CREATED:
						return "gather-00.htm"; // Quest not yet started
						
					case State.STARTED:
					{
						switch (questState.getCond())
						{
							case 1:
								return "daily-04.html";
							case 2:
								return "daily-05.html";
						}
						return "";
					}
					default:
						return ""; // Handle other cases if necessary
				}
			}
			else
			{
				return "gather-00.htm"; // If quest state is null, it means the quest has not been taken yet.
			}
		}
		return "";
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (player.getLevel() < 75)
		{
			return "gather-01.htm";
		}
		else
		{
			showDaily(player, npc);
			return null;
		}
	}
	
	private void showDaily(L2PcInstance player, L2Npc npc)
	{
		StringBuilder tb = new StringBuilder();
		
		tb.append("<html><title>Daily Quests</title><body><br>");
		tb.append("Hello Adventurer, I represent the <font color='LEVEL'>Bounty hunter guild</font>.<br>");
		tb.append("I am looking for capable people willing to complete a variety of assignments that our clients require, with a reward of course.. <br>");
		tb.append("Every day at dawn (6 A.M) the guild brings me more assignments, so check every day if you want to keep yourself busy.<br>");
		
		// Check if the player has completed any daily quests
		if (dailyhandler.getInstance().hasCompletedQuest(player, 11000))
		{
			tb.append("Gather Items: <font color=\"LEVEL\">COMPLETED</font><br>");
		}
		else
		{
			tb.append("<a action=\"bypass -h Quest DailyManager gatheritems\">Gather Items</a><br>");
		}
		
		tb.append("<br><img src=\"l2ui.squaregray\" width=\"280\" height=\"1\"><br>");
		
		NpcHtmlMessage msg = new NpcHtmlMessage(npc.getObjectId());
		msg.setHtml(tb.toString());
		player.sendPacket(msg);
	}
}
