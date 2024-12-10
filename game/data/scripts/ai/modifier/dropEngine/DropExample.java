package ai.modifier.dropEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import l2r.Config;
import l2r.gameserver.data.sql.NpcTable;
import l2r.gameserver.model.actor.L2Attackable;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.instance.L2RaidBossInstance;
import l2r.gameserver.model.actor.templates.L2NpcTemplate;

import ai.npc.AbstractNpcAI;

public final class DropExample extends AbstractNpcAI
{
	private static List<DropDetails> DROP_DATA = new ArrayList<>();
	final Set<Integer> npcIds = new HashSet<>();
	private static boolean USE_DEEP_BLUE = true;
	private static boolean USE_DEEP_BLUE_ON_RAIDS = true;
	private static int MAX_PARTY_MEMBER_LVL_DIFF = 5;
	
	public DropExample()
	{
		super(DropExample.class.getSimpleName(), "ai/modifier/dropEngine");
		
		fillDrops();
		fillNpcIds();
		
		addKillId(npcIds);
	}
	
	private void fillDrops()
	{
		
		// 1-20 LEVEL
		DROP_DATA.add(new DropDetails(1, 20, new DropItem(1871, 1, 3, 10)));
		DROP_DATA.add(new DropDetails(1, 20, new DropItem(1872, 1, 3, 15)));
		DROP_DATA.add(new DropDetails(1, 20, new DropItem(1868, 1, 3, 20)));
		DROP_DATA.add(new DropDetails(1, 20, new DropItem(1870, 1, 3, 10)));
		DROP_DATA.add(new DropDetails(1, 20, new DropItem(1878, 1, 3, 10)));
		
		// 21-40 LEVEL
		DROP_DATA.add(new DropDetails(21, 40, new DropItem(1871, 2, 4, 15)));
		DROP_DATA.add(new DropDetails(21, 40, new DropItem(1872, 2, 4, 15)));
		DROP_DATA.add(new DropDetails(21, 40, new DropItem(1868, 2, 4, 15)));
		DROP_DATA.add(new DropDetails(21, 40, new DropItem(1870, 2, 4, 15)));
		DROP_DATA.add(new DropDetails(21, 40, new DropItem(1878, 2, 4, 10)));
		DROP_DATA.add(new DropDetails(21, 40, new DropItem(1865, 2, 5, 15)));
		DROP_DATA.add(new DropDetails(21, 40, new DropItem(1876, 2, 5, 15)));
		DROP_DATA.add(new DropDetails(21, 40, new DropItem(1880, 1, 3, 10)));
		DROP_DATA.add(new DropDetails(21, 40, new DropItem(1869, 2, 4, 15)));
		DROP_DATA.add(new DropDetails(21, 40, new DropItem(1895, 2, 4, 10)));
		DROP_DATA.add(new DropDetails(21, 40, new DropItem(5549, 2, 5, 15)));
		DROP_DATA.add(new DropDetails(21, 40, new DropItem(1889, 2, 4, 10)));
		DROP_DATA.add(new DropDetails(21, 40, new DropItem(1894, 2, 4, 5)));
		DROP_DATA.add(new DropDetails(21, 40, new DropItem(1881, 2, 4, 5)));
		DROP_DATA.add(new DropDetails(21, 40, new DropItem(1884, 1, 3, 10)));
		
		// 41-60 LEVEL
		DROP_DATA.add(new DropDetails(41, 60, new DropItem(1871, 3, 6, 5)));
		DROP_DATA.add(new DropDetails(41, 60, new DropItem(1872, 3, 6, 10)));
		DROP_DATA.add(new DropDetails(41, 60, new DropItem(1868, 3, 6, 5)));
		DROP_DATA.add(new DropDetails(41, 60, new DropItem(1870, 3, 6, 15)));
		DROP_DATA.add(new DropDetails(41, 60, new DropItem(1878, 3, 6, 10)));
		DROP_DATA.add(new DropDetails(41, 60, new DropItem(1865, 2, 5, 20)));
		DROP_DATA.add(new DropDetails(41, 60, new DropItem(1876, 2, 5, 20)));
		DROP_DATA.add(new DropDetails(41, 60, new DropItem(1880, 1, 3, 15)));
		DROP_DATA.add(new DropDetails(41, 60, new DropItem(1869, 3, 6, 20)));
		DROP_DATA.add(new DropDetails(41, 60, new DropItem(1895, 3, 6, 10)));
		DROP_DATA.add(new DropDetails(41, 60, new DropItem(5549, 2, 5, 15)));
		DROP_DATA.add(new DropDetails(41, 60, new DropItem(1889, 3, 6, 10)));
		DROP_DATA.add(new DropDetails(41, 60, new DropItem(1894, 2, 5, 15)));
		DROP_DATA.add(new DropDetails(41, 60, new DropItem(1881, 2, 5, 10)));
		DROP_DATA.add(new DropDetails(41, 60, new DropItem(1884, 2, 5, 15)));
		DROP_DATA.add(new DropDetails(41, 60, new DropItem(1879, 2, 5, 15)));
		DROP_DATA.add(new DropDetails(41, 60, new DropItem(4043, 1, 3, 5)));
		DROP_DATA.add(new DropDetails(41, 60, new DropItem(4042, 1, 3, 15)));
		DROP_DATA.add(new DropDetails(41, 60, new DropItem(1877, 1, 3, 5)));
		DROP_DATA.add(new DropDetails(41, 60, new DropItem(4044, 1, 3, 5)));
		DROP_DATA.add(new DropDetails(41, 60, new DropItem(4039, 1, 3, 5)));
		DROP_DATA.add(new DropDetails(41, 60, new DropItem(4040, 1, 3, 5)));
		DROP_DATA.add(new DropDetails(41, 60, new DropItem(4041, 1, 3, 5)));
		DROP_DATA.add(new DropDetails(41, 60, new DropItem(1874, 1, 3, 2)));
		DROP_DATA.add(new DropDetails(41, 60, new DropItem(5550, 1, 3, 2)));
		
		// 61-75
		
		DROP_DATA.add(new DropDetails(61, 75, new DropItem(1872, 3, 6, 5)));
		
		DROP_DATA.add(new DropDetails(61, 75, new DropItem(1870, 3, 6, 10)));
		
		DROP_DATA.add(new DropDetails(61, 75, new DropItem(1865, 2, 5, 20)));
		DROP_DATA.add(new DropDetails(61, 75, new DropItem(1876, 2, 5, 20)));
		
		DROP_DATA.add(new DropDetails(61, 75, new DropItem(1869, 3, 6, 20)));
		DROP_DATA.add(new DropDetails(61, 75, new DropItem(1895, 3, 6, 10)));
		DROP_DATA.add(new DropDetails(61, 75, new DropItem(5549, 2, 5, 15)));
		DROP_DATA.add(new DropDetails(61, 75, new DropItem(1889, 3, 6, 10)));
		
		DROP_DATA.add(new DropDetails(61, 75, new DropItem(1884, 2, 5, 15)));
		DROP_DATA.add(new DropDetails(61, 75, new DropItem(1879, 2, 5, 15)));
		
		DROP_DATA.add(new DropDetails(61, 75, new DropItem(4039, 1, 3, 10)));
		DROP_DATA.add(new DropDetails(61, 75, new DropItem(4040, 1, 3, 10)));
		DROP_DATA.add(new DropDetails(61, 75, new DropItem(4041, 1, 3, 10)));
		DROP_DATA.add(new DropDetails(61, 75, new DropItem(1874, 1, 3, 10)));
		DROP_DATA.add(new DropDetails(61, 75, new DropItem(5550, 1, 3, 10)));
		
		// 76-85
		
		DROP_DATA.add(new DropDetails(76, 85, new DropItem(1872, 3, 6, 2)));
		DROP_DATA.add(new DropDetails(76, 85, new DropItem(1870, 3, 6, 5)));
		DROP_DATA.add(new DropDetails(76, 85, new DropItem(1865, 2, 5, 15)));
		DROP_DATA.add(new DropDetails(76, 85, new DropItem(1876, 2, 5, 15)));
		DROP_DATA.add(new DropDetails(76, 85, new DropItem(1869, 3, 6, 10)));
		DROP_DATA.add(new DropDetails(76, 85, new DropItem(1895, 3, 6, 5)));
		DROP_DATA.add(new DropDetails(76, 85, new DropItem(5549, 2, 5, 10)));
		DROP_DATA.add(new DropDetails(76, 85, new DropItem(1889, 3, 6, 2)));
		DROP_DATA.add(new DropDetails(76, 85, new DropItem(1884, 2, 5, 10)));
		DROP_DATA.add(new DropDetails(76, 85, new DropItem(1879, 2, 5, 10)));
		DROP_DATA.add(new DropDetails(76, 85, new DropItem(4039, 1, 3, 15)));
		DROP_DATA.add(new DropDetails(76, 85, new DropItem(4040, 1, 3, 15)));
		DROP_DATA.add(new DropDetails(76, 85, new DropItem(4041, 1, 3, 15)));
		DROP_DATA.add(new DropDetails(76, 85, new DropItem(1874, 1, 3, 15)));
		DROP_DATA.add(new DropDetails(76, 85, new DropItem(5550, 1, 3, 15)));
		DROP_DATA.add(new DropDetails(76, 85, new DropItem(9629, 1, 3, 10)));
		
		// MIXED DROPS
		DROP_DATA.add(new DropDetails(61, 85, new DropItem(1871, 3, 6, 2)));
		DROP_DATA.add(new DropDetails(61, 85, new DropItem(1868, 3, 6, 2)));
		DROP_DATA.add(new DropDetails(61, 85, new DropItem(1878, 3, 6, 2)));
		DROP_DATA.add(new DropDetails(61, 85, new DropItem(1880, 1, 3, 15)));
		DROP_DATA.add(new DropDetails(61, 85, new DropItem(1894, 2, 5, 20)));
		DROP_DATA.add(new DropDetails(61, 85, new DropItem(1881, 2, 5, 20)));
		DROP_DATA.add(new DropDetails(61, 85, new DropItem(4043, 1, 3, 15)));
		DROP_DATA.add(new DropDetails(61, 85, new DropItem(4042, 1, 3, 15)));
		DROP_DATA.add(new DropDetails(61, 85, new DropItem(1877, 1, 3, 10)));
		DROP_DATA.add(new DropDetails(61, 85, new DropItem(4044, 1, 3, 10)));
		
	}
	
