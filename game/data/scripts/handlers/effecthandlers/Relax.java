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

import l2r.gameserver.enums.CtrlIntention;
import l2r.gameserver.model.effects.EffectFlag;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.effects.L2EffectType;
import l2r.gameserver.model.effects.OverTimeEffect;
import l2r.gameserver.model.skills.TickManager;
import l2r.gameserver.model.stats.Env;
import l2r.gameserver.network.SystemMessageId;

public class Relax extends OverTimeEffect
{
	public Relax(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.RELAXING;
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected().isPlayer())
		{
			getEffected().getActingPlayer().sitDown(false);
		}
		else
		{
			getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_REST);
		}
		
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
		
		if (getEffected().isPlayer())
		{
			if (!getEffected().getActingPlayer().isSitting())
			{
				return false;
			}
		}
		
		if ((getEffected().getCurrentHp() + 1) > getEffected().getMaxRecoverableHp())
		{
			if (getSkill().isToggle())
			{
				getEffected().sendPacket(SystemMessageId.SKILL_DEACTIVATED_HP_FULL);
				return false;
			}
		}
		
		double manaDam = getValue() * getTicksMultiplier();
		
		if (manaDam > getEffected().getCurrentMp())
		{
			if (getSkill().isToggle())
			{
				getEffected().sendPacket(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP);
				return false;
			}
		}
		
		getEffected().reduceCurrentMp(manaDam);
		
		TickManager.getInstance().addEffectPerTickTask(getSkill(), this);
		return true;
	}
	
	@Override
	public int getEffectFlags()
	{
		return EffectFlag.RELAXING.getMask();
	}
}
