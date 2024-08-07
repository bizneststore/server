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
public class TalkingIslandBloodAltar extends ABBloodAltar
{
	private static final int[][] BLOODALTARS_DEAD_NPC =
	{
		{
			4328,
			-92376,
			244648,
			-3572,
			0
		},
		{
			4327,
			-92552,
			244600,
			-3571,
			17559
		},
		{
			4328,
			-92648,
			244728,
			-3571,
			32768
		}
	};
	
	private static final int[][] BLOODALTARS_ALIVE_NPC =
	{
		{
			4325,
			-92376,
			244648,
			-3572,
			0
		},
		{
			4324,
			-92552,
			244600,
			-3571,
			17559
		},
		{
			4325,
			-92648,
			244728,
			-3571,
			32768
		}
	};
	
	public TalkingIslandBloodAltar()
	{
		super(TalkingIslandBloodAltar.class.getSimpleName());
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