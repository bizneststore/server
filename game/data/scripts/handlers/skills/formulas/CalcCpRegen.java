package handlers.skills.formulas;

import l2r.Config;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.model.skills.formulas.FormulaEnv;
import l2r.gameserver.model.skills.formulas.FormulaType;
import l2r.gameserver.model.skills.formulas.IFFormulas;
import l2r.gameserver.model.stats.BaseStats;
import l2r.gameserver.model.stats.Stats;

public class CalcCpRegen implements IFFormulas
{
	@Override
	public double calc(L2Character cha, L2Character target, L2Skill skill, FormulaEnv env)
	{
		if (!cha.isPlayer())
		{
			return 0;
		}
		
		final L2PcInstance player = cha.getActingPlayer();
		
		// With CON bonus
		final double init = player.getActingPlayer().getTemplate().getBaseCpRegen(player.getLevel()) * player.getLevelMod() * BaseStats.CON.calcBonus(player);
		double cpRegenMultiplier = Config.CP_REGEN_MULTIPLIER;
		if (player.isSitting())
		{
			cpRegenMultiplier *= 1.5; // Sitting
		}
		else if (!player.isMoving())
		{
			cpRegenMultiplier *= 1.1; // Staying
		}
		else if (player.isRunning())
		{
			cpRegenMultiplier *= 0.7; // Running
		}
		return player.calcStat(Stats.REGENERATE_CP_RATE, Math.max(1, init), null, null) * cpRegenMultiplier;
	}
	
	@Override
	public FormulaType getFormulaType()
	{
		return FormulaType.CALC_CP_REGEN;
	}
}
