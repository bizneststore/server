package handlers.skills.formulas;

import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.effects.L2EffectType;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.model.skills.formulas.FormulaEnv;
import l2r.gameserver.model.skills.formulas.FormulaType;
import l2r.gameserver.model.skills.formulas.IFFormulas;
import l2r.gameserver.model.stats.SkillFormulas;
import l2r.gameserver.model.stats.Stats;
import l2r.util.Rnd;

public class CalcSkillReflectSuccess implements IFFormulas
{
	@Override
	public double calc(L2Character attacker, L2Character target, L2Skill skill, FormulaEnv env)
	{
		// Neither some special skills (like hero debuffs...) or those skills ignoring resistances can be reflected
		if (!skill.canBeReflected() || (skill.getPower() == -1))
		{
			return SkillFormulas.SKILL_REFLECT_FAILED;
		}
		
		// Only magic and melee skills can be reflected
		if (!skill.isMagic() && ((skill.getCastRange() == -1) || (skill.getCastRange() > SkillFormulas.MELEE_ATTACK_RANGE)))
		{
			return SkillFormulas.SKILL_REFLECT_FAILED;
		}
		
		byte reflect = SkillFormulas.SKILL_REFLECT_FAILED;
		// Check for non-reflected skilltypes, need additional retail check
		
		if (skill.hasEffectType(L2EffectType.MAGICAL_ATTACK_MP, L2EffectType.PHYSICAL_ATTACK, L2EffectType.PHYSICAL_ATTACK_HP_LINK, L2EffectType.DEATH_LINK))
		{
			final Stats stat = skill.isMagic() ? Stats.FULL_REFLECT_DD_MAGIC : Stats.PHYSICAL_SKILL_COUNTER;
			final double venganceChance = target.getStat().calcStat(stat, 0, target, skill);
			if (venganceChance > Rnd.get(100))
			{
				reflect |= SkillFormulas.SKILL_REFLECT_VENGEANCE;
			}
		}
		
		switch (skill.getSkillType())
		{
			// vGodfather fixes
			case FEAR:
			case ROOT:
			case STUN:
			case MUTE:
			case BLEED:
			case PARALYZE:
			case SLEEP:
			case DEBUFF:
			case DISARM:
				// vGodFather fixes End
			case PDAM:
			case MDAM:
			case BLOW:
			case DRAIN:
			case CHARGEDAM:
				final Stats stat = skill.isMagic() ? Stats.FULL_REFLECT_DD_MAGIC : Stats.PHYSICAL_SKILL_COUNTER;
				final double venganceChance = target.getStat().calcStat(stat, 0, target, skill);
				if (venganceChance > Rnd.get(100))
				{
					reflect |= SkillFormulas.SKILL_REFLECT_VENGEANCE;
				}
				break;
			default:
			{
				return SkillFormulas.SKILL_REFLECT_FAILED;
			}
		}
		
		if (skill.isDebuff())
		{
			final double reflectChance = target.calcStat(skill.isMagic() ? Stats.MAGICAL_DEBUFF_REFLECT : Stats.PHYSICAL_DEBUFF_REFLECT, 0, null, skill);
			if (Rnd.get(1000) < (reflectChance * 10))
			{
				reflect |= SkillFormulas.SKILL_REFLECT_SUCCEED;
			}
		}
		else
		{
			final double reflectChance = target.calcStat(skill.isMagic() ? Stats.MAGICAL_BUFF_REFLECT : Stats.PHYSICAL_BUFF_REFLECT, 0, null, skill);
			if (Rnd.get(1000) < (reflectChance * 10))
			{
				reflect |= SkillFormulas.SKILL_REFLECT_SUCCEED;
			}
		}
		
		return reflect;
	}
	
	@Override
	public FormulaType getFormulaType()
	{
		return FormulaType.CALC_SKILL_REFL_SUCCESS;
	}
}