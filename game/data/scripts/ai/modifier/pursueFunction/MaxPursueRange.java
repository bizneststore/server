package ai.modifier.pursueFunction;

import l2r.gameserver.model.actor.L2Npc;

import ai.npc.AbstractNpcAI;

/**
 * @author vGodFather
 */
public class MaxPursueRange extends AbstractNpcAI
{
	// @formatter:off
	private final static int[] PURSU_RANGE_NPCS_LIST =
	{
		// Tiat Mobs
		18759, 18772, 18778, 22536, 22537, 22539, 22540, 22541, 22542, 22543, 22544, 22546,
		22548, 22549, 22550, 22551, 22552, 22593, 22596, 22597, 29162,
		// Frintezza Mobs
		18329, 18330, 18331, 18333, 18334, 18335, 18336, 18337, 18338,
		// Beleth Mobs
		29118, 29119,
		// Core Mobs
		29006, 29007, 29008, 29011,
		// Orfen Mobs
		29014, 29016, 29018,
		// VanHalter Mobs
		29062, 22188, 32058, 32059, 32060, 32061, 32062, 32063, 32064, 32065, 32066,
		// Queen Ant Mobs
		29002, 29003, 29004, 29005,
		// Antharas Mobs
		29068, 29069, 29190, 29070,
		// Baium Mobs
		29020, 29021,
		// Sailren Mobs
		29065,
		// Valakas Mobs
		29028,
		// Zaken Mobs
		29022, 29023, 29024, 29026, 29027, 29176, 29181, 29182, 29183, 29184, 29185,
	};
	// @formatter:on
	
	public MaxPursueRange()
	{
		super(MaxPursueRange.class.getSimpleName(), "ai/modifiers");
		addSpawnId(PURSU_RANGE_NPCS_LIST);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		npc.MAX_PURSUE_RANGE = 30000;
		npc.MAX_PURSUE_RANGE_RAID = 30000;
		return super.onSpawn(npc);
	}
}
