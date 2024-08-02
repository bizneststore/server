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
package handlers.skillhandlers;

import l2r.Config;
import l2r.gameserver.enums.ShotType;
import l2r.gameserver.handler.ISkillHandler;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.effects.L2Effect;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.model.skills.L2SkillType;
import l2r.gameserver.model.skills.formulas.FormulaEnv;
import l2r.gameserver.model.skills.formulas.FormulaType;
import l2r.gameserver.model.stats.Env;
import l2r.gameserver.model.stats.SkillFormulas;
import l2r.log.filter.Log;

public class Mdam implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.MDAM,
	};
	
	@Override
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		if (activeChar.isAlikeDead())
		{
			return;
		}
		
		boolean ss = skill.useSoulShot() && activeChar.isChargedShot(ShotType.SOULSHOTS);
		boolean sps = skill.useSpiritShot() && activeChar.isChargedShot(ShotType.SPIRITSHOTS);
		boolean bss = skill.useSpiritShot() && activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
		
		for (L2Object trg : targets)
		{
			if (!trg.isCharacter())
			{
				continue;
			}
			
			L2Character target = (L2Character) trg;
			
			if (activeChar.isPlayer() && target.isPlayer() && target.getActingPlayer().isFakeDeath())
			{
				target.stopFakeDeath(true);
			}
			else if (target.isDead())
			{
				// We must try apply the effects on dead character
				// because some effects must land like consume body
				if (skill.hasEffects())
				{
					skill.getEffects(activeChar, target);
				}
				continue;
			}
			
			final boolean mcrit = SkillFormulas.calcMCrit(activeChar.getMCriticalHit(target, skill));
			final byte shld = SkillFormulas.calcShldUse(activeChar, target, skill);
			final byte reflect = SkillFormulas.calcSkillReflect(target, skill);
			
			// Calculate skill evasion
			final boolean skillIsEvaded = SkillFormulas.calcMagicalSkillEvasion(activeChar, target, skill);
			if (skillIsEvaded)
			{
				continue;
			}
			
			int damage = skill.isStaticDamage() ? (int) skill.getPower() : (SkillFormulas.calculateInt(FormulaType.CALC_MAGI_DMG, activeChar, target, skill, new FormulaEnv(shld, false, ss, bss, mcrit)));
			
			// Curse of Divinity Formula (each buff increase +30%)
			if (!skill.isStaticDamage() && skill.getDependOnTargetBuff())
			{
				damage *= (((target.getBuffCount() * 0.3) + 1.3) / 4);
			}
			
			if (!skill.isStaticDamage() && (skill.getMaxSoulConsumeCount() > 0) && activeChar.isPlayer())
			{
				// Souls Formula (each soul increase +4%)
				int chargedSouls = (activeChar.getActingPlayer().getChargedSouls() <= skill.getMaxSoulConsumeCount()) ? activeChar.getActingPlayer().getChargedSouls() : skill.getMaxSoulConsumeCount();
				damage *= 1 + (chargedSouls * 0.04);
			}
			
			// Possibility of a lethal strike
			SkillFormulas.calculate(FormulaType.CALC_LETHAL_HIT, activeChar, target, skill, null);
			
			if (damage > 0)
			{
				// Manage attack or cast break of the target (calculating rate, sending message...)
				if (!target.isRaid() && SkillFormulas.calcAtkBreak(target, damage))
				{
					target.breakAttack();
					target.breakCast();
				}
				
				// vengeance reflected damage
				// DS: because only skill using vengeanceMdam is Shield Deflect Magic
				// and for this skill no damage should pass to target, just hardcode it for now
				if ((reflect & SkillFormulas.SKILL_REFLECT_VENGEANCE) != 0)
				{
					activeChar.reduceCurrentHp(damage, target, skill);
					activeChar.notifyDamageReceived(damage, target, skill, mcrit, false, true);
				}
				else
				{
					activeChar.sendDamageMessage(target, damage, mcrit, false, false);
					target.reduceCurrentHp(damage, activeChar, skill);
					target.notifyDamageReceived(damage, activeChar, skill, mcrit, false, false);
				}
				
				if (Config.LOG_GAME_DAMAGE && activeChar.isPlayable() && target.isPlayer() && (damage > Config.LOG_GAME_DAMAGE_THRESHOLD))
				{
					Log.LogPlayerMDamages("", new Object[]
					{
						activeChar,
						" did damage ",
						damage,
						skill,
						" crit(" + mcrit + ") ",
						" to ",
						target
					});
				}
				
				// Maybe launch chance skills on us
				if (activeChar.getChanceSkills() != null)
				{
					activeChar.getChanceSkills().onSkillHit(target, skill, false, damage);
				}
				// Maybe launch chance skills on target
				if (target.getChanceSkills() != null)
				{
					target.getChanceSkills().onSkillHit(activeChar, skill, true, damage);
				}
				
				if (skill.hasEffects())
				{
					if ((reflect & SkillFormulas.SKILL_REFLECT_SUCCEED) != 0) // reflect skill effects
					{
						activeChar.stopSkillEffects(skill.getId());
						skill.getEffects(target, activeChar);
					}
					else
					{
						skill.getEffectsVoid(activeChar, target, new Env(shld, ss, sps, bss));
					}
				}
			}
		}
		
		// self Effect :]
		if (skill.hasSelfEffects())
		{
			final L2Effect effect = activeChar.getFirstEffect(skill.getId());
			if ((effect != null) && effect.isSelfEffect())
			{
				// Replace old effect with new one.
				effect.exit();
			}
			skill.getEffectsSelf(activeChar);
		}
		
		if (targets.length > 0)
		{
			activeChar.setChargedShot(bss ? ShotType.BLESSED_SPIRITSHOTS : ShotType.SPIRITSHOTS, false);
		}
		
		if (skill.isSuicideAttack())
		{
			activeChar.doDie(activeChar);
		}
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
