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
package custom.SpawnNPC;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.util.Broadcast;
import l2r.util.Rnd;

/**
 * This script automatically spawns, despawns and respawns an NPC of your choice at your desired location as well as announces the spawn location and despawn/respawn time. FIXME the current coordinates are from all the Newbie Guides in starting villages. Change them to your desired locations.
 * @author jurchiks
 */
public class SpawnNPC extends Quest
{
	private static final int NPC = 576; // the ID of the NPC to spawn
	// private static final int INITIAL_TIME = 60; // spawn 1 hour after server has started
	// private static final int DESPAWN_TIME = 120; // despawn 2 hours after spawned
	// private static final int RESPAWN_TIME = 120; // respawn 2 hours after despawned
	private static final int INITIAL_TIME = 3; // spawn 1 minute after server has started
	private static final int DESPAWN_TIME = 4; // despawn 60 minutes after spawned
	private static final int RESPAWN_TIME = 2; // respawn 5 minutes after despawned
	private static final boolean RANDOM_SPAWN = false; // if true, NPC will spawn in random places, otherwise only the FIRST coordinates will be used
	
	//@formatter:off
	private static final int[][] COORDS =
	{
		{ 82504, 148536, -3472, 0 }, // Giran test spawn 1
	}; // X, Y, Z, heading
	//@formatter:on
	
	public SpawnNPC(int id, String name, String descr)
	{
		super(id, name, descr);
		startQuestTimer("spawn", 60000 * INITIAL_TIME, null, null);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("spawn"))
		{
			int loc = RANDOM_SPAWN ? Rnd.get(COORDS.length) : 0;
			npc = addSpawn(NPC, COORDS[loc][0], COORDS[loc][1], COORDS[loc][2], COORDS[loc][3], false, 0);
			startQuestTimer("despawn", 60000 * DESPAWN_TIME, npc, null);
			// Not needed (seanet)
			Broadcast.toAllOnlinePlayers("Your NPC has spawned at " + COORDS[loc][0] + " " + COORDS[loc][1] + " " + COORDS[loc][2]);
			// Broadcast.announceToOnlinePlayers("It will disappear in " + calcAnnounce(DESPAWN_TIME));
		}
		else if (event.equalsIgnoreCase("despawn"))
		{
			npc.deleteMe();
			startQuestTimer("spawn", 60000 * RESPAWN_TIME, npc, null);
			// Not needed (seanet)
			Broadcast.toAllOnlinePlayers("Your NPC has disappeared.");
			// Broadcast.announceToOnlinePlayers("It will respawn in " + calcAnnounce(RESPAWN_TIME));
		}
		return null;
	}
	
	String calcAnnounce(int time)
	{
		String announce = "";
		int minutes = time % 60;
		if ((time / 60) > 0) // if at least 1 hour
		{
			announce += (time / 60) + " hour(s)";
			if (minutes > 0)
			{
				announce += " and " + minutes + " minute(s)";
			}
		}
		else if (minutes > 0)
		{
			announce += minutes + " minute(s)";
		}
		if (announce.isEmpty())
		{
			announce = "!wrong time set, check your settings!";
		}
		return announce;
	}
	
	public static void main(String[] args)
	{
		new SpawnNPC(-1, "SpawnNPC", "custom");
	}
}