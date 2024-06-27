package handlers.effecthandlers;

import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.effects.L2Effect;
import l2r.gameserver.model.stats.Env;

/**
 * @author vGodFather
 */
public class p_change_fishing_mastery extends L2Effect
{
	private final int _masteryLevel;
	private final double _masteryPower;
	
	public p_change_fishing_mastery(Env env, EffectTemplate template)
	{
		super(env, template);
		
		_masteryPower = Double.parseDouble(template.getParameters().getString("masteryPower"));
		_masteryLevel = Integer.parseInt(template.getParameters().getString("masteryLevel"));
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected().isPlayer())
		{
			getEffected().getActingPlayer().getFishingEx().setPotionMastery(_masteryLevel, _masteryPower);
		}
		return true;
	}
	
	@Override
	public void onExit()
	{
		if (getEffected().isPlayer())
		{
			getEffected().getActingPlayer().getFishingEx().setPotionMastery(0, 0d);
		}
	}
}