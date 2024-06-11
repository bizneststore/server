package handlers.bypasshandlers;

import java.util.List;

import l2r.Config;
import l2r.gameserver.data.xml.impl.SkillTreesData;
import l2r.gameserver.features.rebirth.RebirthEngineConfigs;
import l2r.gameserver.handler.IBypassHandler;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2NpcInstance;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.base.ClassId;
import l2r.gameserver.model.events.EventDispatcher;
import l2r.gameserver.model.events.impl.character.player.OnPlayerLearnSkillRequested;
import l2r.gameserver.network.serverpackets.ActionFailed;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Learn Skill.
 * @author Zoey76
 * @version 2.6.3.0
 */
public class LearnSkill implements IBypassHandler
{
	
	private static final Logger LOG = LoggerFactory.getLogger(LearnSkill.class);
	
	private static final String[] COMMANDS =
	{
		"SkillList",
		"learn_skill"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		if (!(target instanceof L2NpcInstance))
		{
			return false;
		}
		
		if (command.startsWith(COMMANDS[0]))
		{
			if (target.getId() == RebirthEngineConfigs.REBIRTH_SKILL_SELLER_ID)
			{
				L2NpcInstance.showSkillList(activeChar, (L2Npc) target, activeChar.getClassId());
				return true;
			}
			
			if (Config.ALT_GAME_SKILL_LEARN)
			{
				showCustomLearnSkill(command, activeChar, (L2NpcInstance) target);
				return true;
			}
			
			L2NpcInstance.showSkillList(activeChar, (L2Npc) target, activeChar.getClassId());
		}
		else if (command.equals(COMMANDS[1]))
		{
			EventDispatcher.getInstance().notifyEventAsync(new OnPlayerLearnSkillRequested(activeChar), target);
		}
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
	
	private static void showCustomLearnSkill(String command, L2PcInstance player, L2NpcInstance npc)
	{
		try
		{
			final String id = command.substring(9).trim();
			if (id.length() != 0)
			{
				L2NpcInstance.showSkillList(player, npc, ClassId.getClassId(Integer.parseInt(id)));
			}
			else
			{
				boolean own_class = false;
				final List<ClassId> classesToTeach = npc.getClassesToTeach();
				for (ClassId cid : classesToTeach)
				{
					if (cid.equalsOrChildOf(player.getClassId()))
					{
						own_class = true;
						break;
					}
				}
				
				final StringBuilder text = new StringBuilder("<html><body><center>Skill learning:</center><br>");
				if (!own_class)
				{
					text.append("Skills of your class are the easiest to learn.<br>").append("Skills of another class of your race are a little harder.<br>").append("Skills for classes of another race are extremely difficult.<br>").append("But the hardest of all to learn are the ").append(player.getClassId().isMage() ? "fighter" : "mage").append("skills!<br>");
				}
				
				// make a list of classes
				if (!classesToTeach.isEmpty())
				{
					int count = 0;
					ClassId classCheck = player.getClassId();
					
					while ((count == 0) && (classCheck != null))
					{
						for (ClassId cid : classesToTeach)
						{
							if (cid.level() > classCheck.level())
							{
								continue;
							}
							
							if (SkillTreesData.getInstance().getAvailableSkills(player, cid, false, false).isEmpty())
							{
								continue;
							}
							
							text.append("<a action=\"bypass -h npc_%objectId%_SkillList ").append(cid.getId()).append("\">Learn ").append(cid).append("'s class Skills</a><br>\n");
							count++;
						}
						classCheck = classCheck.getParent();
					}
					classCheck = null;
				}
				else
				{
					text.append("No Skills.<br>");
				}
				text.append("</body></html>");
				
				final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				html.setHtml(text.toString());
				html.replace("%objectId%", String.valueOf(npc.getObjectId()));
				player.sendPacket(html);
				
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
		}
		catch (Exception ex)
		{
			LOG.warn("Exception using bypass!", ex);
		}
	}
}
