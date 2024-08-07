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

/**
 * Fake Death effect.
 * @author mkizub
 */
public class FakeDeath extends OverTimeEffect
{
	public FakeDeath(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.FAKE_DEATH;
	}
	
	@Override
	public boolean onStart()
	{
		getEffected().startFakeDeath();
		
		onTick();
		
		return true;
	}
	
	@Override
	public void onExit()
	{
		getEffected().stopFakeDeath(false);
	}
	
	@Override
	public boolean onTick()
	{
		if (getEffected().isDead())
		{
			return false;
		}
		
		double manaDam = getValue() * getTicksMultiplier();
		if ((manaDam > getEffected().getCurrentMp()) && getSkill().isToggle())
		{
			getEffected().sendPacket(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP);
			return false;
		}
		
		getEffected().reduceCurrentMp(manaDam);
		
		TickManager.getInstance().addEffectPerTickTask(getSkill(), this);
		return true;
	}
}