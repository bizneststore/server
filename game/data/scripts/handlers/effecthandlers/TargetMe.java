package handlers.effecthandlers;

import l2r.gameserver.enums.Race;
import l2r.gameserver.model.actor.L2Playable;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.effects.L2Effect;
import l2r.gameserver.model.stats.Env;

/**
 * @author -Nemesiss-
 */
public class TargetMe extends L2Effect
{
	public TargetMe(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected().isPlayable())
		{
			if (getEffected().getTemplate().getRace() == Race.SIEGE_WEAPON)
			{
				return false;
			}
			
			L2PcInstance effector = getEffector().getActingPlayer();
			L2PcInstance effected = getEffected().getActingPlayer();
			if ((effector != null) && (effected != null) && effector.isFriend(effected))
			{
				return false;
			}
			
			if (getEffected().getTarget() != getEffector())
			{
				// If effector is null, then its not a player, but NPC. If its not null, then it should check if the skill is pvp skill.
				if ((effector == null) || effector.checkPvpSkill(getEffected(), getSkill()))
				{
					// Target is different
					getEffected().setTarget(getEffector());
				}
			}
			((L2Playable) getEffected()).setLockedTarget(getEffector());
			return true;
		}
		else if (getEffected().isAttackable() && !getEffected().isRaid())
		{
			return true;
		}
		
		return false;
	}
	
	@Override
	public void onExit()
	{
		if (getEffected().isPlayable())
		{
			((L2Playable) getEffected()).setLockedTarget(null);
		}
	}
}
