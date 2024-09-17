package handlers;

import gr.sr.handler.ABLoader;

import ai.modifier.FlyingNpcs;
import ai.modifier.NoChampionMobs;
import ai.modifier.NoMovingNpcs;
import ai.modifier.NoRandomAnimation;
import ai.modifier.NoRandomWalkMobs;
import ai.modifier.NonAttackingNpcs;
import ai.modifier.NonLethalableNpcs;
import ai.modifier.NonTalkingNpcs;
import ai.modifier.RunningNpcs;
import ai.modifier.SeeThroughSilentMove;
import ai.modifier.SevenSignNpcs;
import ai.modifier.dropEngine.DropExample;
import ai.modifier.dropEngine.FortressReward;
import ai.modifier.pursueFunction.MaxPursueRange;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class ModifiersLoader extends ABLoader
{
	private final Class<?>[] SCRIPTS =
	{
		DropExample.class,
		FlyingNpcs.class,
		NoChampionMobs.class,
		NoMovingNpcs.class,
		NonAttackingNpcs.class,
		NonLethalableNpcs.class,
		NonTalkingNpcs.class,
		NoRandomAnimation.class,
		NoRandomWalkMobs.class,
		RunningNpcs.class,
		SeeThroughSilentMove.class,
		
		// Drop Modifiers
		FortressReward.class,
		
		// Seven signs npcs
		SevenSignNpcs.class,
		
		// Pursue Function
		MaxPursueRange.class,
	};
	
	public ModifiersLoader()
	{
		loadScripts();
	}
	
	@Override
	public Class<?>[] getScripts()
	{
		return SCRIPTS;
	}
}
