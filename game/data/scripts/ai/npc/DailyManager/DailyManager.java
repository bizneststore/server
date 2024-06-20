package ai.npc.DailyManager;

import java.util.Map;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;

import gr.sr.configsEngine.configs.impl.CustomNpcsConfigs;
import gr.sr.dailyengine.DailiesManager;
import gr.sr.dailyengine.base.daily;

import ai.npc.AbstractNpcAI;

/**
 * @author vmilon
 */
public class DailyManager extends AbstractNpcAI
{
	private final static int NPC = 576;
	
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
		return null;
	}
	
	public void showDaily(L2PcInstance player, L2Npc npc)
	{
		StringBuilder tb = new StringBuilder();
		tb.append("<html><title>Daily Quests</title><body><br>");
		tb.append("Hello Adventurer, I represent the <font color='LEVEL'>Bounty hunter guild</font>.<br>");
		tb.append("I am looking for capable people willing to complete a variety of assignments that our clients require, with a reward of course.. <br>");
		tb.append("Every day at dawn (6 A.M) the guild brings me more assignments, so check every day if you want to keep yourself busy.<br>");
		
		// Retrieve daily quests from DailiesManager
		Map<Integer, daily> dailyQuests = DailiesManager.getInstance().getDailyList();
		
		if (!dailyQuests.isEmpty())
		{
			tb.append("<br><img src=\"l2ui.squaregray\" width=\"280\" height=\"1s\"><br>");
			for (daily quest : dailyQuests.values())
			{
				tb.append("<a action=\"bypass -h Quest ").append(quest.getId()).append(" ").append(quest.getName()).append("\">");
				tb.append(quest.getName()).append("</a><br>");
				tb.append(quest.getDescription()).append("<br>");
				tb.append("Reward: ").append(quest.getReward()).append("<br>");
				tb.append("<br>");
			}
		}
		else
		{
			tb.append("<br>No daily quests available.<br>");
		}
		
		tb.append("</body></html>");
		
		NpcHtmlMessage msg = new NpcHtmlMessage(npc.getObjectId());
		msg.setHtml(tb.toString());
		player.sendPacket(msg);
	}
	
}
