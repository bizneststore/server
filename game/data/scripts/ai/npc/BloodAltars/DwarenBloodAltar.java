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
public class DwarenBloodAltar extends ABBloodAltar
{
	private static final int[][] bossGroups =
	{
		{
			25782,
			130376,
			-180664,
			-3331,
			21220
		},
		{
			25800,
			129800,
			-180760,
			-3352,
			13028
		}
	};
	
	private static final int[][] BLOODALTARS_DEAD_NPC =
	{
		{
			4327,
			130136,
			-181176,
			-3312,
			49151
		},
		{
			4328,
			129992,
			-181096,
			-3312,
			40959
		},
		{
			4328,
			130280,
			-181096,
			-3312,
			57343
		}
	};
	
	private static final int[][] BLOODALTARS_ALIVE_NPC =
	{
		{
			4324,
			130136,
			-181176,
			-3312,
			49151
		},
		{
			4325,
			129992,
			-181096,
			-3312,
			40959
		},
		{
			4325,
			130280,
			-181096,
			-3312,
			57343
		}
	};
	
	public DwarenBloodAltar()
	{
		super(DwarenBloodAltar.class.getSimpleName());
		
		addKillId(25782);
		addKillId(25800);
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
		
		if (npcId == 25782)
		{
			progress1 = true;
		}
		
		if (npcId == 25800)
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