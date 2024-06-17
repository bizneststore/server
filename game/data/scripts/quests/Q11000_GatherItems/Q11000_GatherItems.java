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

import handlers.custom.dailyhandler;

/**
 * DAILY QUEST GATHER ITEMS (11000)
 * @author vmilon
 */
public final class Q11000_GatherItems extends Quest
{
	// Npc
	private static final int NPC = 576;
	// Item
	// Monsters
	private static final int[] MONSTERLIST = new int[]
	{
		20120, // Wolf
		577, // Bear
	};
	// Item
	private static int MONSTER;
	private static int ITEM;
	// Rewards
	private static final Map<Integer, Integer> REWARDS = new HashMap<>();
	private static final Map<Integer, String> MONSTER_HTM = new HashMap<>();
	
	static
	{
		MONSTER_HTM.put(577, "bear-01.htm"); // Bear
		MONSTER_HTM.put(20120, "wolf-01.htm"); // Wolf
	}
	
	static
	{
		REWARDS.put(390, 1); // Cotton Shirt
		REWARDS.put(29, 6); // Leather Pants
		REWARDS.put(22, 9); // Leather Shirt
		REWARDS.put(1119, 13); // Short Leather Gloves
		REWARDS.put(426, 16); // Tunic
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
		if ((st != null) && event.equalsIgnoreCase(MONSTER_HTM.get(MONSTER)))
		{
			st.startQuest();
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
				htmltext = "daily-00.htm";
				break;
			}
			case State.STARTED:
			{
				switch (st.getCond())
				{
					case 1:
					{
						htmltext = "daily-04.html";
						break;
					}
					case 2:
					{
						if (st.getQuestItemsCount(ITEM) >= ITEMCOUNT)
						{
							final int chance = getRandom(16);
							for (Map.Entry<Integer, Integer> reward : REWARDS.entrySet())
							{
								if (chance < reward.getValue())
								{
									st.giveItems(reward.getKey(), 1);
									break;
								}
							}
							st.exitQuest(true, true);
							dailyhandler.getInstance().markQuestAsCompleted(player, getId());
							htmltext = "daily-05.html";
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
