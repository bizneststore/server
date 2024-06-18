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
		String monsterHtml = Q11000_GatherItems.MONSTER_HTM.get(Q11000_GatherItems.MONSTER);
		if (event.startsWith("showMainWindow"))
		{
			showDaily(player, npc);
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
							{
								return "gather-04.html";
								
							}
							case 2:
							{
								return "gather-05.html";
								
							}
						}
						return "";
					} // Provide the HTML for gathering items
					default:
						return ""; // Handle other cases if necessary
				}
			}
			else if (event.startsWith("showMainWindow"))
			{
				showDaily(player, npc);
			}
			
		}
		else if (event.startsWith("accept"))
		{
			QuestState questState = player.getQuestState(Q11000_GatherItems.class.getSimpleName());
			if (questState != null)
			{
				questState.startQuest();
				return monsterHtml;
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
		if (player.getLevel() >= 75)
		{
			showDaily(player, npc);
		}
		return "";
	}
	
	public void showDaily(L2PcInstance player, L2Npc npc)
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
		
		tb.append("<br><img src=\"l2ui.squaregray\" width=\"280\" height=\"1s\"><br>");
		// tb.append("<center><button value=\"Back\" action=\"bypass -h Quest DailyManager showMainWindow\" width=160 height=32 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
		
		NpcHtmlMessage msg = new NpcHtmlMessage(npc.getObjectId());
		msg.setHtml(tb.toString());
		player.sendPacket(msg);
	}
}
