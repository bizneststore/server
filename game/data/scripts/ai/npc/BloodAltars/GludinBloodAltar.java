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
public class GludinBloodAltar extends ABBloodAltar
{
	private static final int[][] bossGroups =
	{
		{
			25735,
			-86312,
			151544,
			-3083,
			4836
		},
		{
			25738,
			-87000,
			151720,
			-3084,
			6133
		},
		{
			25741,
			-86568,
			152040,
			-3098,
			6133
		}
	};
	
	private static final int[][] BLOODALTARS_DEAD_NPC =
	{
		{
			4328,
			-86440,
			151624,
			-3083,
			0
		},
		{
			4327,
			-86568,
			151736,
			-3083,
			17559
		},
		{
			4328,
			-86744,
			151704,
			-3083,
			32768
		}
	};
	
	private static final int[][] BLOODALTARS_ALIVE_NPC =
	{
		{
			4325,
			-86440,
			151624,
			-3083,
			0
		},
		{
			4324,
			-86568,
			151736,
			-3083,
			17559
		},
		{
			4325,
			-86744,
			151704,
			-3083,
			32768
		}
	};
	
	public GludinBloodAltar()
	{
		super(GludinBloodAltar.class.getSimpleName());
		
		addKillId(25735);
		addKillId(25738);
		addKillId(25741);
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
		
		if (npcId == 25735)
		{
			progress1 = true;
		}
		
		if (npcId == 25738)
		{
			progress2 = true;
		}
		
		if (npcId == 25741)
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