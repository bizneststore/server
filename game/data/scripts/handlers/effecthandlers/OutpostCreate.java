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
import l2r.gameserver.data.sql.NpcTable;
import l2r.gameserver.instancemanager.TerritoryWarManager;
import l2r.gameserver.model.L2Clan;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.instance.L2SiegeFlagInstance;
import l2r.gameserver.model.effects.EffectInstant;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.stats.Env;

/**
 * Outpost Create effect implementation.
 * @author UnAfraid
 */
public class OutpostCreate extends EffectInstant
{
	private static final int HQ_NPC_ID = 36590;
	
	public OutpostCreate(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public boolean onStart()
	{
		final L2PcInstance player = getEffector().getActingPlayer();
		if (!player.isClanLeader())
		{
			return false;
		}
		
		final L2Clan clan = player.getClan();
		
		if (clan.getCastleId() <= 0)
		{
			return false;
		}
		
		if (TerritoryWarManager.getInstance().hasHQ(clan))
		{
			return false;
		}
		
		if (TerritoryWarManager.getInstance().isTWInProgress())
		{
			// Spawn a new flag
			final L2SiegeFlagInstance flag = new L2SiegeFlagInstance(player, NpcTable.getInstance().getTemplate(HQ_NPC_ID), true, true);
			flag.setTitle(player.getClan().getName());
			flag.setCurrentHpMp(flag.getMaxHp(), flag.getMaxMp());
			flag.setHeading(player.getHeading());
			flag.spawnMe(player.getX(), player.getY(), player.getZ() + Config.CLIENT_SHIFTZ);
			TerritoryWarManager.getInstance().setHQForClan(player.getClan(), flag);
		}
		return true;
	}
}
