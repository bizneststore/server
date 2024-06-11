package custom.SpawnNPC;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.data.sql.CharNameTable;
import l2r.gameserver.instancemanager.QuestManager;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.network.serverpackets.QuestList;

public class npc extends Quest
{
	// NPC ID
	private static final int CUSTOM_NPC_ID = 576;
	// List of Quest IDs
	private static final List<Integer> QUEST_IDS = new ArrayList<>();
	
	static
	{
		QUEST_IDS.add(10600); // GatherXItems
		// Add more quest IDs here
	}
	
	public npc()
	{
		super(-1, npc.class.getSimpleName(), "custom");
		addSpawnId(CUSTOM_NPC_ID);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		ThreadPoolManager.getInstance().scheduleGeneral(() ->
		{
			// Randomly select a quest ID from the list
			Random random = new Random();
			int randomIndex = random.nextInt(QUEST_IDS.size());
			int selectedQuestId = QUEST_IDS.get(randomIndex);
			
			// Fetch all character IDs from the database
			for (int charId : CharNameTable.getInstance().getAllCharIds())
			{
				L2PcInstance player = L2PcInstance.load(charId);
				if (player != null)
				{
					Quest q = QuestManager.getInstance().getQuest(selectedQuestId);
					if (q != null)
					{
						QuestState st = player.getQuestState(q.getName());
						if (st == null)
						{
							st = q.newQuestState(player);
							// Set condition to 1 and broadcast the change
							st.startQuest();
						}
						// If the player is online, send the updated quest list
						if (player.isOnline())
						{
							player.sendPacket(new QuestList());
						}
					}
				}
			}
		} , 1000); // Delay to ensure NPC is fully spawned
		
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new npc();
	}
}
