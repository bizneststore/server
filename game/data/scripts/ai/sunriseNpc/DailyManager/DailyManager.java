package ai.sunriseNpc.DailyManager;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;

import gr.sr.configsEngine.configs.impl.CustomNpcsConfigs;

import ai.npc.AbstractNpcAI;

/**
 * @author vmilon
 */
public class DailyManager extends AbstractNpcAI
{
	private final static int NPC = CustomNpcsConfigs.DAILY_NPC_ID;
	
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
		if (player.getLevel() < CustomNpcsConfigs.ACHIEVEMENT_REQUIRED_LEVEL)
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
		tb.append("<a action=bypass -h Quest Q11000_GaherItems gatheritems" + ">Gather Items</a>");
		// tb.append("<center><button value=\"Back\" action=\"bypass -h Quest DailyManager showMainWindow\" width=160 height=32 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
		
		NpcHtmlMessage msg = new NpcHtmlMessage(npc.getObjectId());
		msg.setHtml(tb.toString());
		player.sendPacket(msg);
	}
}
