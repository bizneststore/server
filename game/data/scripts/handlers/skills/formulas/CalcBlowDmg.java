package handlers.skills.formulas;

import l2r.gameserver.features.balanceEngine.BalancerConfigs;
import l2r.gameserver.features.balanceEngine.classBalancer.ClassBalanceManager;
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
import l2r.gameserver.model.stats.SkillFormulas;
import l2r.gameserver.model.stats.Stats;
import l2r.gameserver.network.Debug;

import gr.sr.balanceEngine.BalanceHandler;
import gr.sr.configsEngine.configs.impl.FormulasConfigs;

public class CalcBlowDmg implements IFFormulas
{
	@Override
	public double calc(L2Character attacker, L2Character target, L2Skill skill, FormulaEnv env)
	{
		double defence = target.getPDef(attacker);
		
		switch (env.shld)
		{
			case SkillFormulas.SHIELD_DEFENSE_SUCCEED:
			{
				defence += target.getShldDef();
				break;
			}
			case SkillFormulas.SHIELD_DEFENSE_PERFECT_BLOCK: // perfect block
			{
				return 1;
			}
		}
		
		final boolean isPvP = attacker.isPlayable() && target.isPlayable();
		final boolean isPvE = attacker.isPlayable() && target.isAttackable();
		double proximityBonus = attacker.isBehindTarget() ? 1.2 : attacker.isInFrontOfTarget() ? 1 : 1.1;
		double ssboost = env.ss ? FormulasConfigs.ALT_BLOW_SS_MULTIPLIER : 1;
		final double pvpPveMod = SkillFormulas.calculatePvpPveBonus(attacker, target, skill);
		
		// Initial damage
		double baseMod = ((FormulasConfigs.ALT_BOOST_BLOW_SKILL_MULTIPLIER * (skill.getPower(isPvP, isPvE) + (attacker.getPAtk(target) * ssboost))) / defence);
		// Critical
		double criticalMod = (attacker.calcStat(Stats.CRITICAL_DAMAGE, 1, target, skill));
		double criticalModPos = (((attacker.calcStat(Stats.CRITICAL_DAMAGE_POS, 1, target, skill) - 1) / 2) + 1);
		double criticalVulnMod = (target.calcStat(Stats.DEFENCE_CRITICAL_DAMAGE, 1, target, skill));
		double criticalAddMod = ((attacker.getStat().calcStat(Stats.CRITICAL_DAMAGE_ADD, 0) * FormulasConfigs.ALT_CRITICAL_DAMAGE_ADD_MULTIPLIER * FormulasConfigs.ALT_BOOST_BLOW_SKILL_MULTIPLIER) / defence);
		double criticalAddVuln = ((target.calcStat(Stats.DEFENCE_CRITICAL_DAMAGE_ADD, 0, target, skill) * FormulasConfigs.ALT_CRITICAL_DAMAGE_ADD_MULTIPLIER * FormulasConfigs.ALT_BOOST_BLOW_SKILL_MULTIPLIER) / defence);
		// Trait, elements
		double weaponTraitMod = SkillFormulas.calcWeaponTraitBonus(attacker, target);
		double generalTraitMod = SkillFormulas.calcGeneralTraitBonus(attacker, target, skill, false);
		double attributeMod = SkillFormulas.calcAttributeBonus(attacker, target, skill);
		double randomMod = attacker.getRandomDamageMultiplier();
		double penaltyMod = SkillFormulas.calculatePvePenalty(attacker, target, skill, false);
		
		double balancedDamage = 1.00d;
		if ((attacker instanceof L2PcInstance) || (attacker instanceof L2Summon))
		{
			L2PcInstance player = attacker instanceof L2PcInstance ? attacker.getActingPlayer() : ((L2Summon) attacker).getOwner();
			int skillId = skill.getId();
			double svsAll[] = SkillsBalanceManager.getInstance().getBalance((skillId * -1) - 65536, player.isInOlympiadMode());
			if ((svsAll != null) && (BalancerConfigs.SKILLS_BALANCER_AFFECTS_MONSTERS || (target instanceof L2Playable)))
			{
				balancedDamage *= svsAll[1];
			}
			if ((target instanceof L2PcInstance) || (target instanceof L2Summon))
			{
				L2PcInstance t = target instanceof L2PcInstance ? target.getActingPlayer() : ((L2Summon) target).getOwner();
				int targetClassId = SkillsBalanceManager.getInstance().getClassId(t.getClassId().getId());
				double vsTarget[] = SkillsBalanceManager.getInstance().getBalance(skillId + (targetClassId * 65536), t.isInOlympiadMode());
				if (vsTarget != null)
				{
					balancedDamage *= vsTarget[1];
				}
			}
		}
		
		if ((attacker instanceof L2PcInstance) || (attacker instanceof L2Summon))
		{
			L2PcInstance player = attacker instanceof L2PcInstance ? attacker.getActingPlayer() : ((L2Summon) attacker).getOwner();
			int playerClassId = ClassBalanceManager.getInstance().getClassId(player.getClassId().getId());
			double vsAll[] = ClassBalanceManager.getInstance().getBalance((playerClassId * -256), player.isInOlympiadMode());
			if ((vsAll != null) && (BalancerConfigs.CLASS_BALANCER_AFFECTS_MONSTERS || (target instanceof L2Playable)))
			{
				balancedDamage *= vsAll[4];
			}
			if ((target instanceof L2PcInstance) || (target instanceof L2Summon))
			{
				L2PcInstance t = target instanceof L2PcInstance ? target.getActingPlayer() : ((L2Summon) target).getOwner();
				int targetClassId = ClassBalanceManager.getInstance().getClassId(t.getClassId().getId());
				double vsTarget[] = ClassBalanceManager.getInstance().getBalance((playerClassId * 256) + targetClassId, player.isInOlympiadMode());
				if (vsTarget != null)
				{
					balancedDamage *= vsTarget[4];
				}
			}
		}
		
		double damage = (baseMod * criticalMod * criticalModPos * criticalVulnMod * proximityBonus * pvpPveMod) + criticalAddMod + criticalAddVuln;
		damage *= weaponTraitMod;
		damage *= generalTraitMod;
		damage *= attributeMod;
		damage *= randomMod;
		damage *= penaltyMod;
		damage *= 1 + (balancedDamage / 100);
		
		// Reunion balancer
		damage = BalanceHandler.getInstance().calc(attacker, target, skill, damage, false);
		// Reunion balancer - End
		
		if (attacker.isDebug())
		{
			final StatsSet set = new StatsSet();
			set.set("Formula", "Blow damage");
			set.set("skillPower", skill.getPower(isPvP, isPvE));
			set.set("ssboost", ssboost);
			set.set("proximityBonus", proximityBonus);
			set.set("pvpPveMod", pvpPveMod);
			set.set("baseMod", baseMod);
			set.set("criticalMod", criticalMod);
			set.set("criticalModPos", criticalModPos);
			set.set("criticalVulnMod", criticalVulnMod);
			set.set("criticalAddMod", criticalAddMod);
			set.set("criticalAddVuln", criticalAddVuln);
			set.set("weaponTraitMod", weaponTraitMod);
			set.set("generalTraitMod", generalTraitMod);
			set.set("attributeMod", attributeMod);
			set.set("randomMod", randomMod);
			set.set("penaltyMod", penaltyMod);
			set.set("balanceMod", balancedDamage);
			set.set("damage", (int) damage);
			set.set("critical possible damage", ((int) damage) * 2);
			Debug.sendSkillDebug(attacker, target, skill, set);
		}
		
		return Math.max(damage, 1);
		
	}
	
	@Override
	public FormulaType getFormulaType()
	{
		return FormulaType.CALC_BLOW_DMG;
	}
}
