package handlers.skills.formulas;

import l2r.Config;
import l2r.gameserver.features.balanceEngine.BalancerConfigs;
import l2r.gameserver.features.balanceEngine.classBalancer.ClassBalanceManager;
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

import gr.sr.configsEngine.configs.impl.FormulasConfigs;
import gr.sr.rankEngine.templates.Ranking;

public class CalcPhysCrossBowDmg implements IFFormulas
{
	@Override
	public double calc(L2Character attacker, L2Character target, L2Skill skill, FormulaEnv env)
	{
		double defence = target.getPDef(attacker);
		
		switch (env.shld)
		{
			case SkillFormulas.SHIELD_DEFENSE_SUCCEED:
				if (!Config.ALT_GAME_SHIELD_BLOCKS)
				{
					defence += target.getShldDef();
				}
				break;
			case SkillFormulas.SHIELD_DEFENSE_PERFECT_BLOCK: // perfect block
				return 1.;
		}
		
		double proximityBonus = attacker.isBehindTarget() ? 1.2 : attacker.isInFrontOfTarget() ? 1 : 1.1;
		double pAtk = attacker.getPAtk(target);
		double ssboost = env.ss ? FormulasConfigs.ALT_PHYSICAL_SKILL_SS_MULTIPLIER : 1;
		double randomMod = attacker.getRandomDamageMultiplier();
		double rangeMod = SkillFormulas.getRangeMultiplier(attacker, target);
		final double pvpPveMod = SkillFormulas.calculatePvpPveBonus(attacker, target, null);
		final double penaltyMod = SkillFormulas.calculatePvePenalty(attacker, target, null, env.crit);
		// Trait, elements
		double attackTraitMod = SkillFormulas.calcAttackTraitBonus(attacker, target);
		double attributeMod = SkillFormulas.calcAttributeBonus(attacker, target, null);
		double criticalMod = 1.00d;
		double criticalModPos = 1.00d;
		double criticalVulnMod = 1.00d;
		double criticalAddMod = 1.00d;
		double criticalAddVuln = 1.00d;
		
		// Initial damage
		double baseMod = pAtk * ssboost;
		
		double damage = baseMod;// baseMod * proximityBonus * randomMod * attackTraitMod * weaponTraitMod * attributeMod * rangeMod;
		
		damage *= randomMod;
		
		if (env.crit)
		{
			criticalMod = attacker.calcStat(Stats.CRITICAL_DAMAGE, 1, target, null);
			criticalModPos = (attacker.calcStat(Stats.CRITICAL_DAMAGE_POS, 1, target, null));
			criticalVulnMod = target.calcStat(Stats.DEFENCE_CRITICAL_DAMAGE, 1, target, null);
			criticalAddMod = ((attacker.getStat().calcStat(Stats.CRITICAL_DAMAGE_ADD, 0) * FormulasConfigs.ALT_CRITICAL_DAMAGE_ADD_MULTIPLIER * FormulasConfigs.ALT_BOOST_RANGE_MULTIPLIER) / defence);
			criticalAddVuln = ((target.calcStat(Stats.DEFENCE_CRITICAL_DAMAGE_ADD, 0, target, null) * FormulasConfigs.ALT_CRITICAL_DAMAGE_ADD_MULTIPLIER * FormulasConfigs.ALT_BOOST_RANGE_MULTIPLIER) / defence);
			
			damage *= 2 * criticalMod * criticalModPos * criticalVulnMod;
			damage += criticalAddMod;
			damage += criticalAddVuln;
		}
		
		damage *= proximityBonus;
		damage *= FormulasConfigs.ALT_BOOST_RANGE_MULTIPLIER / defence;
		damage *= attackTraitMod;
		damage *= attributeMod;
		damage *= rangeMod;
		damage *= pvpPveMod;
		damage *= penaltyMod;
		
		double tempDamage = damage;
		
		// Reunion balancer
		// damage = BalanceHandler.getInstance().calc(attacker, target, null, damage, false);
		// Reunion balancer - End
		
		double balancedDamage = 1.00d;
		if ((attacker instanceof L2PcInstance) || (attacker instanceof L2Summon))
		{
			L2PcInstance player = attacker instanceof L2PcInstance ? attacker.getActingPlayer() : ((L2Summon) attacker).getOwner();
			int playerClassId = ClassBalanceManager.getInstance().getClassId(player.getClassId().getId());
			double vsAll[] = ClassBalanceManager.getInstance().getBalance((playerClassId * -256), player.isInOlympiadMode());
			if ((vsAll != null) && (BalancerConfigs.CLASS_BALANCER_AFFECTS_MONSTERS || (target instanceof L2Playable)))
			{
				if (env.crit)
				{
					balancedDamage *= vsAll[1];
				}
				else
				{
					balancedDamage *= vsAll[0];
				}
			}
			if ((target instanceof L2PcInstance) || (target instanceof L2Summon))
			{
				L2PcInstance t = target instanceof L2PcInstance ? target.getActingPlayer() : ((L2Summon) target).getOwner();
				int targetClassId = ClassBalanceManager.getInstance().getClassId(t.getClassId().getId());
				double vsTarget[] = ClassBalanceManager.getInstance().getBalance((playerClassId * 256) + targetClassId, player.isInOlympiadMode());
				if (vsTarget != null)
				{
					if (env.crit)
					{
						balancedDamage *= vsTarget[1];
					}
					else
					{
						balancedDamage *= vsTarget[0];
					}
				}
			}
		}
		
		damage *= 1 + (balancedDamage / 100);
		
		// Add the damage done to the player stats
		if (attacker.isPlayer() && (damage > 5))
		{
			attacker.getActingPlayer().addPlayerStats(Ranking.STAT_TOP_DAMAGE, (long) damage);
		}
		
		if (attacker.isDebug())
		{
			final StatsSet set = new StatsSet();
			set.set("Formula", "Phys crosbow damage");
			set.set("ssboost", ssboost);
			set.set("proximityBonus", proximityBonus);
			set.set("pvpPveMod", pvpPveMod);
			set.set("penaltyMod", penaltyMod);
			set.set("rangeMod", rangeMod);
			set.set("criticalMod", criticalMod);
			set.set("criticalModPos", criticalModPos);
			set.set("criticalVulnMod", criticalVulnMod);
			set.set("criticalAddMod", criticalAddMod);
			set.set("criticalAddVuln", criticalAddVuln);
			set.set("attackTraitMod", attackTraitMod);
			set.set("attributeMod", attributeMod);
			set.set("randomMod", randomMod);
			set.set("real damage", (int) tempDamage);
			set.set("balanced damage", (int) damage);
			set.set("is critical", env.crit);
			Debug.sendSkillDebug(attacker, target, null, set);
		}
		
		return damage;
	}
	
	@Override
	public FormulaType getFormulaType()
	{
		return FormulaType.CALC_PHYS_CROSSBOW_DMG;
	}
}
