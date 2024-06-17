package ai.npc.DailyManager;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;

import ai.npc.AbstractNpcAI;
import handlers.custom.dailyhandler;

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
		}
		// else if (event.startsWith("GatherItems"))
		// {
		// return "gathermain.htm";
		// }
		else if (event.startsWith("showMainWindow"))
		{
			return "daily-00.htm";
		}
		
		return "";
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (player.getLevel() < 75)
		{
			return "daily-01.htm";
		}
		if (player.getLevel() >= 75)
		{
			showDaily(player, npc);
		}
		return "";
	}
	
	private void showDaily(L2PcInstance player, L2Npc npc)
	{
		StringBuilder tb = new StringBuilder();
		tb.append("<html><title>Daily Quests</title><body><br>");
		
		if (dailyhandler.getInstance().hasCompletedQuest(player, 11000))
		{
			tb.append("<br>Gather Items Quest: <font color=\"LEVEL\">COMPLETED</font><br>");
			tb.append("<button value=\"Gather Items\" width=160 height=32 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" disabled=\"true\"></center>");
		}
		else
		{
			tb.append("<center><button value=\"Gather Items\" action=\"bypass -h Quest Q11000_GatherItems start\" width=160 height=32 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center>");
		}
		
		tb.append("<br><img src=\"l2ui.squaregray\" width=\"280\" height=\"1\"><br>");
		tb.append("<center><button value=\"Back\" action=\"bypass -h Quest DailyManager showMainWindow\" width=160 height=32 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center>");
		tb.append("</body></html>");
		
		NpcHtmlMessage msg = new NpcHtmlMessage(npc.getObjectId());
		msg.setHtml(tb.toString());
		player.sendPacket(msg);
	}
}