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
import l2r.gameserver.enums.ShotType;
import l2r.gameserver.features.balanceEngine.BalancerConfigs;
import l2r.gameserver.features.balanceEngine.classBalancer.ClassBalanceManager;
import l2r.gameserver.features.balanceEngine.skillBalancer.SkillsBalanceManager;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Playable;
import l2r.gameserver.model.actor.L2Summon;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.effects.EffectInstant;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.effects.L2EffectType;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.model.stats.BaseStats;
import l2r.gameserver.model.stats.Env;
import l2r.gameserver.model.stats.SkillFormulas;
import l2r.gameserver.model.stats.Stats;
import l2r.log.filter.Log;
import l2r.util.Rnd;

import gr.sr.balanceEngine.BalanceHandler;
import gr.sr.configsEngine.configs.impl.FormulasConfigs;

/**
 * Energy Attack effect implementation.
 * @author NosBit
 */
public final class EnergyAttack extends EffectInstant
{
	private final double _power;
	private final int _criticalChance;
	private final boolean _ignoreShieldDefence;
	
	public EnergyAttack(Env env, EffectTemplate template)
	{
		super(env, template);
		
		_power = template.getParameters().getDouble("power", 0);
		_criticalChance = template.getParameters().getInt("criticalChance", 0);
		_ignoreShieldDefence = template.getParameters().getBoolean("ignoreShieldDefence", false);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.PHYSICAL_ATTACK;
	}
	
	@Override
	public boolean calcSuccess(Env info)
	{
		return !SkillFormulas.calcPhysicalSkillEvasion(info.getCharacter(), info.getTarget(), info.getSkill());
	}
	
