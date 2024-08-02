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
public class GiranBloodAltar extends ABBloodAltar
{
	private static final int[][] bossGroups =
	{
		{
			25760,
			80888,
			142872,
			-3552,
			6133
		},
		{
			25763,
			80632,
			142200,
			-3559,
			8191
		},
		{
			25766,
			80312,
			142568,
			-3573,
			4836
		}
	};
	
	private static final int[][] BLOODALTARS_DEAD_NPC =
	{
		{
			4328,
			80856,
			142680,
			-3553,
			0
		},
		{
			4327,
			80952,
			142536,
			-3557,
			0
		},
		{
			4328,
			80824,
			142392,
			-3554,
			49152
		}
	};
	
	private static final int[][] BLOODALTARS_ALIVE_NPC =
	{
		{
			4325,
			80856,
			142680,
			-3553,
			0
		},
		{
			4324,
			80952,
			142536,
			-3557,
			0
		},
		{
			4325,
			80824,
			142392,
			-3554,
			49152
		}
	};
	
	public GiranBloodAltar()
	{
		super(GiranBloodAltar.class.getSimpleName());
		
		addKillId(25760);
		addKillId(25763);
		addKillId(25766);
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
		
		if (npcId == 25760)
		{
			progress1 = true;
		}
		
		if (npcId == 25763)
		{
			progress2 = true;
		}
		
		if (npcId == 25766)
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