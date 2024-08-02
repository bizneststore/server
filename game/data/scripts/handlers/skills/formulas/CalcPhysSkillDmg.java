package handlers.skills.formulas;

import l2r.Config;
import l2r.gameserver.features.balanceEngine.BalancerConfigs;
import l2r.gameserver.features.balanceEngine.classBalancer.ClassBalanceManager;
import l2r.gameserver.features.balanceEngine.skillBalancer.SkillsBalanceManager;
import l2r.gameserver.model.StatsSet;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Playable;
import l2r.gameserver.model.actor.L2Summon;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.items.L2Weapon;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.model.skills.formulas.FormulaEnv;
import l2r.gameserver.model.skills.formulas.FormulaType;
import l2r.gameserver.model.skills.formulas.IFFormulas;
import l2r.gameserver.model.stats.SkillFormulas;
import l2r.gameserver.model.stats.Stats;
import l2r.gameserver.network.Debug;

import gr.sr.balanceEngine.BalanceHandler;
import gr.sr.configsEngine.configs.impl.FormulasConfigs;

public class CalcPhysSkillDmg implements IFFormulas
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
		
		final boolean isPvP = attacker.isPlayable() && target.isPlayable();
		final boolean isPvE = attacker.isPlayable() && target.isAttackable();
		double proximityBonus = attacker.isBehindTarget() ? 1.2 : attacker.isInFrontOfTarget() ? 1 : 1.1; // Behind: +20% - Side: +10%
		final L2Weapon weaponItem = attacker.getActiveWeaponItem();
		final boolean isBow = ((weaponItem != null) && weaponItem.getItemType().isBow());
		final boolean isPole = ((weaponItem != null) && weaponItem.getItemType().isPole());
		final double bowMod = isBow && attacker.isPlayer() && !attacker.getActingPlayer().isInOlympiadMode() ? FormulasConfigs.ALT_BOW_SKILL_MULTIPLIER : 1.00d;
		final double poleMod = isPole && attacker.isPlayer() && !attacker.getActingPlayer().isInOlympiadMode() ? FormulasConfigs.ALT_POLE_SKILL_MULTIPLIER : 1.00d;
		double ssBoost = env.ss ? FormulasConfigs.ALT_PHYSICAL_SKILL_SS_MULTIPLIER : 1;
		final double weaponMod = attacker.getAttackType().isRanged() ? FormulasConfigs.ALT_BOOST_PHYS_SKILL_RANGE_MULTIPLIER : FormulasConfigs.ALT_BOOST_PHYS_SKILL_MELEE_MULTIPLIER;
		double penaltyMod = SkillFormulas.calculatePvePenalty(attacker, target, skill, env.crit);
		// Trait, elements
		double attackTraitMod = SkillFormulas.calcAttackTraitBonus(attacker, target);
		double generalTraitMod = 1.0;// SkillFormulas.calcGeneralTraitBonus(attacker, target, skill, false);
		double attributeMod = SkillFormulas.calcAttributeBonus(attacker, target, skill);
		double randomMod = attacker.getRandomDamageMultiplier();
		final double pvpPveMod = SkillFormulas.calculatePvpPveBonus(attacker, target, skill);
		
		// Initial damage
		double baseMod = ((weaponMod * (skill.getPower(isPvP, isPvE) + attacker.getPAtk(target))) / defence) * ssBoost;
		
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
				balancedDamage *= env.crit ? vsAll[6] : vsAll[5];
			}
			if ((target instanceof L2PcInstance) || (target instanceof L2Summon))
			{
				L2PcInstance t = target instanceof L2PcInstance ? target.getActingPlayer() : ((L2Summon) target).getOwner();
				int targetClassId = ClassBalanceManager.getInstance().getClassId(t.getClassId().getId());
				double vsTarget[] = ClassBalanceManager.getInstance().getBalance((playerClassId * 256) + targetClassId, player.isInOlympiadMode());
				if (vsTarget != null)
				{
					balancedDamage *= env.crit ? vsTarget[6] : vsTarget[5];
				}
			}
		}
		
		double damage = baseMod * proximityBonus;
		damage *= pvpPveMod;
		damage *= attackTraitMod;
		damage *= generalTraitMod;
		damage *= attributeMod;
		damage *= randomMod;
		damage *= penaltyMod;
		damage *= bowMod;
		damage *= poleMod;
		damage = attacker.getStat().calcStat(Stats.PHYSICAL_SKILL_POWER, damage);
		damage *= 1 + (balancedDamage / 100);
		
		// Reunion balancer
		damage = BalanceHandler.getInstance().calc(attacker, target, skill, damage, false);
		// Reunion balancer - End
		
		if (attacker.isDebug())
		{
			final StatsSet set = new StatsSet();
			set.set("Formula", "Phys skill damage");
			set.set("skillPower", skill.getPower(isPvP, isPvE));
			set.set("ssboost", ssBoost);
			set.set("proximityBonus", proximityBonus);
			set.set("pvpPveMod", pvpPveMod);
			set.set("penaltyMod", penaltyMod);
			set.set("baseMod", baseMod);
			set.set("attackTraitMod", attackTraitMod);
			set.set("generalTraitMod", generalTraitMod);
			set.set("attributeMod", attributeMod);
			set.set("randomMod", randomMod);
			set.set("bowMod", bowMod);
			set.set("poleMod", poleMod);
			set.set("balanceMod", (1 + (balancedDamage / 100)));
			set.set("damage", (int) damage);
			set.set("critical possible damage", ((int) damage) * 2);
			Debug.sendSkillDebug(attacker, target, skill, set);
		}
		
		return Math.max(damage, 1);
	}
	
	@Override
	public FormulaType getFormulaType()
	{
		return FormulaType.CALC_PHYS_SKILL_DMG;
	}
}
