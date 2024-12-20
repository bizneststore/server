/*
 * Copyright (C) 2004-2013 L2J DataPack
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
package handlers.admincommandhandlers;

import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.handler.IAdminCommandHandler;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.SystemMessageId;

/**
 * @author
 */
public class AdminRide implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_ride_horse",
		"admin_ride_bike",
		"admin_ride_wyvern",
		"admin_ride_strider",
		"admin_unride_wyvern",
		"admin_unride_strider",
		"admin_unride",
		"admin_ride_wolf",
		"admin_unride_wolf",
	};
	private int _petRideId;
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		L2PcInstance player = getRideTarget(activeChar);
		if (player == null)
		{
			return false;
		}
		
		if (command.startsWith("admin_ride"))
		{
			if (player.isMounted() || player.hasSummon())
			{
				activeChar.sendMessage("Target already have a summon.");
				return false;
			}
			if (command.startsWith("admin_ride_wyvern"))
			{
				_petRideId = 12621;
			}
			else if (command.startsWith("admin_ride_strider"))
			{
				_petRideId = 12526;
			}
			else if (command.startsWith("admin_ride_wolf"))
			{
				_petRideId = 16041;
			}
			else if (command.startsWith("admin_ride_horse"))
			{
				player.addSkill(SkillData.getInstance().getSkill(8247, 1));
				if (player.isTransformed() || player.isInStance())
				{
					activeChar.sendPacket(SystemMessageId.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
				}
				else
				{
					player.useMagic(SkillData.getInstance().getInfo(8247, 1), true, true);
				}
				
				return true;
			}
			else if (command.startsWith("admin_ride_bike"))
			{
				player.addSkill(SkillData.getInstance().getSkill(21171, 1));
				if (player.isTransformed() || player.isInStance())
				{
					activeChar.sendPacket(SystemMessageId.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
				}
				else
				{
					player.useMagic(SkillData.getInstance().getInfo(21171, 1), true, true);
				}
				
				return true;
			}
			else
			{
				activeChar.sendMessage("Command '" + command + "' not recognized");
				return false;
			}
			
			player.mount(_petRideId, 0, false);
			
			return false;
		}
		else if (command.startsWith("admin_unride"))
		{
			player.dismount();
		}
		return true;
	}
	
	private L2PcInstance getRideTarget(L2PcInstance activeChar)
	{
		L2PcInstance player = null;
		
		if ((activeChar.getTarget() == null) || (activeChar.getTarget().getObjectId() == activeChar.getObjectId()) || !(activeChar.getTarget() instanceof L2PcInstance))
		{
			player = activeChar;
		}
		else
		{
			player = (L2PcInstance) activeChar.getTarget();
		}
		
		return player;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
}
