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

import java.util.ArrayList;
import java.util.List;

import l2r.Config;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.model.actor.L2Npc;

import ai.npc.AbstractNpcAI;

/**
 * Author: vGodFather
 */
public abstract class ABBloodAltar extends AbstractNpcAI
{
	protected final long CHANGE_STATUS_DELAY = Config.CHANGE_STATUS * 60 * 1000;
	protected final long RESPAWN_TIME_DELAY = Config.RESPAWN_TIME * 60 * 1000;
	
	protected final List<L2Npc> deadnpcs = new ArrayList<>();
	protected final List<L2Npc> alivenpcs = new ArrayList<>();
	protected final List<L2Npc> bosses = new ArrayList<>();
	
	protected boolean progress1 = false;
	protected boolean progress2 = false;
	protected boolean progress3 = false;
	
	protected boolean bossesSpawned = false;
	
	public ABBloodAltar(String script)
	{
		super(ABBloodAltar.class.getSimpleName(), "ai/npc");
		
		manageNpcs(true);
		
		ThreadPoolManager.getInstance().scheduleGeneral(() -> changestatus(), CHANGE_STATUS_DELAY);
	}
	
	protected void manageBosses(boolean spawn)
	{
		if (getBossGroups() == null)
		{
			return;
		}
		
		if (spawn)
		{
			for (int[] bossspawn : getBossGroups())
			{
				L2Npc boss = addSpawn(bossspawn[0], bossspawn[1], bossspawn[2], bossspawn[3], bossspawn[4], false, 0, false);
				if (boss != null)
				{
					bosses.add(boss);
				}
			}
		}
		else
		{
			if (!bosses.isEmpty())
			{
				for (L2Npc boss : bosses)
				{
					if (boss != null)
					{
						boss.deleteMe();
					}
				}
			}
			bosses.clear();
		}
	}
	
	protected void manageNpcs(boolean spawnAlive)
	{
		if (spawnAlive)
		{
			for (int[] spawn : getNpcsAlive())
			{
				L2Npc npc = addSpawn(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4], false, 0, false);
				if (npc != null)
				{
					alivenpcs.add(npc);
				}
			}
			
			if (!deadnpcs.isEmpty())
			{
				for (L2Npc npc : deadnpcs)
				{
					if (npc != null)
					{
						npc.deleteMe();
					}
				}
			}
			deadnpcs.clear();
		}
		else
		{
			for (int[] spawn : getNpcDead())
			{
				L2Npc npc = addSpawn(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4], false, 0, false);
				if (npc != null)
				{
					deadnpcs.add(npc);
				}
			}
			
			if (!alivenpcs.isEmpty())
			{
				for (L2Npc npc : alivenpcs)
				{
					if (npc != null)
					{
						npc.deleteMe();
					}
				}
			}
			alivenpcs.clear();
		}
	}
	
	protected abstract void changestatus();
	
	protected abstract int[][] getBossGroups();
	
	protected abstract int[][] getNpcsAlive();
	
	protected abstract int[][] getNpcDead();
}