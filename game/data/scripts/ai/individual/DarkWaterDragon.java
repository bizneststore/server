package ai.individual;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import l2r.Config;
import l2r.gameserver.enums.CtrlIntention;
import l2r.gameserver.model.actor.L2Attackable;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;

import gr.sr.utils.Rnd;

import ai.npc.AbstractNpcAI;

/**
 * Dark Water Dragon's AI.
 */
public final class DarkWaterDragon extends AbstractNpcAI
{
	private static final int DRAGON = 22267;
	private static final int SHADE1 = 22268;
	private static final int SHADE2 = 22269;
	private static final int FAFURION = 18482;
	private static final int DETRACTOR1 = 22270;
	private static final int DETRACTOR2 = 22271;
	private static final Set<Integer> SECOND_SPAWN = ConcurrentHashMap.newKeySet(); // Used to track if second Shades were already spawned
	// Items
	private static final int BLUE_SEED_OF_EVIL = 9595;
	private static final int RED_SEED_OF_EVIL = 9596;
	private static final int WATER_DRAGON_SCALE = 9691;
	private static final int WATER_DRAGON_CLAW = 9700;
	private static final int SPIRIT_OF_THE_LAKE = 9689;
	private static final Set<Integer> MY_TRACKING_SET = ConcurrentHashMap.newKeySet(); // Used to track instances of npcs
	private static final Map<Integer, L2PcInstance> ID_MAP = new ConcurrentHashMap<>(); // Used to track instances of npcs
	