	private void fillNpcIds()
	{
		for (DropDetails details : DROP_DATA)
		{
			// Just in case there are no drops
			if (details != null)
			{
				if (!details.isById())
				{
					for (int level = details.getMinLevel(); level <= details.getMaxLevel(); level++)
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
				else
				{
					final L2NpcTemplate template = NpcTable.getInstance().getTemplate(details.getMonsterId());
					if (template != null)
					{
						npcIds.add(template.getId());
					}
				}
			}
		}
	}
	
	@Override
	public final String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		if (npc.isAttackable())
		{
			final L2Attackable monster = (L2Attackable) npc;
			
			// RAIDBOSS EXTRA DROPS (vmilon)
			if (npc instanceof L2RaidBossInstance)
			{
				
				// ADDITIONAL DROPS
				List<DropDetails> additionalDrops = new ArrayList<>();
				additionalDrops.add(new DropDetails(49, 58, new DropItem(9876, 1, 2, 70))); // B GRADE FRAGMENTS
				additionalDrops.add(new DropDetails(59, 75, new DropItem(5432, 1, 1, 50))); // Example additional drop
				additionalDrops.add(new DropDetails(72, 80, new DropItem(5432, 1, 1, 50))); // Example additional drop
				additionalDrops.add(new DropDetails(78, 83, new DropItem(5432, 1, 1, 50))); // Example additional drop
				additionalDrops.add(new DropDetails(80, 85, new DropItem(5432, 1, 1, 50))); // Example additional drop
				
				for (DropDetails dd : additionalDrops)
				{
					for (DropItem di : dd.getDrops())
					{
						int dropCount = getRandom(di.getMinCount(), di.getMaxCount());
						rewardPlayer(player, npc, di.getItemId(), dropCount, di.getDropChance());
					}
				}
			}
			
			List<DropDetails> applicableDrops = new ArrayList<>();
			for (DropDetails dd : DROP_DATA)
			{
				if ((dd.isById() && (monster.getId() == dd.getMonsterId())) || (!dd.isById() && (monster.getLevel() >= dd.getMinLevel()) && (monster.getLevel() <= dd.getMaxLevel())))
				{
					applicableDrops.add(dd);
				}
			}
			
			// Limit to a maximum of 5 drops
			if (!applicableDrops.isEmpty())
			{
				// Shuffle list
				Collections.shuffle(applicableDrops);
				
				List<DropDetails> selectedDrops = applicableDrops.subList(0, Math.min(applicableDrops.size(), 5));
				
				for (DropDetails dd : selectedDrops)
				{
					for (DropItem di : dd.getDrops())
					{
						int dropCount = getRandom(di.getMinCount(), di.getMaxCount());
						rewardPlayer(player, npc, di.getItemId(), dropCount, di.getDropChance());
					}
				}
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	private void rewardPlayer(L2PcInstance player, L2Character object, int itemId, long itemCount, int chance)
	{
		if (!(object instanceof L2Attackable))
		{
			return;
		}
		L2Attackable npc = (L2Attackable) object;
		int dropChance = chance;
		int levelDiff = 0;
		boolean giveReward = true;
		int maxLevelDiffForRaidDrop = 8;
		int maxLevelDiffForMonstersDrop = 8;
		
		if ((player.getParty() == null) && (npc.getLevel() < player.getLevel()) && ((player.getLevel() - npc.getLevel()) > (npc.isRaid() ? maxLevelDiffForRaidDrop : maxLevelDiffForMonstersDrop)))
		{
			return;
		}
		else if ((player.getParty() != null))
		{
			for (L2PcInstance member : player.getParty().getMembers())
			{
				if ((npc.getLevel() < member.getLevel()) && ((member.getLevel() - npc.getLevel()) > (npc.isRaid() ? maxLevelDiffForRaidDrop : maxLevelDiffForMonstersDrop)))
				{
					return;
				}
			}
		}
		
		if ((!npc.isRaid() && USE_DEEP_BLUE) || (npc.isRaid() && USE_DEEP_BLUE_ON_RAIDS))
		{
			if (player.isInParty())
			{
				int tempLvlDiff = 0;
				int levelMod = 0;
				for (L2PcInstance member : player.getParty().getMembers())
				{
					tempLvlDiff = calculateLevelModifierForDrop(npc, member);
					levelMod = player.getLevel() - member.getLevel();
					levelMod = levelMod < 0 ? (levelMod * -1) : levelMod;
					if (levelMod >= MAX_PARTY_MEMBER_LVL_DIFF)
					{
						giveReward = false;
					}
					
					if (levelDiff < tempLvlDiff)
					{
						levelDiff = tempLvlDiff;
					}
				}
			}
			else
			{
				levelDiff = calculateLevelModifierForDrop(npc, player);
			}
			
			// Check if we should apply our maths so deep blue mobs will not drop that easy
			dropChance = ((dropChance - ((dropChance * levelDiff) / 100)));
		}
		
		if (giveReward && (getRandom(100) < dropChance))
		{
			if (player.isPremium())
			{
				long tempCount = itemCount;
				tempCount *= player.calcPremiumDropMultipliers(itemId);
				if ((npc.isRaid() && Config.AUTO_LOOT_RAIDS) || (!npc.isRaid() && Config.AUTO_LOOT))
				{
					player.addItem("drop", itemId, tempCount, player, true);
				}
				else
				{
					npc.dropItem(player, itemId, tempCount);
				}
			}
			else
			{
				if ((npc.isRaid() && Config.AUTO_LOOT_RAIDS) || (!npc.isRaid() && Config.AUTO_LOOT))
				{
					player.addItem("drop", itemId, itemCount, player, true);
				}
				else
				{
					npc.dropItem(player, itemId, itemCount);
				}
			}
		}
	}
	
	private int calculateLevelModifierForDrop(L2Character target, L2PcInstance lastAttacker)
	{
		if ((!target.isRaid() && USE_DEEP_BLUE) || (target.isRaid() && USE_DEEP_BLUE_ON_RAIDS))
		{
			int highestLevel = lastAttacker.getLevel();
			
			// Check to prevent very high level player to nearly kill mob and let low level player do the last hit.
			if (!target.getAttackByList().isEmpty())
			{
				for (L2Character atkChar : target.getAttackByList())
				{
					if ((atkChar != null) && (atkChar.getLevel() > highestLevel))
					{
						highestLevel = atkChar.getLevel();
					}
				}
			}
			
			// According to official data (Prima), deep blue mobs are 9 or more levels below players
			if ((highestLevel - 9) >= target.getLevel())
			{
				return ((highestLevel - (target.getLevel() + 8)) * 9);
			}
		}
		return 0;
	}
	
	private static class DropDetails
	{
		private final int _monsterId;
		private final int _minLevel;
		private final int _maxLevel;
		private final boolean _isById;
		private final List<DropItem> _itemDrops = new ArrayList<>();
		
		public DropDetails(int minLevel, int maxLevel, DropItem... drops)
		{
			_minLevel = minLevel;
			_maxLevel = maxLevel;
			_monsterId = 0;
			_isById = false;
			for (DropItem drop : drops)
			{
				_itemDrops.add(drop);
			}
		}
		
		@SuppressWarnings("unused")
		public DropDetails(int monsterId, DropItem... drops)
		{
			_minLevel = 0;
			_maxLevel = 0;
			_monsterId = monsterId;
			_isById = true;
			for (DropItem drop : drops)
			{
				_itemDrops.add(drop);
			}
		}
		
		public boolean isById()
		{
			return _isById;
		}
		
		public int getMonsterId()
		{
			return _monsterId;
		}
		
		public int getMinLevel()
		{
			return _minLevel;
		}
		
		public int getMaxLevel()
		{
			return _maxLevel;
		}
		
		public List<DropItem> getDrops()
		{
			return _itemDrops;
		}
	}
	
	private static class DropItem
	{
		private final int _itemId;
		private final int _minCount;
		private final int _maxCount;
		private final int _dropChance;
		
		public DropItem(int itemId, int minCount, int maxCount, int dropchance)
		{
			_itemId = itemId;
			_minCount = minCount;
			_maxCount = maxCount;
			_dropChance = dropchance;
		}
		
		public int getItemId()
		{
			return _itemId;
		}
		
		public int getMinCount()
		{
			return _minCount;
		}
		
		public int getMaxCount()
		{
			return _maxCount;
		}
		
		public int getDropChance()
		{
			return _dropChance;
		}
	}
	
	public static void main(String[] args)
	{
		new DropExample();
	}
}