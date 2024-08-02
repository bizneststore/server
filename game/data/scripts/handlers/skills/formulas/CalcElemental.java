package handlers.skills.formulas;

import l2r.Config;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.model.skills.formulas.FormulaEnv;
import l2r.gameserver.model.skills.formulas.FormulaType;
import l2r.gameserver.model.skills.formulas.IFFormulas;
import l2r.log.filter.Log;

public class CalcElemental implements IFFormulas
{
	@Override
	public double calc(L2Character attacker, L2Character target, L2Skill skill, FormulaEnv env)
	{
		int calcPower = 0;
		int calcDefen = 0;
		int calcTotal = 0;
		double result = 1.0;
		byte element;
		
		if (skill != null)
		{
			element = skill.getElement();
			if (element >= 0)
			{
				calcPower = skill.getElementPower();
				calcDefen = target.getDefenseElementValue(element);
				
				if (attacker.getAttackElement() == element)
				{
					calcPower += attacker.getAttackElementValue(element);
				}
				
				calcTotal = calcPower - calcDefen;
				if (calcTotal > 0)
				{
					if (calcTotal < 50)
					{
						result += calcTotal * 0.003948;
					}
					else if (calcTotal < 150)
					{
						result = 1.2;
					}
					else if (calcTotal < 300)
					{
						result = 1.4;
					}
					else
					{
						result = 1.7;
					}
				}
				else if (calcTotal < -110)
				{
					if (attacker.isNpc())
					{
						if (calcTotal <= -170)
						{
							result = 0.8;
						}
						else
						{
							result = 1 - ((170 + calcTotal) * 0.0033d);
						}
					}
				}
				
				if (Config.DEVELOPER || attacker.isDebug())
				{
					Log.info(skill.getName() + ": " + calcPower + ", " + calcDefen + ", " + result + " Total: " + calcTotal);
				}
			}
		}
		else
		{
			element = attacker.getAttackElement();
			if (element >= 0)
			{
				calcPower = attacker.getAttackElementValue(element);
				calcDefen = target.getDefenseElementValue(element);
				
				calcTotal = calcPower - calcDefen;
				if (calcTotal < -110)
				{
					if (attacker.isNpc())
					{
						if (calcTotal <= -170)
						{
							result = 0.8;
						}
						else
						{
							result = 1 - ((170 + calcTotal) * 0.0033d);
						}
					}
				}
				else if (calcTotal > 0)
				{
					if (calcTotal < 50)
					{
						result += calcTotal * 0.003948;
					}
					else if (calcTotal < 150)
					{
						result = 1.2;
					}
					else if (calcTotal < 300)
					{
						result = 1.4;
					}
					else
					{
						result = 1.7;
					}
				}
				
				if (Config.DEVELOPER || attacker.isDebug())
				{
					Log.info("Hit: " + calcPower + ", " + calcDefen + ", " + result + " Total: " + calcTotal);
				}
			}
		}
		return Math.max(result, 1.0);
	}
	
	@Override
	public FormulaType getFormulaType()
	{
		return FormulaType.CALC_ELEMENTAL;
	}
}