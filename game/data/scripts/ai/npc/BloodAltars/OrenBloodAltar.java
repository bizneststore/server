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
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.npc.BloodAltars;

import l2r.Config;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.util.Rnd;

public class OrenBloodAltar extends ABBloodAltar
{
	private static final int[][] bossGroups =
	{
		{
			25767,
			80328,
			46792,
			-3189,
			36123
		},
		{
			25770,
			80520,
			47368,
			-3193,
			36736
		}
	};
	
	private static final int[][] BLOODALTARS_DEAD_NPC =
	{
		{
			4328,
			80328,
			47192,
			-3176,
			53375
		},
		{
			4328,
			80040,
			47176,
			-3176,
			16000
		},
		{
			4327,
			80184,
			47272,
			-3178,
			6000
		}
	};
	
	private static final int[][] BLOODALTARS_ALIVE_NPC =
	{
		{
			4325,
			80328,
			47192,
			-3176,
			53375
		},
		{
			4325,
			80040,
			47176,
			-3176,
			16000
		},
		{
			4324,
			80184,
			47272,
			-3178,
			6000
		}
	};
	
	public OrenBloodAltar()
	{
		super(OrenBloodAltar.class.getSimpleName());
		
		addKillId(25767);
		addKillId(25770);
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
		
		if (npcId == 25767)
		{
			progress1 = true;
		}
		
		if (npcId == 25770)
		{
			progress2 = true;
		}
		
		if (progress1 && progress2)
		{
			progress1 = false;
			progress2 = false;
			
			manageBosses(false);
			manageNpcs(true);
			bossesSpawned = false;
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