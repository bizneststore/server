/*
 * Copyright (C) L2J Sunrise
 * This file is part of L2J Sunrise.
 */
package handlers.effecthandlers;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.effects.EffectInstant;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.effects.L2Effect;
import l2r.gameserver.model.effects.L2EffectType;
import l2r.gameserver.model.stats.Env;
import l2r.gameserver.model.stats.SkillFormulas;

import gr.sr.features.cancelreturn.CancelBuffReturnManager;

/**
 * @author vGodFather
 */
public class DispelBySlotProbability extends EffectInstant
{
	private final String _dispel;
	private final Map<String, Short> _dispelAbnormals;
	private final int _rate;
	
	public DispelBySlotProbability(Env env, EffectTemplate template)
	{
		super(env, template);
		
		_dispel = template.getParameters().getString("dispel", null);
		_rate = template.getParameters().getInt("rate", 0);
		if ((_dispel != null) && !_dispel.isEmpty())
		{
			_dispelAbnormals = new ConcurrentHashMap<>();
			for (String ngtStack : _dispel.split(";"))
			{
				String[] ngt = ngtStack.split(",");
				_dispelAbnormals.put(ngt[0].toLowerCase(), (ngt.length > 1) ? Short.parseShort(ngt[1]) : Short.MAX_VALUE);
			}
		}
		else
		{
			_dispelAbnormals = Collections.<String, Short> emptyMap();
		}
	}
	
	@Override
	public boolean calcSuccess(Env info)
	{
		return SkillFormulas.calcStealSuccess(getEffector(), getEffected(), getSkill(), _rate);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.DISPEL;
	}
	
	@Override
	public boolean onStart()
	{
		if (_dispelAbnormals.isEmpty())
		{
			return false;
		}
		
		L2Character target = getEffected();
		if ((target == null) || target.isDead())
		{
			return false;
		}
		
		for (Entry<String, Short> value : _dispelAbnormals.entrySet())
		{
			String stackType = value.getKey();
			float stackOrder = value.getValue();
			int skillCast = getSkill().getId();
			
			// final boolean result = Formulas.calcStealSuccess(getEffector(), target, getSkill(), _rate);
			// if (Rnd.get(100) < _rate)
			{
				for (L2Effect e : target.getAllEffects())
				{
					if (!e.getSkill().canBeDispeled())
					{
						continue;
					}
					
					// Calculate steal success only if rate is less than 100 otherwise must proceed
					if ((_rate < 100) && !SkillFormulas.calcStealSuccess(getEffector(), target, getSkill(), _rate))
					{
						continue;
					}
					
					// Fist check for stacktype
					if (stackType.equalsIgnoreCase(e.getAbnormalType().toString()) && (e.getSkill().getId() != skillCast))
					{
						if (e.getSkill() != null)
						{
							if (stackOrder == -1)
							{
								target.stopSkillEffects(e.getSkill().getId());
								if (!e.getSkill().isDebuff())
								{
									CancelBuffReturnManager.tryAddCanceledBuff(getEffected(), e);
								}
							}
							else if (stackOrder >= e.getSkill().getAbnormalLvl())
							{
								target.stopSkillEffects(e.getSkill().getId());
								if (!e.getSkill().isDebuff())
								{
									CancelBuffReturnManager.tryAddCanceledBuff(getEffected(), e);
								}
							}
						}
					}
				}
			}
		}
		
		CancelBuffReturnManager.startReturnTask(getEffected());
		
		return true;
	}
}
