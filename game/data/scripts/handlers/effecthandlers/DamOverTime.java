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
package handlers.effecthandlers;

import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.effects.L2EffectType;
import l2r.gameserver.model.effects.OverTimeEffect;
import l2r.gameserver.model.skills.TickManager;
import l2r.gameserver.model.stats.Env;
import l2r.gameserver.network.SystemMessageId;

public class DamOverTime extends OverTimeEffect
{
	private final boolean _canKill;
	
	public DamOverTime(Env env, EffectTemplate template)
	{
		super(env, template);
		
		_canKill = template.getParameters().getBoolean("canKill", false);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.DMG_OVER_TIME;
	}
	
	@Override
	public boolean onStart()
	{
		onTick();
		
		return super.onStart();
	}
	
	@Override
	public boolean onTick()
	{
		if (getEffected().isDead())
		{
			return false;
		}
		
		double damage = getValue() * getTicksMultiplier();
		
		if (damage >= (getEffected().getCurrentHp() - 1))
		{
			if (getSkill().isToggle())
			{
				getEffected().sendPacket(SystemMessageId.SKILL_REMOVED_DUE_LACK_HP);
				return false;
			}
			
			// For DOT skills that will not kill effected player.
			if (!_canKill)
			{
				// Fix for players dying by DOTs if HP < 1 since reduceCurrentHP method will kill them
				if (getEffected().getCurrentHp() <= 1)
				{
					TickManager.getInstance().addEffectPerTickTask(getSkill(), this);
					return true;
				}
				
				damage = getEffected().getCurrentHp() - 1;
			}
		}
		getEffected().reduceCurrentHpByDOT(damage, getEffector(), getSkill());
		getEffected().notifyDamageReceived(damage, getEffector(), getSkill(), false, true, false);
		
		TickManager.getInstance().addEffectPerTickTask(getSkill(), this);
		return true;
	}
}
