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
import l2r.gameserver.model.actor.L2Attackable;
import l2r.gameserver.model.effects.EffectInstant;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.effects.L2EffectType;
import l2r.gameserver.model.stats.Env;
import l2r.gameserver.model.stats.SkillFormulas;
import l2r.util.Rnd;

/**
 * @author -Nemesiss-
 */
public class RemoveTarget extends EffectInstant
{
	private final int _chance;
	
	public RemoveTarget(Env env, EffectTemplate template)
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
	public L2EffectType getEffectType()
	{
		return L2EffectType.REMOVE_TARGET;
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected().isAttackable())
		{
			((L2Attackable) getEffected()).stopHating(getEffector());
			((L2Attackable) getEffected()).setFindTargetDelay(Rnd.get(1000, 2000));
		}
		
		getEffected().setTarget(null);
		getEffected().abortAttack();
		getEffected().abortCast();
		
		// vGodFather: Attackable instances must change intention to active not idle
		getEffected().getAI().setIntention(getEffected().isAttackable() ? CtrlIntention.AI_INTENTION_ACTIVE : CtrlIntention.AI_INTENTION_IDLE);
		return true;
	}
}
