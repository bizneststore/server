package handlers.effecthandlers;

import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.effects.L2Effect;
import l2r.gameserver.model.effects.L2EffectType;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.stats.Env;
import l2r.gameserver.network.SystemMessageId;

/**
 * @author Serveros
 */
public class EnergyDamOverTime extends L2Effect
{
	public EnergyDamOverTime(final Env env, final EffectTemplate template)
	{
		super(env, template);
	}
	
	public EnergyDamOverTime(final Env env, final L2Effect effect)
	{
		super(env, effect);
	}
	
	@Override
	public boolean canBeStolen()
	{
		return !this.getSkill().isToggle();
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.ENERGY_DAM_OVER_TIME;
	}
	
	@Override
	public boolean onActionTime()
	{
		if (this.getEffected().isDead() || (this.getEffected().getActingPlayer().getAgathionId() == 0))
		{
			return false;
		}
		final L2ItemInstance item = this.getEffected().getInventory().getPaperdollItem(15);
		if (item == null)
		{
			return false;
		}
		final double energyDam = this.getValue();
		if (energyDam > item.getAgathionEnergy())
		{
			this.getEffected().sendPacket(SystemMessageId.THE_SKILL_HAS_BEEN_CANCELED_BECAUSE_YOU_HAVE_INSUFFICIENT_ENERGY);
			return false;
		}
		item.setAgathionEnergy((int) (item.getAgathionEnergy() - energyDam));
		return this.getSkill().isToggle();
	}
}