	@Override
	public boolean onStart()
	{
		final L2PcInstance attacker = getEffector() instanceof L2PcInstance ? (L2PcInstance) getEffector() : null;
		if (attacker == null)
		{
			return false;
		}
		
		final L2Character target = getEffected();
		final L2Skill skill = getSkill();
		
		double attack = attacker.getPAtk(target);
		double defence = target.getPDef(attacker);
		
		if (!_ignoreShieldDefence)
		{
			byte shield = SkillFormulas.calcShldUse(attacker, target, skill, true);
			switch (shield)
			{
				case SkillFormulas.SHIELD_DEFENSE_FAILED:
				{
					break;
				}
				case SkillFormulas.SHIELD_DEFENSE_SUCCEED:
				{
					defence += target.getShldDef();
					break;
				}
				case SkillFormulas.SHIELD_DEFENSE_PERFECT_BLOCK:
				{
					defence = -1;
					break;
				}
			}
		}
		
		double damage = 1;
		boolean critical = false;
		
		if (defence != -1)
		{
			double damageMultiplier = SkillFormulas.calcWeaponTraitBonus(attacker, target) * SkillFormulas.calcAttributeBonus(attacker, target, skill) * SkillFormulas.calcGeneralTraitBonus(attacker, target, skill, true);
			
			boolean ss = getSkill().useSoulShot() && attacker.isChargedShot(ShotType.SOULSHOTS);
			double ssBoost = ss ? FormulasConfigs.ALT_PHYSICAL_SKILL_SS_MULTIPLIER : 1.0;
			final double weaponMod = attacker.getAttackType().isRanged() ? FormulasConfigs.ALT_BOOST_PHYS_SKILL_RANGE_MULTIPLIER : FormulasConfigs.ALT_BOOST_PHYS_SKILL_MELEE_MULTIPLIER;
			
			// charge count should be the count before casting the skill but since its reduced before calling effects
			// we add skill consume charges to current charges
			double energyChargesBoost = (((attacker.getCharges() + skill.getChargeConsume()) - 1) * 0.2) + 1;
			
			attack += _power;
			attack *= ssBoost;
			attack *= energyChargesBoost;
			attack *= weaponMod;
			
			damage = attack / defence;
			damage *= damageMultiplier;
			if (target instanceof L2PcInstance)
			{
				damage *= attacker.getStat().calcStat(Stats.PVP_PHYSICAL_SKILL_DMG_BONUS, 1.0);
				damage /= target.getStat().calcStat(Stats.PVP_PHYSICAL_SKILL_DEF_BONUS, 1.0);
				damage = attacker.getStat().calcStat(Stats.PHYSICAL_SKILL_POWER, damage);
			}
			
			critical = (BaseStats.STR.calcBonus(attacker) * _criticalChance) > (Rnd.nextDouble() * 100);
			if (critical)
			{
				damage *= 2;
			}
		}
		
		int skillId = skill.getId();
		double svsAll[] = SkillsBalanceManager.getInstance().getBalance((skillId * -1) - 65536, false);
		if ((svsAll != null) && (BalancerConfigs.SKILLS_BALANCER_AFFECTS_MONSTERS || (target instanceof L2Playable)))
		{
			damage *= svsAll[1];
		}
		if ((target instanceof L2PcInstance) || (target instanceof L2Summon))
		{
			L2PcInstance t = target instanceof L2PcInstance ? target.getActingPlayer() : ((L2Summon) target).getOwner();
			int targetClassId = SkillsBalanceManager.getInstance().getClassId(t.getClassId().getId());
			double vsTarget[] = SkillsBalanceManager.getInstance().getBalance(skillId + (targetClassId * 65536), t.isInOlympiadMode());
			if (vsTarget != null)
			{
				damage *= vsTarget[1];
			}
		}
		
		int playerClassId = ClassBalanceManager.getInstance().getClassId(attacker.getClassId().getId());
		double vsAll[] = ClassBalanceManager.getInstance().getBalance(playerClassId * -256, attacker.isInOlympiadMode());
		if ((vsAll != null) && (BalancerConfigs.CLASS_BALANCER_AFFECTS_MONSTERS || (target instanceof L2Playable)))
		{
			if (critical)
			{
				damage *= vsAll[6];
			}
			else if (!critical)
			{
				damage *= vsAll[5];
			}
			else if (critical)
			{
				damage *= vsAll[1];
			}
			else if (!critical)
			{
				damage *= vsAll[0];
			}
		}
		
		if ((target instanceof L2PcInstance) || (target instanceof L2Summon))
		{
			L2PcInstance t = target instanceof L2PcInstance ? target.getActingPlayer() : ((L2Summon) target).getOwner();
			int targetClassId = ClassBalanceManager.getInstance().getClassId(t.getClassId().getId());
			double vsTarget[] = ClassBalanceManager.getInstance().getBalance((playerClassId * 256) + targetClassId, attacker.isInOlympiadMode());
			if (vsTarget != null)
			{
				if (critical)
				{
					damage *= vsTarget[6];
				}
				else if (!critical)
				{
					damage *= vsTarget[5];
				}
				else if (critical)
				{
					damage *= vsTarget[1];
				}
				else if (!critical)
				{
					damage *= vsTarget[0];
				}
			}
		}
		
		// Reunion balancer
		damage = BalanceHandler.getInstance().calc(attacker, target, skill, damage, false);
		// Reunion balancer - End
		
		if (damage > 0)
		{
			attacker.sendDamageMessage(target, (int) damage, false, critical, false);
			target.reduceCurrentHp(damage, attacker, skill);
			target.notifyDamageReceived(damage, attacker, skill, critical, false, false);
			
			if (Config.LOG_GAME_DAMAGE && attacker.isPlayable() && target.isPlayer() && (damage > Config.LOG_GAME_DAMAGE_THRESHOLD))
			{
				Log.LogPlayerPDamages("", new Object[]
				{
					attacker,
					" did damage ",
					(int) damage,
					skill,
					" to ",
					target
				});
			}
			
			// Check if damage should be reflected
			SkillFormulas.calcDamageReflected(attacker, target, skill, damage, critical);
		}
		return true;
	}
}