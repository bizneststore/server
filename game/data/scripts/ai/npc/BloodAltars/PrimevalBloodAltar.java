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
import l2r.util.Rnd;

/**
 * Author: vGodFather
 */
public class PrimevalBloodAltar extends ABBloodAltar
{
	private static final int[][] BLOODALTARS_DEAD_NPC =
	{
		{
			4327,
			10840,
			-24184,
			-3640,
			40959
		},
		{
			4328,
			10968,
			-24248,
			-3640,
			45796
		},
		{
			4328,
			10824,
			-24024,
			-3640,
			29412
		}
	};
	
	private static final int[][] BLOODALTARS_ALIVE_NPC =
	{
		{
			4324,
			10840,
			-24184,
			-3640,
			40959
		},
		{
			4325,
			10968,
			-24248,
			-3640,
			45796
		},
		{
			4325,
			10824,
			-24024,
			-3640,
			29412
		}
	};
	
	public PrimevalBloodAltar()
	{
		super(PrimevalBloodAltar.class.getSimpleName());
	}
	
	@Override
	protected void changestatus()
	{
		if (Rnd.chance(Config.CHANCE_SPAWN))
		{
			boolean aliveSpawned = false;
			if (!aliveSpawned)
			{
				manageNpcs(false);
			}
			else
			{
				manageNpcs(true);
				ThreadPoolManager.getInstance().scheduleGeneral(() -> changestatus(), RESPAWN_TIME_DELAY);
			}
		}
	}
	
	@Override
	protected int[][] getBossGroups()
	{
		return null;
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