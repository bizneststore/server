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
package quests.Q00450_GatherEvidence;

import java.util.HashMap;
import java.util.Map;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.enums.QuestType;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;

/**
 * Gather Evidence. Was previously Grave Robber Rescue (450)
 * @author malyelfik REWORKED BY VMILON FOR L2SoE
 */
public class Q00450_GatherEvidence extends Quest
{
	// NPCs
	private static final int KANEMIKA = 32650;
	// Monsters
	private static final Map<Integer, Integer> MONSTERS = new HashMap<>();
	// Item
	private static final int EVIDENCE_OF_MIGRATION = 14876;
	// Misc
	private static final int MIN_LEVEL = 80;
	
	// All monsters in mines
	static
	{
		MONSTERS.put(22681, 630);
		MONSTERS.put(22683, 630);
		MONSTERS.put(22684, 630);
		MONSTERS.put(22679, 630);
		MONSTERS.put(22686, 670);
		MONSTERS.put(22687, 670);
		MONSTERS.put(22689, 670);
		MONSTERS.put(22688, 670);
		MONSTERS.put(22690, 710);
		MONSTERS.put(22682, 710);
		MONSTERS.put(22680, 710);
		MONSTERS.put(22685, 710);
		MONSTERS.put(22678, 710);
	}
	
	// REWARDS (dynasty weapon recipes 60%)
	private static final int[] REWARD =
	{
		9967,
		9968,
		9969,
		9970,
		9971,
		9972,
		9973,
		9974,
		9975
	};
	
	public Q00450_GatherEvidence()
	{
		super(450, Q00450_GatherEvidence.class.getSimpleName(), "Grave Robber Rescue");
		addStartNpc(KANEMIKA);
		addTalkId(KANEMIKA);
		addKillId(MONSTERS.keySet());
		registerQuestItems(EVIDENCE_OF_MIGRATION);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		
		if (st == null)
		{
			return null;
		}
		
		String htmltext = event;
		switch (event)
		{
			case "32650-04.htm":
			case "32650-05.htm":
			case "32650-06.html":
				break;
			case "32650-07.htm":
				st.startQuest();
				break;
			case "32650-10.html":
				st.exitQuest(QuestType.DAILY, true);
				st.giveItems(REWARD[getRandom(REWARD.length)], 1);
				break;
			case "9967":
			case "9968":
			case "9969":
			case "9970":
			case "9971":
			case "9972":
			case "9973":
			case "9974":
			case "9975":
				st.giveItems(Integer.valueOf(event), 1);
				st.takeItems(EVIDENCE_OF_MIGRATION, 1600);
				htmltext = "32650-12.html";
				st.exitQuest(QuestType.DAILY, true);
				break;
			default:
				htmltext = null;
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final QuestState st = player.getQuestState(getName());
		final L2PcInstance partyMember = getRandomPartyMember(player, 1);
		if (partyMember == null)
		{
			return super.onKill(npc, player, isPet);
		}
		
		// vmilon condition is set to 2 if player has 1200 evidence, and set to 3 when player has 1600
		if ((st.isCond(1) || st.isCond(2)) && (st.getQuestItemsCount(EVIDENCE_OF_MIGRATION) < 1600))
		{
			st.giveItems(EVIDENCE_OF_MIGRATION, 1);
			st.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		if (st.isCond(1) && (st.getQuestItemsCount(EVIDENCE_OF_MIGRATION) >= 1200))
		{
			st.setCond(2);
		}
		else if (st.isCond(2) && (st.getQuestItemsCount(EVIDENCE_OF_MIGRATION) >= 1600))
		{
			st.setCond(3);
		}
		
		return super.onKill(npc, player, isPet);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		
		if (st == null)
		{
			return htmltext;
		}
		
		if (npc.getId() == KANEMIKA)
		{
			switch (st.getState())
			{
				case State.COMPLETED:
					if (!st.isNowAvailable())
					{
						htmltext = "32650-03.html";
						break;
					}
					st.setState(State.CREATED);
				case State.CREATED:
					htmltext = (player.getLevel() >= MIN_LEVEL) ? "32650-01.htm" : "32650-02.htm";
					break;
				case State.STARTED:
					if (st.isCond(1))
					{
						htmltext = (!st.hasQuestItems(EVIDENCE_OF_MIGRATION)) ? "32650-08.html" : "32650-09.html";
					}
					else if (st.isCond(3))
					{
						// st.giveAdena(65000, true); // Glory days reward: 6 886 980 exp, 8 116 410 sp, 371 400 Adena
						htmltext = "32650-11.html";
						break;
					}
					else
					{
						htmltext = "32650-12.html";
						break;
					}
					
			}
		}
		
		return htmltext;
	}
}