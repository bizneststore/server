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

import java.util.List;
import java.util.stream.Collectors;

import l2r.gameserver.model.actor.L2Attackable;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.effects.EffectInstant;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.stats.Env;
import l2r.gameserver.model.stats.SkillFormulas;
import l2r.util.Rnd;

public class RandomizeHate extends EffectInstant
{
	private final int _chance;
	
	public RandomizeHate(Env env, EffectTemplate template)
	{
		super(env, template);
		
		_chance = template.getParameters().getInt("chance", 100);
	}
	
	@Override
	public boolean calcSuccess(Env info)
	{
		return SkillFormulas.calcProbability(_chance, info.getCharacter(), info.getTarget(), info.getSkill());
	}
	
	@Override
	public boolean onStart()
	{
		if ((getEffected() == null) || (getEffected() == getEffector()) || !getEffected().isAttackable())
		{
			return false;
		}
		
		final L2Attackable effectedMob = (L2Attackable) getEffected();
		final List<L2Character> aggroList = effectedMob.getAggroList().keySet().stream().filter(c -> c != getEffector()).collect(Collectors.toList());
		if (aggroList.isEmpty())
		{
			return true;
		}
		
		// Choosing randomly a new target
		final L2Character target = aggroList.get(Rnd.get(aggroList.size()));
		final long hate = effectedMob.getHating(getEffector());
		effectedMob.stopHating(getEffector());
		effectedMob.addDamageHate(target, 0, hate);
		
		return true;
	}
}