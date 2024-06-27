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
package handlers.effecthandlers;

import l2r.Config;
import l2r.gameserver.GeoData;
import l2r.gameserver.data.xml.impl.FishingDataTable;
import l2r.gameserver.instancemanager.ZoneManager;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.effects.EffectInstant;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.effects.L2EffectType;
import l2r.gameserver.model.fishing.FishPlaceType;
import l2r.gameserver.model.fishing.FishingPlace;
import l2r.gameserver.model.itemcontainer.Inventory;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.stats.Env;
import l2r.gameserver.model.zone.L2ZoneType;
import l2r.gameserver.model.zone.type.L2WaterZone;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.util.Util;
import l2r.util.Rnd;

/**
 * Fishing effect implementation.
 * @author UnAfraid
 */
public final class Fishing extends EffectInstant
{
	private static final int MIN_BAIT_DISTANCE = 50;
	private static final int MAX_BAIT_DISTANCE = 150;
	
	public Fishing(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.FISHING_START;
	}
	
	@Override
	public boolean onStart()
	{
		final L2Character activeChar = getEffector();
		if (!activeChar.isPlayer())
		{
			return false;
		}
		
		final L2PcInstance player = activeChar.getActingPlayer();
		
		// calculate a position in front of the player with a random distance
		int distance = Rnd.get(MIN_BAIT_DISTANCE, MAX_BAIT_DISTANCE);
		final double angle = Util.convertHeadingToDegree(player.getHeading());
		final double radian = Math.toRadians(angle);
		final double sin = Math.sin(radian);
		final double cos = Math.cos(radian);
		int baitX = (int) (player.getX() + (cos * distance));
		int baitY = (int) (player.getY() + (sin * distance));
		
		// search for fishing and water zone
		FishingPlace place = null;
		boolean canFish = false;
		boolean hasWaterZ = false;
		
		int baitZ = 0;
		int z = 0;
		
		for (distance = MAX_BAIT_DISTANCE; distance >= MIN_BAIT_DISTANCE; --distance)
		{
			baitX = (int) (player.getX() + (cos * distance));
			baitY = (int) (player.getY() + (sin * distance));
			
			z = GeoData.getInstance().getSpawnHeight(baitX, baitY, player.getZ());
			
			place = FishingDataTable.getInstance().getFishingPlace(baitX, baitY, z);
			if (place != null)
			{
				if (place.fishing_place_type == FishPlaceType.FISHING_PLACE_TYPE2)
				{
					baitZ = z + 5;
				}
				else
				{
					for (final L2ZoneType zone : ZoneManager.getInstance().getZones(baitX, baitY))
					{
						if (zone instanceof L2WaterZone)
						{
							// always use water zone, fishing zone high z is high in the air...
							baitZ = ((L2WaterZone) zone).getWaterZ();
							hasWaterZ = true;
							break;
						}
					}
				}
				
				if (computeBaitZ(player, baitX, baitY, baitZ))
				{
					canFish = true;
					
					if (!hasWaterZ)
					{
						baitZ = place.territory.getLowZ();
					}
					break;
				}
			}
			
			// if position found break
			if (canFish)
			{
				break;
			}
		}
		
		if (!canFish)
		{
			player.sendPacket(SystemMessageId.CANNOT_FISH_HERE);
			return false;
		}
		
		// check for equiped lure
		final L2ItemInstance equipedLeftHand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
		
		if (!player.destroyItem("Fishing", equipedLeftHand, 1, null, false))
		{
			player.sendPacket(SystemMessageId.NOT_ENOUGH_BAIT);
			return false;
		}
		
		player.getFishingEx().startFishing(place, baitX, baitY, baitZ, equipedLeftHand.getId());
		return true;
	}
	
	/**
	 * Computes the Z of the bait.
	 * @param player the player
	 * @param baitX the bait x
	 * @param baitY the bait y
	 * @param baitZ
	 * @return the bait z or {@link Integer#MIN_VALUE} when you cannot fish here
	 */
	private static boolean computeBaitZ(final L2PcInstance player, final int baitX, final int baitY, final int baitZ)
	{
		if (!GeoData.getInstance().canSeeTarget(player, baitX, baitY, baitZ))
		{
			return false;
		}
		
		if (Config.GEODATA)
		{
			if (GeoData.getInstance().getHeight(baitX, baitY, baitZ) > baitZ)
			{
				return false;
			}
			
			if (GeoData.getInstance().getHeight(baitX, baitY, player.getZ()) > baitZ)
			{
				return false;
			}
		}
		
		return true;
	}
}
