package handlers.voicedcommandhandlers;

import java.text.NumberFormat;
import java.util.Locale;

import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.handler.IVoicedCommandHandler;
import l2r.gameserver.model.Elementals;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.items.L2Armor;
import l2r.gameserver.model.items.L2Item;
import l2r.gameserver.model.items.type.ArmorType;
import l2r.gameserver.model.skills.formulas.FormulaType;
import l2r.gameserver.model.stats.SkillFormulas;
import l2r.gameserver.model.stats.Stats;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author -=DoctorNo=-
 */
public class CharStatsVCmd implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"getstat",
		"getstats"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.startsWith("getstat") || command.startsWith("getstats"))
		{
			calculateStats(activeChar);
		}
		
		return true;
	}
	
	private void calculateStats(L2PcInstance player)
	{
		L2Character target = null;// (L2Character) player.getTarget();
		
		double hpRegen = SkillFormulas.calculate(FormulaType.CALC_HP_REGEN, player, null, null, null);
		double cpRegen = SkillFormulas.calculate(FormulaType.CALC_CP_REGEN, player, null, null, null);
		double mpRegen = SkillFormulas.calculate(FormulaType.CALC_MP_REGEN, player, null, null, null);
		double hpDrain = player.calcStat(Stats.ABSORB_DAMAGE_PERCENT, 0., target, null);
		double mpDrain = player.calcStat(Stats.ABSORB_DMG_TO_MP_PERCENT, 0., target, null);
		double hpGain = player.calcStat(Stats.HEAL_EFFECTIVNESS, 100., target, null);
		double mpGain = player.calcStat(Stats.MP_EFFECTIVNESS, 100., target, null);
		double critPerc = 100 - (player.calcStat(Stats.CRITICAL_DAMAGE, 1, target, null) * 100);
		double critStatic = player.calcStat(Stats.CRITICAL_DAMAGE_ADD, 0, target, null);
		double mCritRate = (player.calcStat(Stats.MAGICAL_CRITICAL_RATE, 1, target, null)) * 10;
		double blowRate = player.calcStat(Stats.FATAL_BLOW_RATE, 0, target, null);
		
		L2Item shld = player.getSecondaryWeaponItem();
		boolean shield = (shld != null) && (shld instanceof L2Armor) && (((L2Armor) shld).getItemType() != ArmorType.SIGIL);
		
		double shieldDef = shield ? player.calcStat(Stats.SHIELD_DEFENCE, player.getTemplate().getBaseShldDef(), target, null) : 0.;
		double shieldRate = shield ? player.calcStat(Stats.SHIELD_RATE, 0, target, null) : 0.;
		
		double fireResist = player.getDefenseElementValue(Elementals.FIRE);
		double windResist = player.getDefenseElementValue(Elementals.WIND);
		double waterResist = player.getDefenseElementValue(Elementals.WATER);
		double earthResist = player.getDefenseElementValue(Elementals.EARTH);
		double holyResist = player.getDefenseElementValue(Elementals.HOLY);
		double unholyResist = player.getDefenseElementValue(Elementals.DARK);
		
		double bleedPower = player.calcStat(Stats.BLEED_PROF, 0, target, null);
		double bleedResist = player.calcStat(Stats.BLEED_VULN, 0, target, null);
		double poisonPower = player.calcStat(Stats.POISON_PROF, 0, target, null);
		double poisonResist = player.calcStat(Stats.POISON_VULN, 0, target, null);
		double stunPower = player.calcStat(Stats.STUN_PROF, 0, target, null);
		double stunResist = player.calcStat(Stats.STUN_VULN, 0, target, null);
		double rootPower = player.calcStat(Stats.ROOT_PROF, 0, target, null);
		double rootResist = player.calcStat(Stats.ROOT_VULN, 0, target, null);
		double sleepPower = player.calcStat(Stats.SLEEP_PROF, 0, target, null);
		double sleepResist = player.calcStat(Stats.SLEEP_VULN, 0, target, null);
		double paralyzePower = player.calcStat(Stats.PARALYZE_PROF, 0, target, null);
		double paralyzeResist = player.calcStat(Stats.PARALYZE_VULN, 0, target, null);
		double mentalPower = player.calcStat(Stats.DERANGEMENT_PROF, 0, target, null);
		double mentalResist = player.calcStat(Stats.DERANGEMENT_VULN, 0, target, null);
		double debuffPower = player.calcStat(Stats.DEBUFF_PROF, 0, target, null);
		double debuffResist = player.calcStat(Stats.RESIST_SLOT_DEBUFF, 0, target, null);
		double cancelPower = player.calcStat(Stats.CANCEL_PROF, 0, target, null);
		double cancelResist = player.calcStat(Stats.CANCEL_VULN, 0, target, null);
		
		double swordResist = 100 - (player.calcStat(Stats.SWORD_WPN_VULN, 1, target, null) * 100);
		double dualResist = 100 - (player.calcStat(Stats.DUAL_WPN_VULN, 1, target, null) * 100);
		double bluntResist = 100 - (player.calcStat(Stats.BLUNT_WPN_VULN, 1, target, null) * 100);
		double daggerResist = 100 - (player.calcStat(Stats.DAGGER_WPN_VULN, 1, target, null) * 100);
		double bowResist = 100 - (player.calcStat(Stats.BOW_WPN_VULN, 1, target, null) * 100);
		double crossbowResist = 100 - (player.calcStat(Stats.CROSSBOW_WPN_VULN, 1, target, null) * 100);
		double poleResist = 100 - (player.calcStat(Stats.POLE_WPN_VULN, 1, target, null) * 100);
		double fistResist = 100 - (player.calcStat(Stats.FIST_WPN_VULN, 1, target, null) * 100);
		
		double critChanceResist = 100 - (player.calcStat(Stats.DEFENCE_CRITICAL_RATE, 1, target, null) * 100);
		double critDamResist = 100 - (player.calcStat(Stats.DEFENCE_CRITICAL_DAMAGE, 1, target, null) * 100);
		
		NumberFormat df = NumberFormat.getInstance(Locale.ENGLISH);
		df.setMaximumFractionDigits(1);
		df.setMinimumFractionDigits(1);
		
		String html = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/sunrise/character/main.htm");
		
		html = html.replace("%hpRegen%", df.format(hpRegen));
		html = html.replace("%cpRegen%", df.format(cpRegen));
		html = html.replace("%mpRegen%", df.format(mpRegen));
		html = html.replace("%hpDrain%", df.format(hpDrain));
		html = html.replace("%mpDrain%", df.format(mpDrain));
		html = html.replace("%hpGain%", df.format(hpGain));
		html = html.replace("%mpGain%", df.format(mpGain));
		html = html.replace("%critPerc%", df.format(critPerc));
		html = html.replace("%critStatic%", df.format(critStatic));
		html = html.replace("%mCritRate%", df.format(mCritRate));
		html = html.replace("%blowRate%", df.format(blowRate));
		html = html.replace("%shieldDef%", df.format(shieldDef));
		html = html.replace("%shieldRate%", df.format(shieldRate));
		html = html.replace("%fireResist%", df.format(fireResist));
		html = html.replace("%windResist%", df.format(windResist));
		html = html.replace("%waterResist%", df.format(waterResist));
		html = html.replace("%earthResist%", df.format(earthResist));
		html = html.replace("%holyResist%", df.format(holyResist));
		html = html.replace("%darkResist%", df.format(unholyResist));
		html = html.replace("%bleedPower%", df.format(bleedPower));
		html = html.replace("%bleedResist%", df.format(bleedResist));
		html = html.replace("%poisonPower%", df.format(poisonPower));
		html = html.replace("%poisonResist%", df.format(poisonResist));
		html = html.replace("%stunPower%", df.format(stunPower));
		html = html.replace("%stunResist%", df.format(stunResist));
		html = html.replace("%rootPower%", df.format(rootPower));
		html = html.replace("%rootResist%", df.format(rootResist));
		html = html.replace("%sleepPower%", df.format(sleepPower));
		html = html.replace("%sleepResist%", df.format(sleepResist));
		html = html.replace("%paralyzePower%", df.format(paralyzePower));
		html = html.replace("%paralyzeResist%", df.format(paralyzeResist));
		html = html.replace("%mentalPower%", df.format(mentalPower));
		html = html.replace("%mentalResist%", df.format(mentalResist));
		html = html.replace("%debuffPower%", df.format(debuffPower));
		html = html.replace("%debuffResist%", df.format(debuffResist));
		html = html.replace("%cancelPower%", df.format(cancelPower));
		html = html.replace("%cancelResist%", df.format(cancelResist));
		html = html.replace("%swordResist%", df.format(swordResist));
		html = html.replace("%dualResist%", df.format(dualResist));
		html = html.replace("%bluntResist%", df.format(bluntResist));
		html = html.replace("%daggerResist%", df.format(daggerResist));
		html = html.replace("%bowResist%", df.format(bowResist));
		html = html.replace("%crossbowResist%", df.format(crossbowResist));
		html = html.replace("%fistResist%", df.format(fistResist));
		html = html.replace("%poleResist%", df.format(poleResist));
		html = html.replace("%critChanceResist%", df.format(critChanceResist));
		html = html.replace("%critDamResist%", df.format(critDamResist));
		
		NpcHtmlMessage msg = new NpcHtmlMessage();
		msg.setHtml(html);
		player.sendPacket(msg);
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}