package handlers.skills.formulas;

import l2r.Config;
import l2r.gameserver.enums.ShotType;
import l2r.gameserver.features.balanceEngine.BalancerConfigs;
import l2r.gameserver.features.balanceEngine.skillBalancer.SkillsBalanceManager;
import l2r.gameserver.model.StatsSet;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Playable;
import l2r.gameserver.model.actor.L2Summon;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.model.skills.formulas.FormulaEnv;
import l2r.gameserver.model.skills.formulas.FormulaType;
import l2r.gameserver.model.skills.formulas.IFFormulas;
import l2r.gameserver.model.stats.BaseStats;
import l2r.gameserver.model.stats.SkillFormulas;
import l2r.gameserver.model.stats.Stats;
import l2r.gameserver.network.Debug;
import l2r.gameserver.util.Util;
import l2r.util.Rnd;

public class CalcSkillSuccess implements IFFormulas
{
	@Override
	public double calc(L2Character attacker, L2Character target, L2Skill skill, FormulaEnv env)
	{
		// Perfect Shield Block.
		if (env.shld == SkillFormulas.SHIELD_DEFENSE_PERFECT_BLOCK)
		{
			return 0;
		}
		
		if ((skill.isDebuff() && (target.calcStat(Stats.DEBUFF_IMMUNITY, 0, attacker, skill) > 0)) || target.isDebuffBlocked())
		{
			return 0;
		}
		
		final double activateRate = skill.getActivateRate();
		// Force success some cancel effects cause they use other calculations
		if (activateRate == -1)
		{
			final double prayChance = SkillFormulas.calcPrayingChance(target, 0);
			if (prayChance != 0)
			{
				return (prayChance * 10) > Rnd.get(1000) ? 1 : 0;
			}
			return 1;
		}
		
		int magicLevel = skill.getMagicLevel();
		if (magicLevel <= -1)
		{
			magicLevel = target.getLevel() + 3;
		}
		
		// Calculate BaseRate.
		final double baseRate = activateRate;
		final double skillStatModifier = calcSkillStatModifier(skill, target);
		final double targetBaseStat = getTargetBasicProperty(skill.getBasicProperty(), target);
		// double baseMod = ((((((magicLevel - target.getLevel()) + 3) * skill.getLvlBonusRate()) + activateRate) + 30.0) - targetBaseStat);
		double baseMod = ((baseRate * skillStatModifier) + 20) - targetBaseStat;
		
		final double elementMod = SkillFormulas.calcAttributeBonus(attacker, target, skill);
		final double traitMod = SkillFormulas.calcGeneralTraitBonus(attacker, target, skill, false);
		final double lvlBonusMod = SkillFormulas.calcLvlBonusMod(attacker, target, skill);
		final double buffDebuffMod = 1 + (target.calcStat(skill.isDebuff() ? Stats.RESIST_SLOT_DEBUFF : Stats.BUFF_VULN, 1, null, null) / 100);
		double mAtkMod = 1;
		
		if (skill.isMagic())
		{
			double mAtk = attacker.getMAtk(null, null);
			double val = 0;
			if (attacker.isChargedShot(ShotType.BLESSED_SPIRITSHOTS))
			{
				val = mAtk * 3.0;// 3.0 is the blessed spiritshot multiplier
			}
			val += mAtk;
			val = (Math.sqrt(val) / target.getMDef(null, null)) * 11.0;
			mAtkMod = val;
		}
		
		final double rate = baseMod * elementMod * traitMod * mAtkMod * lvlBonusMod * buffDebuffMod;
		double finalRate = traitMod > 0 ? Util.constrain(rate, skill.getMinChance(), skill.getMaxChance()) : 0;
		
		double balancedChance = 1.00d;
		if ((attacker instanceof L2PcInstance) || (attacker instanceof L2Summon))
		{
			L2PcInstance player = attacker instanceof L2PcInstance ? attacker.getActingPlayer() : ((L2Summon) attacker).getOwner();
			int skillId = skill.getId();
			double svsAll[] = SkillsBalanceManager.getInstance().getBalance((skillId * -1) - 65536, player.isInOlympiadMode());
			if ((svsAll != null) && (BalancerConfigs.SKILLS_BALANCER_AFFECTS_MONSTERS || (target instanceof L2Playable)))
			{
				balancedChance *= svsAll[0];
			}
			if ((target instanceof L2PcInstance) || (target instanceof L2Summon))
			{
				L2PcInstance t = target instanceof L2PcInstance ? target.getActingPlayer() : ((L2Summon) target).getOwner();
				int targetClassId = SkillsBalanceManager.getInstance().getClassId(t.getClassId().getId());
				double vsTarget[] = SkillsBalanceManager.getInstance().getBalance(skillId + (targetClassId * 65536), t.isInOlympiadMode());
				if (vsTarget != null)
				{
					balancedChance *= vsTarget[0];
				}
			}
		}
		
		finalRate += balancedChance;
		
		// vGodFather: minimum and maximum chance on raids-bosses should not change
		finalRate = (target.isRaid()) ? rate : Math.min(Math.max(finalRate, skill.getMinChance()), skill.getMaxChance());
		
		// vGodFather: sunrise modify success rates if target is praying
		finalRate = SkillFormulas.calcPrayingChance(target, finalRate);
		
		boolean result = (finalRate * 10) > Rnd.get(1000);
		
		if (attacker.isDebug() || Config.DEVELOPER)
		{
			final StatsSet set = new StatsSet();
			set.set("Formula", "Skill success");
			set.set("baseRate", baseRate);
			set.set("baseMod", baseMod);
			set.set("elementMod", elementMod);
			set.set("traitMod", traitMod);
			set.set("mAtkMod", mAtkMod);
			set.set("targetBaseStat", targetBaseStat);
			set.set("lvlBonusMod", lvlBonusMod);
			set.set("buffDebuffMod", buffDebuffMod);
			set.set("rate", String.format("%.2f", rate));
			set.set("balancedChance", String.format("%.2f", balancedChance));
			set.set("finalRate", String.format("%.2f", finalRate));
			set.set("result", String.valueOf(result));
			Debug.sendSkillDebug(attacker, target, skill, set);
		}
		
		return result ? 1 : 0;
	}
	
	private int getTargetBasicProperty(BaseStats basicProperty, L2Character target)
	{
		int targetBaseStat = 0;
		switch (basicProperty)
		{
			case STR:
				targetBaseStat = target.getSTR();
				break;
			case DEX:
				targetBaseStat = target.getDEX();
				break;
			case CON:
				targetBaseStat = target.getCON();
				break;
			case INT:
				targetBaseStat = target.getINT();
				break;
			case MEN:
				targetBaseStat = target.getMEN();
				break;
			case WIT:
				targetBaseStat = target.getWIT();
				break;
		}
		
		return targetBaseStat;
	}
	
	private double calcSkillStatModifier(L2Skill skill, L2Character target)
	{
		double mod = 1d;
		final BaseStats stat = skill.getBasicProperty();
		if (stat == null)
		{
			return mod;
		}
		
		mod /= stat.calcBonus(target);
		return mod > 1 ? 1 : mod;
	}
	
	@Override
	public FormulaType getFormulaType()
	{
		return FormulaType.CALC_SKILL_SUCC;
	}
}
