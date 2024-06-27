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
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.model.skills.L2SkillType;
import l2r.gameserver.model.skills.formulas.FormulaEnv;
import l2r.gameserver.model.skills.formulas.FormulaType;
import l2r.gameserver.model.skills.formulas.IFFormulas;
import l2r.gameserver.model.stats.SkillFormulas;
import l2r.gameserver.model.stats.Stats;
import l2r.gameserver.network.Debug;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.SystemMessage;

import gr.sr.balanceEngine.BalanceHandler;
import gr.sr.configsEngine.configs.impl.FormulasConfigs;
import gr.sr.rankEngine.templates.Ranking;

public class CalcMagiDmg implements IFFormulas
{
	@Override
	public double calc(L2Character attacker, L2Character target, L2Skill skill, FormulaEnv env)
	{
		double mDef = target.getMDef(attacker, skill);
		switch (env.shld)
		{
			case SkillFormulas.SHIELD_DEFENSE_SUCCEED:
				mDef += target.getShldDef(); // kamael
				break;
			case SkillFormulas.SHIELD_DEFENSE_PERFECT_BLOCK: // perfect block
				return 1;
		}
		
		final double mAtk = attacker.getMAtk(target, skill);
		
		final boolean isPvP = attacker.isPlayable() && target.isPlayable();
		final boolean isPvE = attacker.isPlayable() && target.isAttackable();
		final double shotsBonus = env.bss ? FormulasConfigs.ALT_MAGE_BSS_MULTIPLIER : env.sps ? FormulasConfigs.ALT_MAGE_SS_MULTIPLIER : 1;
		final double pvpPveMod = SkillFormulas.calculatePvpPveBonus(attacker, target, skill);
		final double penaltyMod = SkillFormulas.calculatePvePenalty(attacker, target, skill, env.crit);
		
		// Trait, elements
		final double generalTraitMod = SkillFormulas.calcGeneralTraitBonus(attacker, target, skill, true);
		final double attributeMod = SkillFormulas.calcAttributeBonus(attacker, target, skill);
		final double randomMod = attacker.getRandomDamageMultiplier();
		final double skillPower = skill.getPower(attacker, target, isPvP, isPvE);
		
		// damage=91 * sqrt [mAtk * spiritshot_bonus] * power / mDef * trait_bonus * AttBonus * crit_mod * weapon_random + magic_critical_dmg_diff
		//
		// where:
		// sqrt - root
		// mAtk - current magic attack indicator of the attacking character.
		// power - power of the skill
		// mDef - current indicator of magical defense
		// trait_bonus - theoretically this is the ratio of attack bonuses to defense bonuses.
		// attr_bonus
		// spiritshot_bonus- Blessed Charge of Spirit = 4, Charge of Spirit = 2, charge missing = 1.
		// crit_mod - if landed critical
		// weapon_random - damage spread. Each type of weapon has its own parameter.
		// magic_critical_dmg_diff - magical critical damage bonus. For example mag.
		
		double baseMod = ((skillPower * (attacker.isAttackable() ? 80 : FormulasConfigs.ALT_BOOST_MAGIC_SKILL_MULTIPLIER)) * Math.sqrt(mAtk * shotsBonus) * randomMod) / mDef;
		
		double damage = baseMod * generalTraitMod * attributeMod;
		
		// Failure calculation
		if (Config.ALT_GAME_MAGICFAILURES && !SkillFormulas.calcMagicSuccess(attacker, target, skill))
		{
			if (attacker.isPlayer())
			{
				if (SkillFormulas.calcMagicSuccess(attacker, target, skill) && ((target.getLevel() - attacker.getLevel()) <= 9))
				{
					if (skill.getSkillType() == L2SkillType.DRAIN)
					{
						attacker.sendPacket(SystemMessageId.DRAIN_HALF_SUCCESFUL);
					}
					else
					{
						attacker.sendPacket(SystemMessageId.ATTACK_FAILED);
					}
					
					damage /= 2;
				}
				else
				{
					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_RESISTED_YOUR_S2);
					sm.addCharName(target);
					sm.addSkillName(skill);
					attacker.sendPacket(sm);
					
					damage = 1;
				}
			}
			
			if (target.isPlayer())
			{
				final SystemMessage sm = (skill.getSkillType() == L2SkillType.DRAIN) ? SystemMessage.getSystemMessage(SystemMessageId.RESISTED_C1_DRAIN) : SystemMessage.getSystemMessage(SystemMessageId.RESISTED_C1_MAGIC);
				sm.addCharName(attacker);
				target.sendPacket(sm);
			}
		}
		
		if (env.crit)
		{
			damage *= attacker.isPlayer() && target.isPlayer() ? 2.5 : 3;
			damage *= attacker.calcStat(Stats.MAGICAL_CRITICAL_DAMAGE, 1, null, null);
		}
		
		damage *= pvpPveMod;
		damage *= penaltyMod;
		
		// Reunion balancer
		damage = BalanceHandler.getInstance().calc(attacker, target, skill, damage, true);
		// Reunion balancer - End
		
		double balancedDamage = 1.00;
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
				balancedDamage *= env.crit ? vsAll[3] : vsAll[2];
			}
			if ((target instanceof L2PcInstance) || (target instanceof L2Summon))
			{
				L2PcInstance t = target instanceof L2PcInstance ? target.getActingPlayer() : ((L2Summon) target).getOwner();
				int targetClassId = ClassBalanceManager.getInstance().getClassId(t.getClassId().getId());
				double vsTarget[] = ClassBalanceManager.getInstance().getBalance((playerClassId * 256) + targetClassId, player.isInOlympiadMode());
				if (vsTarget != null)
				{
					balancedDamage *= env.crit ? vsTarget[3] : vsTarget[2];
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
			set.set("Formula", "Magic skill damage");
			set.set("skillPower", skillPower);
			set.set("ssboost", shotsBonus);
			set.set("pvpPveMod", pvpPveMod);
			set.set("penaltyMod", penaltyMod);
			set.set("baseMod", baseMod);
			set.set("generalTraitMod", generalTraitMod);
			set.set("attributeMod", attributeMod);
			set.set("randomMod", randomMod);
			set.set("balanceMod", (1 + (balancedDamage / 100)));
			set.set("critical", env.crit);
			set.set("damage rounded", (int) damage);
			Debug.sendSkillDebug(attacker, target, skill, set);
		}
		
		return damage;
	}
	
	@Override
	public FormulaType getFormulaType()
	{
		return FormulaType.CALC_MAGI_DMG;
	}
}