package ai.sunriseNpc.DailyManager;

import java.util.LinkedHashMap;
import java.util.Map;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;

import gr.sr.configsEngine.configs.impl.CustomNpcsConfigs;
import gr.sr.dailyquests.DailyDBcon;

import ai.npc.AbstractNpcAI;

public class DailyManager extends AbstractNpcAI
{
	private final static int NPC = 576;
	
	// Map to store quest IDs and their display names
	private static final Map<Integer, String> QUESTS = new LinkedHashMap<>();
	
	static
	{
		QUESTS.put(11000, "Gather Items");
		// Add more quests here with their IDs and names
		// QUESTS.put(11001, "Another Quest");
	}
	
	public DailyManager()
	{
		super(DailyManager.class.getSimpleName(), "ai/sunriseNpc");
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
		}
		else if (event.startsWith("showQuestHtml_"))
		{
			String questIdStr = event.substring("showQuestHtml_".length());
			try
			{
				int questId = Integer.parseInt(questIdStr);
				showQuestHtml(player, npc, questId);
			}
			catch (NumberFormatException e)
			{
				// Handle invalid quest ID
			}
		}
		return event;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (player.getLevel() < CustomNpcsConfigs.DAILY_REQUIRED_LEVEL)
		{
			return "daily-lvl.htm";
		}
		showDaily(player, npc);
		return "";
	}
	
	public void showDaily(L2PcInstance player, L2Npc npc)
	{
		StringBuilder tb = new StringBuilder();
		tb.append("<html><title>Daily Quests</title><body><br>");
		tb.append("Hello Adventurer, I represent the <font color='LEVEL'>Bounty hunter guild</font>.<br>");
		tb.append("I am looking for capable people willing to complete a variety of assignments that our clients require, with a reward of course.. <br>");
		tb.append("Every day at dawn (6 A.M) the guild brings me more assignments, so check every day if you want to keep yourself busy.<br>");
		tb.append("<br><img src=\"l2ui.squaregray\" width=\"280\" height=\"1\"><br>");
		
		for (Map.Entry<Integer, String> entry : QUESTS.entrySet())
		{
			int questId = entry.getKey();
			String questName = entry.getValue();
			boolean isQuestCompleted = DailyDBcon.isQuestCompleted(player.getObjectId(), questId);
			
			if (isQuestCompleted)
			{
				tb.append("<font color='FF0000'>" + questName + " (Completed)</font><br>");
			}
			else
			{
				tb.append("<a action=\"bypass -h Quest DailyManager showQuestHtml_" + questId + "\">" + questName + "</a><br>");
			}
		}
		
		tb.append("</body></html>");
		NpcHtmlMessage msg = new NpcHtmlMessage(npc.getObjectId());
		msg.setHtml(tb.toString());
		player.sendPacket(msg);
	}
	
	private void showQuestHtml(L2PcInstance player, L2Npc npc, int questId)
	{
		String htmlFile = getQuestHtmlFile(questId);
		if (htmlFile != null)
		{
			NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
			html.setHtml(htmlFile);
			player.sendPacket(html);
		}
	}
	
	private String getQuestHtmlFile(int questId)
	{
		switch (questId)
		{
			case 11000:
				return "data/scripts/quests/Q11000_GatherItems/gather.htm";
			// Add cases for other quests here
			// case 11001:
			// return "data/scripts/quests/Q11001_AnotherQuest/anotherquest.htm";
			default:
				return null; // Handle unknown quests if needed
		}
	}
}
