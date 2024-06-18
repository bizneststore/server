/*
 * Copyright (C) 2004-2013 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests.Q11000_GatherItems;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;

/**
 * DAILY QUEST GATHER ITEMS (11000)
 * @author vmilon
 */
public final class Q11000_GatherItems extends Quest
{
	// Npc
	private static final int NPC = 576;
	// Monsters
	private static final int[] MONSTERLIST = new int[]
	{
		20120, // Wolf
		577, // Bear
	};
	public static int MONSTER;
	private static int ITEM;
	public static final Map<Integer, String> MONSTER_HTM = new HashMap<>();
	
	static
	{
		MONSTER_HTM.put(577, "bear-01.htm"); // Bear
		MONSTER_HTM.put(20120, "wolf-01.htm"); // Wolf
	}
	
	// Misc
	private static final int ITEMCOUNT = 100;
	
	public Q11000_GatherItems()
	{
		super(11000, Q11000_GatherItems.class.getSimpleName(), "Gather Items");
		addStartNpc(NPC);
		addTalkId(NPC);
		MONSTER = MONSTERLIST[new Random().nextInt(MONSTERLIST.length)];
		if (MONSTER == 577)
		{
			ITEM = 1867;
		}
		else if (MONSTER == 20120)
		{
			ITEM = 702;
		}
		addKillId(MONSTER);
		registerQuestItems(ITEM);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if ((st != null) && (event.equalsIgnoreCase("accept")))
		{
			String html = MONSTER_HTM.get(MONSTER);
			st.startQuest();
			return html;
		}
		
		// if ((st != null) && (event.equalsIgnoreCase("gather-05.html")))
		// {
		// st.giveItems(57, 100000000);
		// st.exitQuest(true, true);
		// dailyhandler.getInstance().markQuestAsCompleted(player, getId());
		// }
		else if (event.startsWith("gatheritems"))
		{
			QuestState questState = player.getQuestState(Q11000_GatherItems.class.getSimpleName());
			if (questState != null)
			{
				switch (questState.getState())
				{
					case State.CREATED:
						return "gather.htm"; // Quest not yet started
						
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
								questState.giveItems(57, 100000000);
								questState.exitQuest(true, true);
								return "gather-05.html";
								
							}
						}
						return "";
					} // Provide the HTML for gathering items
					default:
						return ""; // Handle other cases if necessary
				}
			}
			
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
		return htmltext;
	}
}
