package handlers.effecthandlers;

import l2r.gameserver.model.effects.EffectInstant;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.stats.Env;

/**
 * Vitality Point Up effect implementation.
 * @author GodFather
 */
public final class VitalityPointUp extends EffectInstant
{
	private final float _value;
	
	public VitalityPointUp(Env env, EffectTemplate template)
	{
		super(env, template);
		
		_value = template.getParameters().getFloat("value", 0);
	}
	
	@Override
	public boolean onStart()
	{
		if ((getEffected() != null) && getEffected().isPlayer())
		{
			getEffected().getActingPlayer().updateVitalityPoints(_value, false, false);
			getEffected().getActingPlayer().sendUserInfo(true);
		}
		return true;
	}
}
