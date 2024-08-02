package handlers.skills.formulas;

import l2r.gameserver.model.StatsSet;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.model.skills.formulas.FormulaEnv;
import l2r.gameserver.model.skills.formulas.FormulaType;
import l2r.gameserver.model.skills.formulas.IFFormulas;
import l2r.gameserver.model.stats.BaseStats;
import l2r.gameserver.model.stats.Stats;
import l2r.gameserver.network.Debug;
import l2r.gameserver.util.Util;
import l2r.util.Rnd;

public class CalcBlowSuccess implements IFFormulas
{
	@Override
	public double calc(L2Character activeChar, L2Character target, L2Skill skill, FormulaEnv env)
	{
		double dexMod = BaseStats.DEX.calcBonus(activeChar);
		// Apply DEX Mod.
		double blowChance = skill.getBlowChance();
		// Apply Position Bonus.
		double sideMod = (activeChar.isInFrontOfTarget()) ? 1 : (activeChar.isBehindTarget()) ? 1.5 : 1.25;
		// Apply all mods.
		double baseRate = blowChance * dexMod * sideMod;
		// Apply blow rates
		double rate = activeChar.calcStat(Stats.FATAL_BLOW_RATE, baseRate, target, null);
		
		final double finalRate = Math.min(rate, 80);
		
		final boolean isBackstab = skill.getDmgDirectlyToHP();
		final boolean isOnAngle = Util.isOnAngle(target, activeChar, 180, 120);
		final boolean result = isBackstab && !isOnAngle ? false : Rnd.get(1000) < (finalRate * 10);
		// Debug
		if (activeChar.isDebug())
		{
			final StatsSet set = new StatsSet();
			set.set("Formula", "Blow success");
			set.set("dexMod", dexMod);
			set.set("blowChance", blowChance);
			set.set("sideMod", sideMod);
			set.set("baseRate", baseRate);
			set.set("isBackstab", isBackstab);
			set.set("behind", activeChar.isBehindTarget());
			set.set("isOnAngle", isOnAngle);
			set.set("rate", String.format("%.2f", rate));
			set.set("finalRate: (max 80 of 100)", String.format("%.2f", finalRate));
			set.set("result", String.valueOf(result));
			Debug.sendSkillDebug(activeChar, target, skill, set);
		}
		
		return result ? 1 : 0;
	}
	
	@Override
	public FormulaType getFormulaType()
	{
		return FormulaType.CALC_BLOW_SUCC;
	}
}
