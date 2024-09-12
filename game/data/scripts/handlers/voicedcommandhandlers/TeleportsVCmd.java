package handlers.voicedcommandhandlers;

import java.util.concurrent.ThreadLocalRandom;

import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.handler.IVoicedCommandHandler;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.entity.olympiad.OlympiadManager;

import gr.sr.interf.SunriseEvents;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public class TeleportsVCmd implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"giran",
		"dion",
		"aden",
		"goddard",
		"gludio",
		"rune",
		"heine",
		"schuttgart",
		"oren"
	};
	
	private static final int TELEPORT_DELAY = 3000; // Delay in milliseconds (3 seconds)
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (activeChar.getKarma() > 0)
		{
			activeChar.sendMessage("Cannot use while have karma.");
			return false;
		}
		
		if (activeChar.getPvpFlag() > 0)
		{
			activeChar.sendMessage("Cannot use while have pvp flag.");
			return false;
		}
		
		if (activeChar.isInOlympiadMode() || activeChar.inObserverMode() || OlympiadManager.getInstance().isRegistered(activeChar))
		{
			activeChar.sendMessage("Cannot use while in Olympiad.");
			return false;
		}
		
		if (SunriseEvents.isInEvent(activeChar) || SunriseEvents.isRegistered(activeChar))
		{
			activeChar.sendMessage("Cannot use while in event.");
			return false;
		}
		
		if (activeChar.isJailed())
		{
			activeChar.sendMessage("Cannot use while in jail.");
			return false;
		}
		
		if (activeChar.isAlikeDead())
		{
			activeChar.sendMessage("Cannot use while in fake death mode.");
			return false;
		}
		
		if (!activeChar.isInsideZone(ZoneIdType.PEACE) && activeChar.isInCombat())
		{
			activeChar.sendMessage("Cannot use while in combat outside of peace zone.");
			return false;
		}
		if (!activeChar.isPremium() && !activeChar.isPlatinum())
		{
			activeChar.sendMessage("This Command is for Premium Users only!");
			return false;
		}
		
		Location loc = null;
		switch (command)
		{
			case "giran":
				// Get a random number, either 1 or 2
				int randomLocation = ThreadLocalRandom.current().nextInt(1, 3);
				
				// Switch between the two locations based on the random number
				switch (randomLocation)
				{
					case 1:
						loc = new Location(83432, 148296, -3408);
						break;
					case 2:
						loc = new Location(83432, 148984, -3408);
						break;
				}
				break;
			case "dion":
				loc = new Location(15619, 143132, -2705);
				break;
			case "aden":
				loc = new Location(147974, 26883, -2200);
				break;
			case "gludio":
				loc = new Location(-14413, 123044, -3112);
				break;
			case "rune":
				loc = new Location(43759, -48122, -792);
				break;
			case "heine":
				loc = new Location(111381, 218981, -3538);
				break;
			case "goddard":
				loc = new Location(147732, -56554, -2776);
				break;
			case "schuttgart":
				loc = new Location(87355, -142095, -1336);
				break;
			case "oren":
				loc = new Location(82760, 53578, -1491);
				break;
		}
		
		Location loc2 = loc;
		// Schedule the teleport with a delay
		ThreadPoolManager.getInstance().scheduleGeneral(() -> activeChar.teleToLocation(loc2, false), TELEPORT_DELAY);
		activeChar.sendMessage("Teleporting in " + (TELEPORT_DELAY / 1000) + " seconds...");
		// activeChar.teleToLocation(loc, false);
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}