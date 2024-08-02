package handlers.skills.formulas;

import l2r.gameserver.instancemanager.SiegeManager;
import l2r.gameserver.model.L2SiegeClan;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.entity.Siege;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.model.skills.formulas.FormulaEnv;
import l2r.gameserver.model.skills.formulas.FormulaType;
import l2r.gameserver.model.skills.formulas.IFFormulas;
import l2r.gameserver.util.Util;

public class CalcSiegeRegen implements IFFormulas
{
	@Override
	public double calc(L2Character actor, L2Character target, L2Skill skill, FormulaEnv env)
	{
		if (actor == null)
		{
			return 0;
		}
		
		L2PcInstance activeChar = actor.getActingPlayer();
		
		if ((activeChar == null) || (activeChar.getClan() == null))
		{
			return 0;
		}
		
		Siege siege = SiegeManager.getInstance().getSiege(activeChar.getX(), activeChar.getY(), activeChar.getZ());
		if ((siege == null) || !siege.isInProgress())
		{
			return 0;
		}
		
		L2SiegeClan siegeClan = siege.getAttackerClan(activeChar.getClan().getId());
		if ((siegeClan == null) || siegeClan.getFlag().isEmpty() || !Util.checkIfInRange(200, activeChar, siegeClan.getFlag().get(0), true))
		{
			return 0;
		}
		
		return 1.5; // If all is true, then modifier will be 50% more
	}
	
	@Override
	public FormulaType getFormulaType()
	{
		return FormulaType.CALC_SIEGE_REGEN;
	}
}