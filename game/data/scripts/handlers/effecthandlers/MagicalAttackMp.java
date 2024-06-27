/*
 * Copyright (C) 2004-2015 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.effecthandlers;

import l2r.gameserver.enums.ShotType;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.effects.EffectInstant;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.effects.L2EffectType;
import l2r.gameserver.model.skills.formulas.FormulaEnv;
import l2r.gameserver.model.skills.formulas.FormulaType;
import l2r.gameserver.model.stats.Env;
import l2r.gameserver.model.stats.SkillFormulas;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.SystemMessage;

/**
 * Magical Attack MP effect.
 * @author Adry_85
 */
public final class MagicalAttackMp extends EffectInstant
{
	public MagicalAttackMp(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.MAGICAL_ATTACK_MP;
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected().isInvul() || getEffected().isMpBlocked())
		{
			return false;
		}
		if (!SkillFormulas.calcMagicAffected(getEffector(), getEffected(), getSkill()))
		{
			if (getEffector().isPlayer())
			{
				getEffector().sendPacket(SystemMessageId.ATTACK_FAILED);
			}
			if (getEffected().isPlayer())
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_RESISTED_C2_DRAIN2);
				sm.addCharName(getEffected());
				sm.addCharName(getEffector());
				getEffected().sendPacket(sm);
			}
			return false;
		}
		
		L2Character target = getEffected();
		L2Character activeChar = getEffector();
		
		if (activeChar.isAlikeDead())
		{
			return false;
		}
		
		boolean sps = getSkill().useSpiritShot() && activeChar.isChargedShot(ShotType.SPIRITSHOTS);
		boolean bss = getSkill().useSpiritShot() && activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
		final byte shld = SkillFormulas.calcShldUse(activeChar, target, getSkill());
		final boolean mcrit = SkillFormulas.calcMCrit(activeChar.getMCriticalHit(target, getSkill()));
		double damage = SkillFormulas.calculate(FormulaType.CALC_MANA_DMG, activeChar, target, getSkill(), new FormulaEnv(shld, false, sps, bss, mcrit));
		double mp = (damage > target.getCurrentMp() ? target.getCurrentMp() : damage);
		
		if (damage > 0)
		{
			target.stopEffectsOnDamage(true);
			target.setCurrentMp(target.getCurrentMp() - mp);
		}
		
		if (target.isPlayer())
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_MP_HAS_BEEN_DRAINED_BY_C1);
			sm.addCharName(activeChar);
			sm.addInt((int) mp);
			target.sendPacket(sm);
		}
		
		if (activeChar.isPlayer())
		{
			SystemMessage sm2 = SystemMessage.getSystemMessage(SystemMessageId.YOUR_OPPONENTS_MP_WAS_REDUCED_BY_S1);
			sm2.addInt((int) mp);
			activeChar.sendPacket(sm2);
		}
		return true;
	}
}