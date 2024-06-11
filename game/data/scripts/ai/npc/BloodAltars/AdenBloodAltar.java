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
public class AdenBloodAltar extends ABBloodAltar
{
	protected static final int[][] bossGroups =
	{
		{
			25793,
			152936,
			25016,
			-2154,
			28318
		},
		{
			25794,
			153144,
			24664,
			-2139,
			32767
		},
		{
			25797,
			152392,
			24712,
			-2158,
			24575
		}
	};
	
	protected static final int[][] BLOODALTARS_DEAD_NPC =
	{
		{
			4327,
			152728,
			24920,
			-2120,
			16383
		},
		{
			4328,
			152856,
			24840,
			-2128,
			8191
		},
		{
			4328,
			152584,
			24840,
			-2120,
			24575
		}
	};
	
	protected static final int[][] BLOODALTARS_ALIVE_NPC =
	{
		{
			4324,
			152728,
			24920,
			-2120,
			16383
		},
		{
			4325,
			152856,
			24840,
			-2128,
			8191
		},
		{
			4325,
			152584,
			24840,
			-2120,
			24575
		}
	};
	
	public AdenBloodAltar()
	{
		super(AdenBloodAltar.class.getSimpleName());
		
		addKillId(25793);
		addKillId(25794);
		addKillId(25797);
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
		
		if (npcId == 25793)
		{
			progress1 = true;
		}
		
		if (npcId == 25794)
		{
			progress2 = true;
		}
		
		if (npcId == 25797)
		{
			progress3 = true;
		}
		
		if (progress1 && progress2 && progress3)
		{
			progress1 = false;
			progress2 = false;
			progress3 = false;
			
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