package handlers.skills.formulas;

import l2r.Config;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.model.skills.formulas.FormulaEnv;
import l2r.gameserver.model.skills.formulas.FormulaType;
import l2r.gameserver.model.skills.formulas.IFFormulas;
import l2r.gameserver.model.stats.SkillFormulas;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.SystemMessage;

import gr.sr.configsEngine.configs.impl.FormulasConfigs;

public class CalcManaDmg implements IFFormulas
{
	@Override
	public double calc(L2Character attacker, L2Character target, L2Skill skill, FormulaEnv env)
	{
		
		// Formula: (SQR(M.Atk)*Power*(Target Max MP/97))/M.Def
		double mAtk = attacker.getMAtk(target, skill);
		double mDef = target.getMDef(attacker, skill);
		final boolean isPvP = attacker.isPlayable() && target.isPlayable();
		final boolean isPvE = attacker.isPlayable() && target.isAttackable();
		
		final double pvpPveMod = SkillFormulas.calculatePvpPveBonus(attacker, target, skill);
		
		double mp = target.getMaxMp();
		
		switch (env.shld)
		{
			case SkillFormulas.SHIELD_DEFENSE_SUCCEED:
				mDef += target.getShldDef();
				break;
			case SkillFormulas.SHIELD_DEFENSE_PERFECT_BLOCK: // perfect block
				return 1;
		}
		
		// Bonus Spiritshot
		mAtk *= env.bss ? FormulasConfigs.ALT_MAGE_BSS_MULTIPLIER : env.sps ? FormulasConfigs.ALT_MAGE_SS_MULTIPLIER : 1;
		
		double damage = (Math.sqrt(mAtk) * skill.getPower(attacker, target, isPvP, isPvE) * (mp / 97)) / mDef;
		damage *= (1 + (SkillFormulas.calcSkillVulnerability(attacker, target, skill) / 100));
		
		damage *= pvpPveMod;
		damage *= SkillFormulas.calculatePvePenalty(attacker, target, skill, env.crit);
		
		// Failure calculation
		if (Config.ALT_GAME_MAGICFAILURES && !SkillFormulas.calcMagicSuccess(attacker, target, skill))
		{
			if (attacker.isPlayer())
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.DAMAGE_DECREASED_BECAUSE_C1_RESISTED_C2_MAGIC);
				sm.addCharName(target);
				sm.addCharName(attacker);
				attacker.sendPacket(sm);
				damage /= 2;
			}
			
			if (target.isPlayer())
			{
				SystemMessage sm2 = SystemMessage.getSystemMessage(SystemMessageId.C1_WEAKLY_RESISTED_C2_MAGIC);
				sm2.addCharName(target);
				sm2.addCharName(attacker);
				target.sendPacket(sm2);
			}
		}
		
		if (env.crit)
		{
			damage *= 3;
			attacker.sendPacket(SystemMessageId.CRITICAL_HIT_MAGIC);
		}
		return damage;
	}
	
	@Override
	public FormulaType getFormulaType()
	{
		return FormulaType.CALC_MANA_DMG;
	}
}