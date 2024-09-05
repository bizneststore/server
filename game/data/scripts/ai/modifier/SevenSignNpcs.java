package ai.modifier;

import l2r.gameserver.SevenSigns;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.skills.L2Skill;

import ai.npc.AbstractNpcAI;

/**
 * @author vGodFather
 */
public class SevenSignNpcs extends AbstractNpcAI
{
	// @formatter:off
	private final static int[] SEVEN_SIGNS_NPCS_LIST =
	{
		21143, //HereticsCatacomb
		21144, //HereticsCatacomb
		21145, //HereticsCatacomb
		21146, //HereticsCatacomb
		21169, //HereticsCatacomb
		21170, //HereticsCatacomb
		21171, //HereticsCatacomb
		21172, //HereticsCatacomb
		21190, //HereticsCatacomb
		21191, //HereticsCatacomb
		21192, //HereticsCatacomb
		21193, //HereticsCatacomb
		21236, //HereticsCatacomb
		21237, //HereticsCatacomb
		21238, //HereticsCatacomb
		21239, //HereticsCatacomb
		21152, //ApostateCatacomb
		21153, //ApostateCatacomb
		21154, //ApostateCatacomb
		21155, //ApostateCatacomb
		21176, //ApostateCatacomb
		21177, //ApostateCatacomb
		21178, //ApostateCatacomb
		21179, //ApostateCatacomb
		21197, //ApostateCatacomb
		21198, //ApostateCatacomb
		21199, //ApostateCatacomb
		21200, //ApostateCatacomb
		21244, //ApostateCatacomb
		21245, //ApostateCatacomb
		21246, //ApostateCatacomb
		21247, //ApostateCatacomb
		21162, //CatacombDarkOmen
		21163, //CatacombDarkOmen
		21165, //CatacombDarkOmen
		21184, //CatacombDarkOmen
		21185, //CatacombDarkOmen
		21186, //CatacombDarkOmen
		21205, //CatacombDarkOmen
		21206, //CatacombDarkOmen
		21207, //CatacombDarkOmen
		21253, //CatacombDarkOmen
		21254, //CatacombDarkOmen
		21255, //CatacombDarkOmen
		21163, //CatacombOfTheForbiddenPathn
		21164, //CatacombOfTheForbiddenPathn
		21165, //CatacombOfTheForbiddenPathn
		21185, //CatacombOfTheForbiddenPathn
		21186, //CatacombOfTheForbiddenPathn
		21206, //CatacombOfTheForbiddenPathn
		21207, //CatacombOfTheForbiddenPathn
		21254, //CatacombOfTheForbiddenPathn
		21255, //CatacombOfTheForbiddenPathn
		21147, //CatacombBranded
		21149, //CatacombBranded
		21150, //CatacombBranded
		21151, //CatacombBranded
		21173, //CatacombBranded
		21174, //CatacombBranded
		21175, //CatacombBranded
		21176, //CatacombBranded
		21194, //CatacombBranded
		21195, //CatacombBranded
		21196, //CatacombBranded
		21197, //CatacombBranded
		21240, //CatacombBranded
		21241, //CatacombBranded
		21242, //CatacombBranded
		21243, //CatacombBranded
		21156, //CatacombOfTheWitch
		21157, //CatacombOfTheWitch
		21159, //CatacombOfTheWitch
		21160, //CatacombOfTheWitch
		21179, //CatacombOfTheWitch
		21180, //CatacombOfTheWitch
		21181, //CatacombOfTheWitch
		21182, //CatacombOfTheWitch
		21183, //CatacombOfTheWitch
		21200, //CatacombOfTheWitch
		21201, //CatacombOfTheWitch
		21202, //CatacombOfTheWitch
		21203, //CatacombOfTheWitch
		21204, //CatacombOfTheWitch
		21248, //CatacombOfTheWitch
		21249, //CatacombOfTheWitch
		21250, //CatacombOfTheWitch
		21251, //CatacombOfTheWitch
		21252, //CatacombOfTheWitch
		
		21156, //DevotionNecropolis
		21157, //DevotionNecropolis
		21158, //DevotionNecropolis
		21179, //DevotionNecropolis
		21180, //DevotionNecropolis
		21181, //DevotionNecropolis
		21200, //DevotionNecropolis
		21201, //DevotionNecropolis
		21202, //DevotionNecropolis
		21224, //DevotionNecropolis
		21225, //DevotionNecropolis
		21226, //DevotionNecropolis
		21139, //NecropolisSacrifice
		21140, //NecropolisSacrifice
		21141, //NecropolisSacrifice
		21142, //NecropolisSacrifice
		21166, //NecropolisSacrifice
		21167, //NecropolisSacrifice
		21168, //NecropolisSacrifice
		21169, //NecropolisSacrifice
		21187, //NecropolisSacrifice
		21188, //NecropolisSacrifice
		21189, //NecropolisSacrifice
		21190, //NecropolisSacrifice
		21208, //NecropolisSacrifice
		21209, //NecropolisSacrifice
		21210, //NecropolisSacrifice
		21211, //NecropolisSacrifice
		21153, //PatriotsNecropolis
		21154, //PatriotsNecropolis
		21155, //PatriotsNecropolis
		21198, //PatriotsNecropolis
		21199, //PatriotsNecropolis
		21200, //PatriotsNecropolis
		21221, //PatriotsNecropolis
		21222, //PatriotsNecropolis
		21223, //PatriotsNecropolis
		21144, //PilgrimsNecropolis
		21145, //PilgrimsNecropolis
		21146, //PilgrimsNecropolis
		21213, //PilgrimsNecropolis
		21214, //PilgrimsNecropolis
		21215, //PilgrimsNecropolis
		21161, //SaintsNecropolis
		21162, //SaintsNecropolis
		21163, //SaintsNecropolis
		21183, //SaintsNecropolis
		21184, //SaintsNecropolis
		21185, //SaintsNecropolis
		21204, //SaintsNecropolis
		21205, //SaintsNecropolis
		21206, //SaintsNecropolis
		21228, //SaintsNecropolis
		21230, //SaintsNecropolis
		21231, //SaintsNecropolis
		21158, //MartyrsNecropolis
		21159, //MartyrsNecropolis
		21160, //MartyrsNecropolis
		21181, //MartyrsNecropolis
		21182, //MartyrsNecropolis
		21183, //MartyrsNecropolis
		21202, //MartyrsNecropolis
		21203, //MartyrsNecropolis
		21204, //MartyrsNecropolis
		21226, //MartyrsNecropolis
		21227, //MartyrsNecropolis
		21228, //MartyrsNecropolis
		21147, //WorshipersNecropolis
		21148, //WorshipersNecropolis
		21149, //WorshipersNecropolis
		21174, //WorshipersNecropolis
		21175, //WorshipersNecropolis
		21176, //WorshipersNecropolis
		21195, //WorshipersNecropolis
		21196, //WorshipersNecropolis
		21197, //WorshipersNecropolis
		21217, //WorshipersNecropolis
		21218, //WorshipersNecropolis
		21219, //WorshipersNecropolis
		21161, //DisciplesNecropolis
		21162, //DisciplesNecropolis
		21164, //DisciplesNecropolis
		21165, //DisciplesNecropolis
		21183, //DisciplesNecropolis
		21184, //DisciplesNecropolis
		21185, //DisciplesNecropolis
		21186, //DisciplesNecropolis
		21204, //DisciplesNecropolis
		21205, //DisciplesNecropolis
		21206, //DisciplesNecropolis
		21207, //DisciplesNecropolis
		21228, //DisciplesNecropolis
		21229, //DisciplesNecropolis
		21230, //DisciplesNecropolis
		21231, //DisciplesNecropolis
		
	};
	// @formatter:on
	
	public SevenSignNpcs()
	{
		super(SevenSignNpcs.class.getSimpleName(), "ai/modifiers");
		addAttackId(SEVEN_SIGNS_NPCS_LIST);
		addSkillSeeId(SEVEN_SIGNS_NPCS_LIST);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		final int playerCabal = SevenSigns.getInstance().getPlayerCabal(attacker.getObjectId());
		if (playerCabal == SevenSigns.CABAL_NULL)
		{
			attacker.teleToClosestTown();
			return super.onAttack(npc, attacker, damage, isSummon);
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance caster, L2Skill skill, L2Object[] targets, boolean isSummon)
	{
		final int playerCabal = SevenSigns.getInstance().getPlayerCabal(caster.getObjectId());
		if (playerCabal == SevenSigns.CABAL_NULL)
		{
			caster.teleToClosestTown();
			return super.onSkillSee(npc, caster, skill, targets, isSummon);
		}
		return super.onSkillSee(npc, caster, skill, targets, isSummon);
	}
}
