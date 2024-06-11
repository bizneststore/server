package ai.modifier.dropEngine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import l2r.gameserver.data.sql.NpcTable;
import l2r.gameserver.model.actor.L2Attackable;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.templates.L2NpcTemplate;

import ai.npc.AbstractNpcAI;

public final class DropExample extends AbstractNpcAI
{
	private static List<DropDetails> DROP_DATA = new ArrayList<>();
	final Set<Integer> npcIds = new HashSet<>();
	
	private void fillDrops()
	{
		DROP_DATA.add(new DropDetails(1, 10, new DropItem(57, 100, 1000, 100), new DropItem(1206, 1, 1, 100)));
		DROP_DATA.add(new DropDetails(11, 20, new DropItem(57, 1000, 2600, 100)));
	}
	
	private void fillNpcIds()
	{
		for (DropDetails details : DROP_DATA)
		{
			if (details != null)
			{
				for (int level = details.minLevel; level <= details.maxLevel; level++)
				{
					final List<L2NpcTemplate> templates = NpcTable.getInstance().getAllOfLevel(level);
					if (templates != null)
					{
						for (int i = 0; i < templates.size(); i++)
						{
							if (templates.get(i) != null)
							{
								L2NpcTemplate npc = templates.get(i);
								if (!npcIds.contains(npc.getId()))
								{
									npcIds.add(npc.getId());
								}
							}
						}
					}
				}
			}
		}
	}
	
	public DropExample()
	{
		super(DropExample.class.getSimpleName(), "ai/modifier/dropEngine");
		
		fillDrops();
		fillNpcIds();
		
		addKillId(npcIds);
	}
	
	@Override
	public final String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		if (npc.isAttackable())
		{
			final L2Attackable monster = (L2Attackable) npc;
			// find drop data based on monster's level, can't do it any other way since drop data is stored in a list, not a map
			Optional<DropDetails> dropDetail = DROP_DATA.stream().filter(dd -> (monster.getLevel() >= dd.minLevel) && (monster.getLevel() <= dd.maxLevel)).findFirst();
			// drop details for NPC's level were found, use them
			if (dropDetail.isPresent())
			{
				for (DropItem di : dropDetail.get().getDrops())
				{
					if (getRandom(100) < di.dropChance)
					{
						player.addItem("drop", di.itemId, getRandom(di.minCount, di.maxCount), player, true);
					}
				}
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	private static class DropDetails
	{
		public int minLevel;
		public int maxLevel;
		private final List<DropItem> itemDrops = new ArrayList<>();
		
		public DropDetails(int minLevel, int maxLevel, DropItem... drops)
		{
			this.minLevel = minLevel;
			this.maxLevel = maxLevel;
			
			for (DropItem drop : drops)
			{
				itemDrops.add(drop);
			}
		}
		
		public List<DropItem> getDrops()
		{
			return this.itemDrops;
		}
	}
	
	private static class DropItem
	{
		public final int itemId;
		public final int minCount;
		public final int maxCount;
		public final int dropChance;
		
		public DropItem(int itemId, int minCount, int maxCount, int dropchance)
		{
			this.itemId = itemId;
			this.minCount = minCount;
			this.maxCount = maxCount;
			this.dropChance = dropchance;
		}
	}
	
	public static void main(String[] args)
	{
		new DropExample();
	}
}