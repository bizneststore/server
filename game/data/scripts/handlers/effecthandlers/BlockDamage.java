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

import l2r.gameserver.model.effects.EffectFlag;
import l2r.gameserver.model.effects.EffectPump;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.effects.L2EffectType;
import l2r.gameserver.model.stats.Env;

/**
 * @author Zealar
 */
public final class BlockDamage extends EffectPump
{
	public enum BlockType
	{
		HP,
		MP
	}
	
	private final BlockType _type;
	
	public BlockDamage(Env env, EffectTemplate template)
	{
		super(env, template);
		_type = template.getParameters().getEnum("block", BlockType.class, BlockType.HP);
	}
	
	@Override
	public int getEffectFlags()
	{
		return _type == BlockType.HP ? EffectFlag.BLOCK_HP.getMask() : EffectFlag.BLOCK_MP.getMask();
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.BLOCK_DAMAGE;
	}
}