	public DarkWaterDragon()
	{
		super(DarkWaterDragon.class.getSimpleName(), "ai/individual");
		int[] mobs =
		{
			DRAGON,
			SHADE1,
			SHADE2,
			FAFURION,
			DETRACTOR1,
			DETRACTOR2
		};
		addKillId(mobs);
		addAttackId(mobs);
		addSpawnId(mobs);
		MY_TRACKING_SET.clear();
		SECOND_SPAWN.clear();
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (npc != null)
		{
			if (event.equalsIgnoreCase("first_spawn"))
			{ // timer to start timer "1"
				startQuestTimer("1", 40000, npc, null, true); // spawns detractor every 40 seconds
			}
			else if (event.equalsIgnoreCase("second_spawn"))
			{ // timer to start timer "2"
				startQuestTimer("2", 40000, npc, null, true); // spawns detractor every 40 seconds
			}
			else if (event.equalsIgnoreCase("third_spawn"))
			{ // timer to start timer "3"
				startQuestTimer("3", 40000, npc, null, true); // spawns detractor every 40 seconds
			}
			else if (event.equalsIgnoreCase("fourth_spawn"))
			{ // timer to start timer "4"
				startQuestTimer("4", 40000, npc, null, true); // spawns detractor every 40 seconds
			}
			else if (event.equalsIgnoreCase("1"))
			{ // spawns a detractor
				addSpawn(DETRACTOR1, (npc.getX() + 100), (npc.getY() + 100), npc.getZ(), 0, false, 40000);
			}
			else if (event.equalsIgnoreCase("2"))
			{ // spawns a detractor
				addSpawn(DETRACTOR2, (npc.getX() + 100), (npc.getY() - 100), npc.getZ(), 0, false, 40000);
			}
			else if (event.equalsIgnoreCase("3"))
			{ // spawns a detractor
				addSpawn(DETRACTOR1, (npc.getX() - 100), (npc.getY() + 100), npc.getZ(), 0, false, 40000);
			}
			else if (event.equalsIgnoreCase("4"))
			{ // spawns a detractor
				addSpawn(DETRACTOR2, (npc.getX() - 100), (npc.getY() - 100), npc.getZ(), 0, false, 40000);
			}
			else if (event.equalsIgnoreCase("fafurion_despawn"))
			{ // Fafurion Kindred disappears and drops reward
				cancelQuestTimer("fafurion_poison", npc, null);
				cancelQuestTimer("1", npc, null);
				cancelQuestTimer("2", npc, null);
				cancelQuestTimer("3", npc, null);
				cancelQuestTimer("4", npc, null);
				
				MY_TRACKING_SET.remove(npc.getObjectId());
				player = ID_MAP.remove(npc.getObjectId());
				if (player != null)
				{
					calculateDrop(npc, player, WATER_DRAGON_SCALE, 100.0);
					calculateDrop(npc, player, WATER_DRAGON_CLAW, 33.0);
				}
				
				npc.deleteMe();
			}
			else if (event.equalsIgnoreCase("fafurion_poison"))
			{ // Reduces Fafurions hp like it is poisoned
				if (npc.getCurrentHp() <= 500)
				{
					cancelQuestTimer("fafurion_despawn", npc, null);
					cancelQuestTimer("first_spawn", npc, null);
					cancelQuestTimer("second_spawn", npc, null);
					cancelQuestTimer("third_spawn", npc, null);
					cancelQuestTimer("fourth_spawn", npc, null);
					cancelQuestTimer("1", npc, null);
					cancelQuestTimer("2", npc, null);
					cancelQuestTimer("3", npc, null);
					cancelQuestTimer("4", npc, null);
					MY_TRACKING_SET.remove(npc.getObjectId());
					ID_MAP.remove(npc.getObjectId());
				}
				npc.reduceCurrentHp(500, npc, null); // poison kills Fafurion if he is not healed
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		int npcId = npc.getId();
		int npcObjId = npc.getObjectId();
		if (npcId == DRAGON)
		{
			final L2Character originalAttacker = isSummon ? attacker.getSummon() : attacker;
			if (!MY_TRACKING_SET.contains(npcObjId))
			{ // this allows to handle multiple instances of npc
				MY_TRACKING_SET.add(npcObjId);
				// Spawn first 5 shades on first attack on Dark Water Dragon
				spawnShade(originalAttacker, SHADE1, npc.getX() + 100, npc.getY() + 100, npc.getZ());
				spawnShade(originalAttacker, SHADE2, npc.getX() + 100, npc.getY() - 100, npc.getZ());
				spawnShade(originalAttacker, SHADE1, npc.getX() - 100, npc.getY() + 100, npc.getZ());
				spawnShade(originalAttacker, SHADE2, npc.getX() - 100, npc.getY() - 100, npc.getZ());
				spawnShade(originalAttacker, SHADE1, npc.getX() - 150, npc.getY() + 150, npc.getZ());
			}
			else if ((npc.getCurrentHp() < (npc.getMaxHp() / 2.0)) && !(SECOND_SPAWN.contains(npcObjId)))
			{
				SECOND_SPAWN.add(npcObjId);
				// Spawn second 5 shades on half hp of on Dark Water Dragon
				spawnShade(originalAttacker, SHADE2, npc.getX() + 100, npc.getY() + 100, npc.getZ());
				spawnShade(originalAttacker, SHADE1, npc.getX() + 100, npc.getY() - 100, npc.getZ());
				spawnShade(originalAttacker, SHADE2, npc.getX() - 100, npc.getY() + 100, npc.getZ());
				spawnShade(originalAttacker, SHADE1, npc.getX() - 100, npc.getY() - 100, npc.getZ());
				spawnShade(originalAttacker, SHADE2, npc.getX() - 150, npc.getY() + 150, npc.getZ());
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		int npcId = npc.getId();
		int npcObjId = npc.getObjectId();
		switch (npcId)
		{
			case DRAGON:
			{
				MY_TRACKING_SET.remove(npcObjId);
				SECOND_SPAWN.remove(npcObjId);
				L2Attackable faf = (L2Attackable) addSpawn(FAFURION, npc.getX(), npc.getY(), npc.getZ(), 0, false, 0); // spawns Fafurion Kindred when Dard Water Dragon is dead
				ID_MAP.put(faf.getObjectId(), killer);
				calculateDrop(npc, killer, RED_SEED_OF_EVIL, 77.0);
				break;
			}
			case FAFURION:
			{
				cancelQuestTimer("fafurion_poison", npc, null);
				cancelQuestTimer("fafurion_despawn", npc, null);
				cancelQuestTimer("first_spawn", npc, null);
				cancelQuestTimer("second_spawn", npc, null);
				cancelQuestTimer("third_spawn", npc, null);
				cancelQuestTimer("fourth_spawn", npc, null);
				cancelQuestTimer("1", npc, null);
				cancelQuestTimer("2", npc, null);
				cancelQuestTimer("3", npc, null);
				cancelQuestTimer("4", npc, null);
				MY_TRACKING_SET.remove(npcObjId);
				ID_MAP.remove(npcObjId);
				break;
			}
			case SHADE1:
				calculateDrop(npc, killer, BLUE_SEED_OF_EVIL, 9.87);
				break;
			case SHADE2:
				calculateDrop(npc, killer, BLUE_SEED_OF_EVIL, 9.95);
				break;
			case DETRACTOR1:
			case DETRACTOR2:
			{
				calculateDrop(npc, killer, SPIRIT_OF_THE_LAKE, 100.0);
				calculateDrop(npc, killer, BLUE_SEED_OF_EVIL, 10.08);
				break;
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	private void calculateDrop(final L2Npc npc, final L2PcInstance killer, final int itemId, final double dropRate)
	{
		final int finalRate = (int) ((dropRate * 100) * Config.RATE_DEATH_DROP_CHANCE_MULTIPLIER);
		if (Rnd.get(10000) <= finalRate)
		{
			int finalAmount = Math.round(Config.RATE_DEATH_DROP_AMOUNT_MULTIPLIER);
			if (finalRate > 10000)
			{
				finalAmount *= (finalRate / 10000) + (Rnd.get(10000) <= (finalRate % 10000) ? 1 : 0);
			}
			npc.dropItem(killer, itemId, finalAmount);
		}
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		int npcId = npc.getId();
		int npcObjId = npc.getObjectId();
		if (npcId == FAFURION)
		{
			if (!MY_TRACKING_SET.contains(npcObjId))
			{
				MY_TRACKING_SET.add(npcObjId);
				// Spawn 4 Detractors on spawn of Fafurion
				int x = npc.getX();
				int y = npc.getY();
				addSpawn(DETRACTOR2, x + 100, y + 100, npc.getZ(), 0, false, 40000);
				addSpawn(DETRACTOR1, x + 100, y - 100, npc.getZ(), 0, false, 40000);
				addSpawn(DETRACTOR2, x - 100, y + 100, npc.getZ(), 0, false, 40000);
				addSpawn(DETRACTOR1, x - 100, y - 100, npc.getZ(), 0, false, 40000);
				startQuestTimer("first_spawn", 2000, npc, null); // timer to delay timer "1"
				startQuestTimer("second_spawn", 4000, npc, null); // timer to delay timer "2"
				startQuestTimer("third_spawn", 8000, npc, null); // timer to delay timer "3"
				startQuestTimer("fourth_spawn", 10000, npc, null); // timer to delay timer "4"
				startQuestTimer("fafurion_poison", 3000, npc, null, true); // Every three seconds reduces Fafurions hp like it is poisoned
				startQuestTimer("fafurion_despawn", 120000, npc, null); // Fafurion Kindred disappears after two minutes
			}
		}
		return super.onSpawn(npc);
	}
	
	private void spawnShade(L2Character attacker, int npcId, int x, int y, int z)
	{
		final L2Npc shade = addSpawn(npcId, x, y, z, 0, false, 0);
		shade.setRunning();
		((L2Attackable) shade).addDamageHate(attacker, 0, 999);
		shade.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
	}
}
