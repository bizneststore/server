/*
 * Copyright (C) 2004-2015 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.itemhandlers;

import java.util.Calendar;
import java.util.List;

import l2r.gameserver.data.xml.impl.ItemData;
import l2r.gameserver.handler.IItemHandler;
import l2r.gameserver.model.actor.L2Playable;
import l2r.gameserver.model.holders.SkillHolder;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.items.type.ActionType;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.model.skills.L2SkillType;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.MagicSkillUse;
import l2r.gameserver.network.serverpackets.SystemMessage;

import gr.sr.premiumEngine.PremiumDuration;
import gr.sr.premiumEngine.PremiumHandler;
import gr.sr.rankEngine.templates.Ranking;

/**
 * Template for item skills handler.
 * @author Zoey76
 */
public class ItemSkillsTemplate implements IItemHandler
{
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse)
	{
		if (!playable.isPlayer() && !playable.isPet())
		{
			return false;
		}
		
		// Pets can use items only when they are tradable.
		if (playable.isPet() && !item.isTradeable())
		{
			playable.sendPacket(SystemMessageId.ITEM_NOT_FOR_PETS);
			return false;
		}
		
		// Verify that item is not under reuse.
		if (!checkReuse(playable, null, item))
		{
			return false;
		}
		
		final List<SkillHolder> skills = item.getItem().getSkills();
		if (skills.isEmpty())
		{
			_log.info("Item " + item + " does not have registered any skill for handler.");
			return false;
		}
		
		boolean hasConsumeSkill = false;
		boolean isSimultaniously = false;
		for (SkillHolder skillInfo : skills)
		{
			if (skillInfo == null)
			{
				continue;
			}
			
			L2Skill itemSkill = skillInfo.getSkill();
			if (itemSkill != null)
			{
				if ((itemSkill.getItemConsumeId() == 0) && (itemSkill.getItemConsumeCount() > 0))
				{
					hasConsumeSkill = true;
				}
				
				if (itemSkill.isSimultaneousCast())
				{
					isSimultaniously = true;
				}
				
				if (!itemSkill.checkCondition(playable, playable.getTarget(), false))
				{
					return false;
				}
				
				if (playable.isSkillDisabled(itemSkill))
				{
					checkReuse(playable, itemSkill, item);
					return false;
				}
				
				// Verify that skill is not under reuse.
				if (!checkReuse(playable, itemSkill, item))
				{
					return false;
				}
				
				if (!item.isPotion() && !item.isElixir() && !item.isScroll() && playable.isCastingNow() && !item.getItem().hasImmediateEffect() && !item.getItem().hasExImmediateEffect())
				{
					return false;
				}
				
				// Send message to the master.
				if (playable.isPet())
				{
					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.PET_USES_S1);
					sm.addSkillName(itemSkill);
					playable.sendPacket(sm);
				}
				
				// Add a new BSOE used to the stats
				if (itemSkill.getName().contains("Blessed") && (itemSkill.getSkillType() == L2SkillType.RECALL))
				{
					playable.getActingPlayer().addPlayerStats(Ranking.STAT_TOP_BSOES_USED);
				}
				
				// vmilon premium scrolls
				if ((itemSkill.getId() == 10013) && !playable.getActingPlayer().isPlatinum())
				{
					switch (itemSkill.getLevel())
					{
						case 1:
							PremiumHandler.addPremiumServices(1, playable.getActingPlayer().getAccountName(), PremiumDuration.WEEKS, playable.getActingPlayer());
							playable.getActingPlayer().sendMessageS("You have added 1 week of gold account!", 5);
							playable.getActingPlayer().broadcastPacket(new MagicSkillUse(playable.getActingPlayer(), 6463, 1, 1000, 0));
							break;
						case 2:
							PremiumHandler.addPremiumServices(2, playable.getActingPlayer().getAccountName(), PremiumDuration.WEEKS, playable.getActingPlayer());
							playable.getActingPlayer().sendMessageS("You have added 4 weeks of gold account!", 5);
							playable.getActingPlayer().broadcastPacket(new MagicSkillUse(playable.getActingPlayer(), 6463, 1, 1000, 0));
							break;
						case 3:
							PremiumHandler.addPremiumServices(4, playable.getActingPlayer().getAccountName(), PremiumDuration.WEEKS, playable.getActingPlayer());
							playable.getActingPlayer().sendMessageS("You have added 4 weeks of gold account!", 5);
							playable.getActingPlayer().broadcastPacket(new MagicSkillUse(playable.getActingPlayer(), 6463, 1, 1000, 0));
							break;
					}
					
				}
				else if ((itemSkill.getId() == 10013) && playable.getActingPlayer().isPlatinum())
				{
					playable.getActingPlayer().sendMessage("You are a platinum member. Platinum members cannot downgrade!");
					return false;
				}
				if (itemSkill.getId() == 10014)
				{
					switch (itemSkill.getLevel())
					{
						case 1:
							PremiumHandler.addPlatinumServices(1, playable.getActingPlayer().getAccountName(), PremiumDuration.WEEKS, playable.getActingPlayer());
							playable.getActingPlayer().sendMessageS("You have added 1 week of platinum account!", 5);
							playable.getActingPlayer().broadcastPacket(new MagicSkillUse(playable.getActingPlayer(), 6463, 1, 1000, 0));
							break;
						case 2:
							PremiumHandler.addPlatinumServices(2, playable.getActingPlayer().getAccountName(), PremiumDuration.WEEKS, playable.getActingPlayer());
							playable.getActingPlayer().sendMessageS("You have added 2 weeks of platinum account!", 5);
							playable.getActingPlayer().broadcastPacket(new MagicSkillUse(playable.getActingPlayer(), 6463, 1, 1000, 0));
							break;
						case 3:
							PremiumHandler.addPlatinumServices(4, playable.getActingPlayer().getAccountName(), PremiumDuration.WEEKS, playable.getActingPlayer());
							playable.getActingPlayer().sendMessageS("You have added 4 weeks of platinum account!", 5);
							playable.getActingPlayer().broadcastPacket(new MagicSkillUse(playable.getActingPlayer(), 6463, 1, 1000, 0));
							break;
					}
					
				}
				
				if (itemSkill.getReuseDelay() > 0)
				{
					playable.addTimeStamp(itemSkill, itemSkill.getReuseDelay());
				}
				else if (itemSkill.isReuseDaily())
				{
					Calendar reUse = Calendar.getInstance();
					reUse.set(Calendar.HOUR_OF_DAY, 6);
					reUse.set(Calendar.MINUTE, 30);
					long reUseDelay = reUse.getTimeInMillis();
					if (reUseDelay < System.currentTimeMillis())
					{
						reUseDelay += 86400000;
					}
					playable.addTimeStamp(itemSkill, reUseDelay - System.currentTimeMillis());
				}
				
				if (itemSkill.isSimultaneousCast() || ((item.getItem().hasImmediateEffect() || item.getItem().hasExImmediateEffect()) && itemSkill.isStatic()))
				{
					playable.doSimultaneousCast(itemSkill);
					if (item.getItem().hasExImmediateEffect())
					{
						ItemData.getInstance().destroyItem("Consume", item, playable.getActingPlayer(), null);
					}
				}
				else
				{
					if (!playable.useMagic(itemSkill, forceUse, false))
					{
						return false;
					}
				}
			}
		}
		
		final boolean isCapsuleItem = (item.getItem().getDefaultAction() == ActionType.CAPSULE) || item.getItem().mustConsume();
		if (isCapsuleItem || (hasConsumeSkill && (item.isPotion() || item.isElixir() || item.isScroll() || isSimultaniously)))
		{
			if (!playable.destroyItem("Consume", item.getObjectId(), 1, playable, false))
			{
				playable.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * @param playable the character using the item or skill
	 * @param skill the skill being used, can be null
	 * @param item the item being used
	 * @return {@code true} if the the item or skill to check is available, {@code false} otherwise
	 */
	private boolean checkReuse(L2Playable playable, L2Skill skill, L2ItemInstance item)
	{
		final long remainingTime = (skill != null) ? playable.getSkillRemainingReuseTime(skill.getReuseHashCode()) : playable.getItemRemainingReuseTime(item.getObjectId());
		final boolean isAvailable = remainingTime <= 0;
		final boolean isDailyReuse = skill != null ? skill.isReuseDaily() : false;
		
		if (playable.isPlayer())
		{
			if (!isAvailable)
			{
				final int hours = (int) (remainingTime / 3600000L);
				final int minutes = (int) (remainingTime % 3600000L) / 60000;
				final int seconds = (int) ((remainingTime / 1000) % 60);
				SystemMessage sm = null;
				
				if (isDailyReuse)
				{
					if (hours > 0)
					{
						sm = SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_S2_HOURS_S3_MINUTES_S4_SECONDS_REMAINING_FOR_S1_REUSE_TIME);
						sm.addItemName(item);
						sm.addInt(hours);
						sm.addInt(minutes);
					}
					else if (minutes > 0)
					{
						sm = SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_FOR_S1_REUSE_TIME);
						sm.addItemName(item);
						sm.addInt(minutes);
					}
					else
					{
						sm = SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_S2_SECONDS_REMAINING_FOR_S1_REUSE_TIME);
						sm.addItemName(item);
					}
				}
				else
				{
					if (hours > 0)
					{
						sm = SystemMessage.getSystemMessage(SystemMessageId.S2_HOURS_S3_MINUTES_S4_SECONDS_REMAINING_FOR_REUSE_S1);
						if ((skill == null) || skill.isStatic())
						{
							sm.addItemName(item);
						}
						else
						{
							sm.addSkillName(skill);
						}
						sm.addInt(hours);
						sm.addInt(minutes);
					}
					else if (minutes > 0)
					{
						sm = SystemMessage.getSystemMessage(SystemMessageId.S2_MINUTES_S3_SECONDS_REMAINING_FOR_REUSE_S1);
						if ((skill == null) || skill.isStatic())
						{
							sm.addItemName(item);
						}
						else
						{
							sm.addSkillName(skill);
						}
						sm.addInt(minutes);
					}
					else
					{
						sm = SystemMessage.getSystemMessage(SystemMessageId.S2_SECONDS_REMAINING_FOR_REUSE_S1);
						if ((skill == null) || skill.isStatic())
						{
							sm.addItemName(item);
						}
						else
						{
							sm.addSkillName(skill);
						}
					}
				}
				
				sm.addInt(seconds);
				playable.sendPacket(sm);
			}
		}
		return isAvailable;
	}
}
