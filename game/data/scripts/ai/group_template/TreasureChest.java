/*
 * Copyright (C) 2004-2018 L2J DataPack
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
package ai.group_template;

import java.util.List;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2ChestInstance;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.holders.ItemChanceHolder;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.network.SystemMessageId;

import ai.npc.AbstractNpcAI;

/**
 * Treasure Chest AI.
 * @author ivantotov
 */
public final class TreasureChest extends AbstractNpcAI
{
	private static final String TIMER_1 = "5001";
	private static final String TIMER_2 = "5002";
	private static final int MAX_SPAWN_TIME = 14400000;
	private static final int ATTACK_SPAWN_TIME = 5000;
	private static final int PLAYER_LEVEL_THRESHOLD = 78;
	private static final int MAESTROS_KEY_SKILL_ID = 22271;
	
	public TreasureChest()
	{
		super(TreasureChest.class.getSimpleName(), "ai/group_template");
		
		addSpawnId(L2ChestInstance.DROPS.keySet());
		addAttackId(L2ChestInstance.DROPS.keySet());
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
//			case TIMER_1:
			case TIMER_2:
			{
				npc.decayMe();
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		L2ChestInstance chest = (L2ChestInstance) npc;
		chest.disableCoreAI(true);
		chest.setIsNoRndWalk(true);
		chest.setIsImmobilized(true);
		chest.setMustRewardExpSp(false);
		chest.enableItemDrop(false);
		chest.resetInteract();
		
		npc.getVariables().set("MAESTRO_SKILL_USED", 0);
		startQuestTimer(TIMER_2, MAX_SPAWN_TIME, npc, null);
		return super.onSpawn(npc);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon, L2Skill skill)
	{
		if (attacker.getLevel() < PLAYER_LEVEL_THRESHOLD)
		{
			npc.getVariables().set("MAX_LEVEL_DIFFERENCE", 6);
		}
		else
		{
			npc.getVariables().set("MAX_LEVEL_DIFFERENCE", 5);
		}
		
		if (npc.getVariables().getInt("MAESTRO_SKILL_USED") == 0)
		{
			if ((skill != null) && (skill.getId() == MAESTROS_KEY_SKILL_ID))
			{
				npc.getVariables().set("MAESTRO_SKILL_USED", 1);
				startQuestTimer(TIMER_1, ATTACK_SPAWN_TIME, npc, null);
				
				if ((npc.getLevel() - npc.getVariables().getInt("MAX_LEVEL_DIFFERENCE")) > attacker.getLevel())
				{
					addSkillCastDesire(npc, attacker, L2ChestInstance.TREASURE_BOMBS[npc.getLevel() / 10], 1000000);
					attacker.broadcastSocialAction(13);
				}
				else
				{
					if (getRandom(100) < 10)
					{
						npc.doDie(null);
						
						final List<ItemChanceHolder> items = L2ChestInstance.DROPS.get(npc.getId());
						if (items == null)
						{
							_log.warn("Tresure Chest ID " + npc.getId() + " doesn't have a drop list!");
						}
						else
						{
							attacker.broadcastSocialAction(3);
							for (ItemChanceHolder item : items)
							{
								if (getRandom(10000) < item.getChance())
								{
									npc.dropItem(attacker, item.getId(), item.getCount());
								}
							}
						}
					}
					else
					{
						addSkillCastDesire(npc, attacker, L2ChestInstance.TREASURE_BOMBS[npc.getLevel() / 10], 1000000);
						attacker.broadcastSocialAction(13);
					}
				}
			}
			else
			{
				if (getRandom(100) < 30)
				{
					attacker.sendPacket(SystemMessageId.IF_YOU_HAVE_A_MAESTROS_KEY_YOU_CAN_USE_IT_TO_OPEN_THE_TREASURE_CHEST);
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
}
