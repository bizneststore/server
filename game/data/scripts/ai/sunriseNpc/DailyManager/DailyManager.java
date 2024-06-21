package ai.sunriseNpc.DailyManager;

import java.util.LinkedHashMap;
import java.util.Map;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;

import gr.sr.configsEngine.configs.impl.CustomNpcsConfigs;
import gr.sr.dailyquests.DailyDBcon;

import ai.npc.AbstractNpcAI;

/**
 * Daily Manager NPC
 */
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
		else if (event.startsWith("gatheritems"))
		{
			return "gather.htm";
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
		tb.append("<br><img src=\"l2ui.squaregray\" width=\"280\" height=\"1s\"><br>");
		
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
				tb.append("<a action=\"bypass -h Quest Q" + questId + "_" + questName.replace(" ", "") + " " + questName.replace(" ", "").toLowerCase() + "\">" + questName + "</a><br>");
			}
		}
		
		NpcHtmlMessage msg = new NpcHtmlMessage(npc.getObjectId());
		msg.setHtml(tb.toString());
		player.sendPacket(msg);
	}
}
