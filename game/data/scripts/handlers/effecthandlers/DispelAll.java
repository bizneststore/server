/*
 * Copyright (C) L2J Sunrise
 * This file is part of L2J Sunrise.
 */
package handlers.effecthandlers;

import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.effects.EffectInstant;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.effects.L2Effect;
import l2r.gameserver.model.effects.L2EffectType;
import l2r.gameserver.model.stats.Env;
import l2r.gameserver.model.stats.SkillFormulas;

import gr.sr.features.cancelreturn.CancelBuffReturnManager;

/**
 * Dispel All effect implementation.
 */
public final class DispelAll extends EffectInstant
{
	public DispelAll(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public boolean calcSuccess(Env info)
	{
		return SkillFormulas.calcStealSuccess(getEffector(), getEffected(), getSkill(), 20);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.DISPEL;
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected().isPlayer())
		{
			L2PcInstance returnTarget = getEffected().getActingPlayer();
			for (L2Effect effect : returnTarget.getAllEffects())
			{
				CancelBuffReturnManager.tryAddCanceledBuff(getEffected(), effect);
			}
			
			CancelBuffReturnManager.startReturnTask(getEffected());
		}
		
		getEffected().stopAllEffects(false);
		return false;
	}
}
