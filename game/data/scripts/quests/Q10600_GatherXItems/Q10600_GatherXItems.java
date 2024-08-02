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
package quests.Q10600_GatherXItems;

import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.data.sql.CharNameTable;
import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.instancemanager.QuestManager;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.network.serverpackets.QuestList;
import l2r.gameserver.util.Broadcast;

/**
 * GatherXItems (10600)
 */
public final class Q10600_GatherXItems extends Quest
{
	// NPC
	private static final int QUESTTEST = 576;
	// Item
	private static final int ANIMAL_SKIN = 1867;
	// Monsters
	private static final int[] MONSTERS =
	{
		577
	}; // Winnie
		// Misc
	private static final int ANIMAL_SKIN_COUNT = 100;
	// Reward
	private static final int REWARD_ADENA = 100000;
	
	private static final String TIMER_NAME = "quest_timer";
	
	public Q10600_GatherXItems()
	{
		super(10600, Q10600_GatherXItems.class.getSimpleName(), "Gather Animal Skins");
		addStartNpc(QUESTTEST);
		addKillId(MONSTERS);
		registerQuestItems(ANIMAL_SKIN);
		addSpawnId(QUESTTEST);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (event.equalsIgnoreCase(TIMER_NAME))
		{
			if (st != null)
			{
				if (st.isCond(2))
				{
					st.giveItems(57, REWARD_ADENA);
					Broadcast.toAllOnlinePlayers("You got the ADENA");
				}
				st.exitQuest(true, true);
				player.sendMessage("The quest has ended.");
				Broadcast.toAllOnlinePlayers("QUEST HAS ENDED");
				
			}
		}
		
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		ThreadPoolManager.getInstance().scheduleGeneral(() ->
		{
			// Fetch all character IDs from the database
			for (int charId : CharNameTable.getInstance().getAllCharIds())
			{
				L2PcInstance player = L2PcInstance.load(charId);
				if ((player != null))
				{
					Quest q = QuestManager.getInstance().getQuest("Q10600_GatherXItems");
					if (q != null)
					{
						QuestState st = player.getQuestState(q.getName());
						if (st == null)
						{
							st = q.newQuestState(player);
							st.startQuest();
							startQuestTimer(TIMER_NAME, 180000, npc, player, false); // 4 minutes timer
							player.sendPacket(new QuestList()); // Send updated quest list
						}
					}
				}
			}
		} , 1000); // Delay to ensure NPC is fully spawned
		
		return super.onSpawn(npc);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState st = killer.getQuestState(getName());
		if ((st != null) && st.isCond(1))
		{
			if (st.getQuestItemsCount(ANIMAL_SKIN) >= ANIMAL_SKIN_COUNT)
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
}
