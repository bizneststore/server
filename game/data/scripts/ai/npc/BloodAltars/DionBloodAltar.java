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
public class DionBloodAltar extends ABBloodAltar
{
	private static final int[][] bossGroups =
	{
		{
			25753,
			16680,
			147992,
			-3267,
			63477
		},
		{
			25754,
			16872,
			148680,
			-3319,
			55285
		},
		{
			25757,
			17000,
			148008,
			-3254,
			55285
		}
	};
	
	private static final int[][] BLOODALTARS_DEAD_NPC =
	{
		{
			4328,
			16856,
			148216,
			-3276,
			3355
		},
		{
			4328,
			16808,
			148488,
			-3279,
			14324
		},
		{
			4327,
			16920,
			148360,
			-3282,
			54112
		}
	};
	
	private static final int[][] BLOODALTARS_ALIVE_NPC =
	{
		{
			4325,
			16856,
			148216,
			-3276,
			3355
		},
		{
			4325,
			16808,
			148488,
			-3279,
			14324
		},
		{
			4324,
			16920,
			148360,
			-3282,
			54112
		}
	};
	
	public DionBloodAltar()
	{
		super(DionBloodAltar.class.getSimpleName());
		
		addKillId(25753);
		addKillId(25754);
		addKillId(25757);
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
		
		if (npcId == 25753)
		{
			progress1 = true;
		}
		
		if (npcId == 25754)
		{
			progress2 = true;
		}
		
		if (npcId == 25757)
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