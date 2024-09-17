package ai.modifier.dropEngine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import l2r.Config;
import l2r.gameserver.data.sql.NpcTable;
import l2r.gameserver.model.actor.L2Attackable;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.templates.L2NpcTemplate;
import l2r.gameserver.util.Util;

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
		DROP_DATA.add(new DropDetails(1, 5, new DropItem(40007, 10, 10, 100)));
		DROP_DATA.add(new DropDetails(80, 85, new DropItem(40007, 80, 80, 100)));
		DROP_DATA.add(new DropDetails(50, 55, new DropItem(40007, 50, 50, 100)));
		
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
			// find drop data based on monster's level, can't do it any other way since drop data is stored in a list, not a map
			
			for (DropDetails dd : DROP_DATA)
			{
				if ((dd.isById() && (monster.getId() == dd.getMonsterId())) || (!dd.isById() && (monster.getLevel() >= dd.getMinLevel()) && (monster.getLevel() <= dd.getMaxLevel())))
				{
					for (DropItem di : dd.getDrops())
					{
						int dropCount = getRandom(di.getMinCount(), di.getMaxCount());
						if (player.isInParty())
						{
							final List<L2PcInstance> toReward = new LinkedList<>();
							for (L2PcInstance member : player.getParty().getMembers())
							{
								if (Util.checkIfInRange(1400, npc, member, true))
								{
									toReward.add(member);
								}
							}
							
							if (!toReward.isEmpty())
							{
								// Now we can actually distribute the item count for reward
								// (Total item count split by the number of party members that are in range and must be rewarded)
								long count = dropCount / toReward.size();
								for (L2PcInstance member : toReward)
								{
									rewardPlayer(member, npc, di.getItemId(), count, di.getDropChance());
								}
							}
						}
						else
						{
							rewardPlayer(player, npc, di.getItemId(), dropCount, di.getDropChance());
						}
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