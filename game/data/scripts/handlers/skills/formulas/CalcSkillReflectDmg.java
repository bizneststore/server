package handlers.skills.formulas;

import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.model.skills.formulas.FormulaEnv;
import l2r.gameserver.model.skills.formulas.FormulaType;
import l2r.gameserver.model.skills.formulas.IFFormulas;
import l2r.gameserver.model.stats.SkillFormulas;
import l2r.gameserver.model.stats.Stats;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.SystemMessage;
import l2r.util.Rnd;

public class CalcSkillReflectDmg implements IFFormulas
{
	@Override
	public double calc(L2Character attacker, L2Character target, L2Skill skill, FormulaEnv env)
	{
		// Only melee skills can be reflected
		if (skill.isMagic() || (skill.getCastRange() == -1) || (skill.getCastRange() > SkillFormulas.MELEE_ATTACK_RANGE))
		{
			return 0;
		}
		
		final double chance = target.calcStat(Stats.PHYSICAL_SKILL_COUNTER, 0, target, skill);
		boolean result = Rnd.get(1000) < (chance * 10);
		if (result)
		{
			if (target.isPlayer())
			{
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.COUNTERED_C1_ATTACK);
				sm.addCharName(attacker);
				target.sendPacket(sm);
			}
			if (attacker.isPlayer())
			{
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_PERFORMING_COUNTERATTACK);
				sm.addCharName(target);
				attacker.sendPacket(sm);
			}
			
			double counterdmg = (((target.getPAtk(attacker) * 10.0) * 70.0) / attacker.getPDef(target));
			counterdmg *= SkillFormulas.calcWeaponTraitBonus(attacker, target);
			counterdmg *= SkillFormulas.calcGeneralTraitBonus(attacker, target, skill, false);
			counterdmg *= SkillFormulas.calcAttributeBonus(attacker, target, skill);
			
			attacker.reduceCurrentHp(counterdmg, target, skill);
			target.notifyDamageReceived(counterdmg, attacker, skill, env.crit, false, true);
		}
		
		return result ? 1 : 0;
	}
	
	@Override
	public FormulaType getFormulaType()
	{
		return FormulaType.CALC_SKILL_REFL_DMG;
	}
}