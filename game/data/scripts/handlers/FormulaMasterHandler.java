package handlers;

import l2r.gameserver.handler.FormulasHandler;
import l2r.gameserver.handler.IHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import handlers.skills.formulas.CalcBlowDmg;
import handlers.skills.formulas.CalcBlowSuccess;
import handlers.skills.formulas.CalcCpRegen;
import handlers.skills.formulas.CalcElemental;
import handlers.skills.formulas.CalcHpRegen;
import handlers.skills.formulas.CalcLethalHit;
import handlers.skills.formulas.CalcMagiCubicDmg;
import handlers.skills.formulas.CalcMagiDmg;
import handlers.skills.formulas.CalcManaDmg;
import handlers.skills.formulas.CalcMpRegen;
import handlers.skills.formulas.CalcPhysBowDmg;
import handlers.skills.formulas.CalcPhysCrossBowDmg;
import handlers.skills.formulas.CalcPhysDmg;
import handlers.skills.formulas.CalcPhysSkillDmg;
import handlers.skills.formulas.CalcShieldUse;
import handlers.skills.formulas.CalcSiegeRegen;
import handlers.skills.formulas.CalcSkillReflectDmg;
import handlers.skills.formulas.CalcSkillReflectSuccess;
import handlers.skills.formulas.CalcSkillSuccess;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class FormulaMasterHandler
{
	private static final Logger _log = LoggerFactory.getLogger(FormulaMasterHandler.class);
	
	private final static Class<?>[] SCRIPTS =
	{
		CalcBlowDmg.class,
		CalcBlowSuccess.class,
		CalcCpRegen.class,
		CalcElemental.class,
		CalcHpRegen.class,
		CalcLethalHit.class,
		CalcMagiCubicDmg.class,
		CalcMagiDmg.class,
		CalcManaDmg.class,
		CalcMpRegen.class,
		CalcPhysBowDmg.class,
		CalcPhysCrossBowDmg.class,
		CalcPhysDmg.class,
		CalcPhysSkillDmg.class,
		CalcShieldUse.class,
		CalcSiegeRegen.class,
		CalcSkillReflectSuccess.class,
		CalcSkillReflectDmg.class,
		CalcSkillSuccess.class,
	};
	
	public FormulaMasterHandler()
	{
		loadHandlers(FormulasHandler.getInstance(), SCRIPTS);
	}
	
	private void loadHandlers(IHandler<?, ?> handler, Class<?>[] classes)
	{
		for (Class<?> c : classes)
		{
			if (c == null)
			{
				continue;
			}
			
			try
			{
				handler.registerByClass(c);
			}
			catch (Exception ex)
			{
				_log.error("Failed loading handler {}!", c.getSimpleName(), ex);
			}
		}
		
		_log.info("{}: Loaded {} handlers.", handler.getClass().getSimpleName(), handler.size());
	}
	
	public static void main(String[] args)
	{
		new FormulaMasterHandler();
	}
}
