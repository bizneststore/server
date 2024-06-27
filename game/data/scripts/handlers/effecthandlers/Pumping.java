package handlers.effecthandlers;

import l2r.gameserver.enums.ShotType;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.effects.EffectInstant;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.effects.L2EffectType;
import l2r.gameserver.model.fishing.L2Fishing;
import l2r.gameserver.model.items.L2Weapon;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.stats.Env;

/**
 * Pumping effect implementation.
 * @author vGodFather
 */
public final class Pumping extends EffectInstant
{
	private final double _power;
	
	public Pumping(Env env, EffectTemplate template)
	{
		super(env, template);
		
		if (template.getParameters().getString("power", null) == null)
		{
			throw new IllegalArgumentException(getClass().getSimpleName() + ": effect without power!");
		}
		_power = template.getParameters().getDouble("power");
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.FISHING;
	}
	
	@Override
	public boolean onStart()
	{
		final L2Character activeChar = getEffector();
		if (!activeChar.isPlayer())
		{
			return false;
		}
		
		final L2PcInstance player = activeChar.getActingPlayer();
		final L2Fishing fish = player.getFishingEx().getFishCombat();
		final L2Weapon weaponItem = player.getActiveWeaponItem();
		final L2ItemInstance weaponInst = activeChar.getActiveWeaponInstance();
		if ((weaponInst == null) || (weaponItem == null))
		{
			return false;
		}
		
		final int SS = activeChar.isChargedShot(ShotType.FISH_SOULSHOTS) ? 2 : 1;
		if (SS > 1)
		{
			weaponInst.setChargedShot(ShotType.FISH_SOULSHOTS, false);
		}
		
		fish.usePumping(_power, SS, getSkill().getLevel());
		return true;
	}
}
