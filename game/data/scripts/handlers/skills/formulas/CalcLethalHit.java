package handlers.skills.formulas;

import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.model.skills.formulas.FormulaEnv;
import l2r.gameserver.model.skills.formulas.FormulaType;
import l2r.gameserver.model.skills.formulas.IFFormulas;
import l2r.gameserver.model.stats.Stats;
import l2r.gameserver.network.SystemMessageId;
import l2r.util.Rnd;

import gr.sr.configsEngine.configs.impl.FormulasConfigs;

public class CalcLethalHit implements IFFormulas
{
	@Override
	public double calc(L2Character activeChar, L2Character target, L2Skill skill, FormulaEnv env)
	{
		if ((activeChar.isPlayer() && !activeChar.getAccessLevel().canGiveDamage()) || (((skill.getCondition() & L2Skill.COND_BEHIND) != 0) && !activeChar.isBehindTarget()))
		{
			return 0;
		}
		if (target.isLethalable() && !target.isInvul())
		{
			// Lethal Strike
			if (calcLethalSuccess(activeChar, target, skill.getLethalStrikeRate(), skill.getMagicLevel()))
			{
				// for Players CP and HP is set to 1.
				if (target.isPlayer() && FormulasConfigs.ALLOW_LETHAL_STRIKES_ON_PLAYERS && !FormulasConfigs.ALLOW_LETHAL_STRIKES_ON_PLAYERS_CP_ONLY)
				{
					target.setCurrentCp(1);
					target.setCurrentHp(1);
					target.sendPacket(SystemMessageId.LETHAL_STRIKE);
				}
				else if (target.isPlayer() && FormulasConfigs.ALLOW_LETHAL_STRIKES_ON_PLAYERS && FormulasConfigs.ALLOW_LETHAL_STRIKES_ON_PLAYERS_CP_ONLY)
				{
					target.setCurrentCp(1);
					target.sendPacket(SystemMessageId.LETHAL_STRIKE);
				}
				else if (target.isSummon() && FormulasConfigs.ALLOW_LETHAL_STRIKES_ON_SUMMONS)
				{
					target.setCurrentHp(1);
				}
				// for Monsters HP is set to 1.
				else if (target.isMonster() && FormulasConfigs.ALLOW_LETHAL_STRIKES_ON_MOBS)
				{
					target.reduceCurrentHp(target.getCurrentHp() - 1, activeChar, skill);
				}
				
				activeChar.sendPacket(SystemMessageId.LETHAL_STRIKE_SUCCESSFUL);
			}
			// Half-Kill
			else if (calcLethalSuccess(activeChar, target, skill.getHalfKillRate(), skill.getMagicLevel()))
			{
				// for Players CP is set to 1.
				if (target.isPlayer() && FormulasConfigs.ALLOW_HALF_KILL_ON_PLAYERS)
				{
					target.setCurrentCp(1);
					target.sendPacket(SystemMessageId.HALF_KILL);
					target.sendPacket(SystemMessageId.CP_DISAPPEARS_WHEN_HIT_WITH_A_HALF_KILL_SKILL);
				}
				// for Monsters HP is set to 50%.
				else if (target.isMonster() && FormulasConfigs.ALLOW_HALF_KILL_ON_MOBS)
				{
					target.setCurrentHp(target.getCurrentHp() * 0.5);
				}
				else if (target.isSummon() && FormulasConfigs.ALLOW_HALF_KILL_ON_SUMMONS)
				{
					target.setCurrentHp(target.getCurrentHp() * 0.5);
				}
				activeChar.sendPacket(SystemMessageId.HALF_KILL);
			}
		}
		else
		{
			return 0;
		}
		return 1;
	}
	
	private boolean calcLethalSuccess(L2Character activeChar, L2Character target, double baseLethal, int magiclvl)
	{
		if (baseLethal <= 0)
		{
			return false;
		}
		
		double chance = 0;
		if (magiclvl > 0)
		{
			int delta = ((magiclvl + activeChar.getLevel()) / 2) - 1 - target.getLevel();
			
			// delta [-3,infinite)
			if (delta >= -3)
			{
				chance = (baseLethal * ((double) activeChar.getLevel() / target.getLevel()));
			}
			// delta [-9, -3[
			else if ((delta < -3) && (delta >= -9))
			{
				// baseLethal
				// chance = -1 * -----------
				// (delta / 3)
				chance = (-3) * (baseLethal / (delta));
			}
			// delta [-infinite,-9[
			else
			{
				chance = baseLethal / 15d;
			}
		}
		else
		{
			chance = (baseLethal * ((double) activeChar.getLevel() / target.getLevel()));
		}
		
		return Rnd.get(1000) < (10 * activeChar.calcStat(Stats.LETHAL_RATE, chance, target, null));
	}
	
	@Override
	public FormulaType getFormulaType()
	{
		return FormulaType.CALC_LETHAL_HIT;
	}
}