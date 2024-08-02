/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program.
 */
package ai.npc.BloodAltars;

import l2r.Config;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.util.Rnd;

/**
 * Author: vGodFather
 */
public class GoddardBloodAltar extends ABBloodAltar
{
	private static final int[][] bossGroups =
	{
		{
			25787,
			152488,
			-57208,
			-3431,
			47429
		},
		{
			25790,
			152568,
			-58008,
			-3477,
			49151
		}
	};
	
	private static final int[][] BLOODALTARS_DEAD_NPC =
	{
		{
			4328,
			152440,
			-57816,
			-3447,
			0
		},
		{
			4328,
			152136,
			-57848,
			-3447,
			40961
		},
		{
			4327,
			152296,
			-57912,
			-3447,
			32768
		}
	};
	
	private static final int[][] BLOODALTARS_ALIVE_NPC =
	{
		{
			4325,
			152440,
			-57816,
			-3447,
			0
		},
		{
			4325,
			152136,
			-57848,
			-3447,
			40961
		},
		{
			4324,
			152296,
			-57912,
			-3447,
			32768
		}
	};
	
	public GoddardBloodAltar()
	{
		super(GoddardBloodAltar.class.getSimpleName());
		
		addKillId(25787);
		addKillId(25790);
	}
	
	@Override
	protected void changestatus()
	{
		if (Rnd.chance(Config.CHANCE_SPAWN))
		{
			if (!bossesSpawned)
			{
				manageNpcs(false);
				manageBosses(true);
				bossesSpawned = true;
			}
			else
			{
				manageBosses(false);
				manageNpcs(true);
				bossesSpawned = false;
				ThreadPoolManager.getInstance().scheduleGeneral(() -> changestatus(), RESPAWN_TIME_DELAY);
			}
		}
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		final int npcId = npc.getId();
		
		if (npcId == 25787)
		{
			progress1 = true;
		}
		
		if (npcId == 25790)
		{
			progress2 = true;
		}
		
		if (progress1 && progress2)
		{
			progress1 = false;
			progress2 = false;
			
			manageBosses(false);
			manageNpcs(true);
			ThreadPoolManager.getInstance().scheduleGeneral(() -> changestatus(), RESPAWN_TIME_DELAY);
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	protected int[][] getBossGroups()
	{
		return bossGroups;
	}
	
	@Override
	protected int[][] getNpcsAlive()
	{
		return BLOODALTARS_ALIVE_NPC;
	}
	
	@Override
	protected int[][] getNpcDead()
	{
		return BLOODALTARS_DEAD_NPC;
	}
}