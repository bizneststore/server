/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package handlers.voicedcommandhandlers;

import java.text.SimpleDateFormat;
import java.util.Map;

import l2r.Config;
import l2r.gameserver.handler.IVoicedCommandHandler;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;

import gr.sr.configsEngine.configs.impl.BufferConfigs;
import gr.sr.configsEngine.configs.impl.PremiumServiceConfigs;
import gr.sr.premiumEngine.PremiumHandler;

public class PremiumVCmd implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		// added account for testing (vmilon)
		"premium",
		"account"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.startsWith("premium") || command.startsWith("account"))
		{
			try
			{
				SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
				// get default buff scheme amount
				int normalBuffScheme = BufferConfigs.MAX_SCHEME_PROFILES;
				
				// get pr buff scheme amount
				int prBuffScheme = PremiumServiceConfigs.PREMIUM_MAX_SCHEME;
				int plBuffScheme = PremiumServiceConfigs.PLATINUM_MAX_SCHEME;
				
				// get pr drop map
				Map<Integer, Float> prSpecialDropMap = PremiumServiceConfigs.PR_RATE_DROP_ITEMS_ID;
				Map<Integer, Float> plSpecialDropMap = PremiumServiceConfigs.PL_RATE_DROP_ITEMS_ID;
				
				// get normal drop map
				Map<Integer, Float> normalSpecialDropMap = Config.RATE_DROP_AMOUNT_MULTIPLIER;
				
				// get pr adena multiplier from pr map (57 is adena item id)
				float PrAdenaMultiplier = prSpecialDropMap.get(57);
				float PlAdenaMultiplier = plSpecialDropMap.get(57);
				
				// get normal adena multiplier from normal map (57 is adena item id)
				float normalAdenaMultiplier = normalSpecialDropMap.containsKey(57) ? normalSpecialDropMap.get(57) : 1.0f;
				
				// get pr drop rate
				int PrAdenaDrop = Math.round(PrAdenaMultiplier * normalAdenaMultiplier);
				int PlAdenaDrop = Math.round(PlAdenaMultiplier * normalAdenaMultiplier);
				
				// NEXT STEP EXPLAINED FOR NEWBIES TO LEARN:
				// This is how to get % difference to show bonus in pr drop rate compared to normal.
				// we need to subtract 1 here because the default PR multiplier is 1 + the percent you specify.
				// (example: PrRateDropItemsById=1.10 in PremiumService.ini is 1.10 x your drop rate multiplier in Rates.ini)
				// so if your adena multiplier was 35, your premium rate would be 1.10 x 35
				// we found this amount in the previous step, so now we just divide by normal drop rate to get % (which is 1.10, or 110%)
				// and we subtract our 1x default pr multiplier, to give us .10, or 10%)
				// then multiply by 100 to give us 10% instead of .10
				
				int PrAdenaDropDiff = Math.round(((PrAdenaDrop / normalAdenaMultiplier) - 1) * 100);
				int PlAdenaDropDiff = Math.round(((PlAdenaDrop / normalAdenaMultiplier) - 1) * 100);
				
				if (activeChar.isPremium())
				{
					long _end_prem_date;
					_end_prem_date = PremiumHandler.getPremServiceData(activeChar.getAccountName());
					NpcHtmlMessage preReply = new NpcHtmlMessage();
					
					StringBuilder html3 = new StringBuilder("<html><body><title>Premium Account Details</title><center>");
					html3.append("<table>");
					html3.append("<tr><td><center>Thank you for supporting <font color=\"00FF00\">L2 - SoE</font>.<br></td></tr>");
					html3.append("<tr><td><center>Account Type:<font color=\"LEVEL\"> GOLD<br></font></td></tr>");
					html3.append("<tr><td>Rate EXP: <font color=\"LEVEL\">x" + Math.round(PremiumServiceConfigs.PREMIUM_RATE_XP) + " (+" + Math.round(PremiumServiceConfigs.PREMIUM_RATE_XP - Config.RATE_XP) + ")<br1></font></td></tr>");
					html3.append("<tr><td>Rate SP: <font color=\"LEVEL\">x" + Math.round(PremiumServiceConfigs.PREMIUM_RATE_SP) + " (+" + Math.round(PremiumServiceConfigs.PREMIUM_RATE_SP - Config.RATE_SP) + ")<br1></font></td></tr>");
					html3.append("<tr><td>Rate Drop: <font color=\"LEVEL\">x" + Math.round(PremiumServiceConfigs.PREMIUM_RATE_DROP_ITEMS) + " (+" + Math.round(PremiumServiceConfigs.PREMIUM_RATE_DROP_ITEMS - Config.RATE_DEATH_DROP_AMOUNT_MULTIPLIER) + ")<br1></font></td></tr>");
					html3.append("<tr><td>Rate Adena: <font color=\"LEVEL\">x" + PrAdenaDrop + " (+" + PrAdenaDropDiff + "%)<br1></font></td></tr><br>");
					// html3.append("<tr><td>Autofarm: <font color=\"70FFCA\">1H</font></td></tr>");
					html3.append("<tr><td>Gold Buffs: <font color=\"LEVEL\">2 Schemes Max.<br><br></font></td></tr>");
					html3.append("<tr><td>Expires: <font color=\"00A5FF\">" + String.valueOf(format.format(_end_prem_date)) + "</font></td></tr>");
					html3.append("<tr><td>Current Date: <font color=\"70FFCA\">" + String.valueOf(format.format(System.currentTimeMillis())) + "<br><br></font></td></tr>");
					html3.append("<tr><td><font color=\"LEVEL\"><center>Premium Info & Rules<br1></font></center></td></tr>");
					html3.append("<tr><td><font color=\"70FFCA\">1. Premium Accounts CAN NOT BE TRANSFERED.<br1></font></td></tr>");
					html3.append("<tr><td><font color=\"70FFCA\">2. Premium Accounts affects ALL characters in same account.<br1></font></td></tr>");
					html3.append("<tr><td><font color=\"70FFCA\">3. Does not affect Party members.<br><br></font></td></tr>");
					html3.append("</table>");
					html3.append("</center></body></html>");
					
					preReply.setHtml(html3.toString());
					activeChar.sendPacket(preReply);
				}
				else if (activeChar.isPlatinum())
				{
					long _end_prem_date;
					_end_prem_date = PremiumHandler.getPremServiceData(activeChar.getAccountName());
					NpcHtmlMessage preReply = new NpcHtmlMessage();
					
					StringBuilder html3 = new StringBuilder("<html><body><title>Premium Account Details</title><center>");
					html3.append("<table>");
					html3.append("<tr><td><center>Thank you for supporting <font color=\"00FF00\">L2 - SoE</font>.<br></td></tr>");
					html3.append("<tr><td><center>Account Type:<font color=\"LEVEL\"> PLATINUM<br></font></td></tr>");
					html3.append("<tr><td>Rate EXP: <font color=\"LEVEL\">x" + Math.round(PremiumServiceConfigs.PLATINUM_RATE_XP) + " (+" + Math.round(PremiumServiceConfigs.PLATINUM_RATE_XP - Config.RATE_XP) + ")<br1></font></td></tr>");
					html3.append("<tr><td>Rate SP: <font color=\"LEVEL\">x" + Math.round(PremiumServiceConfigs.PLATINUM_RATE_SP) + " (+" + Math.round(PremiumServiceConfigs.PLATINUM_RATE_SP - Config.RATE_SP) + ")<br1></font></td></tr>");
					html3.append("<tr><td>Rate Drop: <font color=\"LEVEL\">x" + Math.round(PremiumServiceConfigs.PLATINUM_RATE_DROP_ITEMS) + " (+" + Math.round(PremiumServiceConfigs.PLATINUM_RATE_DROP_ITEMS - Config.RATE_DEATH_DROP_AMOUNT_MULTIPLIER) + ")<br1></font></td></tr>");
					html3.append("<tr><td>Rate Adena: <font color=\"LEVEL\">x" + PlAdenaDrop + " (+" + PlAdenaDropDiff + "%)<br1></font></td></tr><br>");
					// html3.append("<tr><td>Autofarm: <font color=\"70FFCA\">2H</font></td></tr>");
					html3.append("<tr><td>Platinum Buffs: <font color=\"LEVEL\"> 4 Schemes Max.<br><br></font></td></tr>");
					html3.append("<tr><td>Expires: <font color=\"00A5FF\">" + String.valueOf(format.format(_end_prem_date)) + "</font></td></tr>");
					html3.append("<tr><td>Current Date: <font color=\"70FFCA\">" + String.valueOf(format.format(System.currentTimeMillis())) + "<br><br></font></td></tr>");
					html3.append("<tr><td><font color=\"LEVEL\"><center>Premium Info & Rules<br1></font></center></td></tr>");
					html3.append("<tr><td><font color=\"70FFCA\">1. Premium Accounts CAN NOT BE TRANSFERED.<br1></font></td></tr>");
					html3.append("<tr><td><font color=\"70FFCA\">2. Premium Accounts affects ALL characters in same account.<br1></font></td></tr>");
					html3.append("<tr><td><font color=\"70FFCA\">3. Does not affect Party members.<br><br></font></td></tr>");
					html3.append("</table>");
					html3.append("</center></body></html>");
					
					preReply.setHtml(html3.toString());
					activeChar.sendPacket(preReply);
				}
				else
				{
					NpcHtmlMessage preReply = new NpcHtmlMessage();
					StringBuilder html3 = new StringBuilder("<html><body><title>Normal Account</title><center>");
					html3.append("<table>");
					html3.append("<tr><td><center>Account Type: <font color=\"LEVEL\">Normal<br></font></td></tr>");
					html3.append("<tr><td><center>Details<br1></td></tr>");
					html3.append("<tr><td>Rate EXP: <font color=\"00A5FF\">x" + Math.round(Config.RATE_XP) + "<br1></font></td></tr>");
					html3.append("<tr><td>Rate SP: <font color=\"00A5FF\">x" + Math.round(Config.RATE_SP) + "<br1></font></td></tr>");
					html3.append("<tr><td>Rate Drop: <font color=\"00A5FF\">x" + Math.round(Config.RATE_DEATH_DROP_AMOUNT_MULTIPLIER) + "<br1></font></td></tr>");
					html3.append("<tr><td>Rate Adena: <font color=\"00A5FF\">x" + Math.round(normalAdenaMultiplier) + "<br1></font></td></tr><br>");
					
					html3.append("<tr><td>Expires: <font color=\"00A5FF\">Never (Normal Account)<br1></font></td></tr>");
					html3.append("<tr><td>Current Date: <font color=\"70FFCA\">" + String.valueOf(format.format(System.currentTimeMillis())) + " <br><br></font></td></tr><br><br1><br1>");
					html3.append("<tr><td>Upgrade to Premium Account: <font color=\"70FFCA\"> Press Alt+B.</font></td></tr>");
					html3.append("<tr><td>Rate EXP: <font color=\"LEVEL\">x" + Math.round(PremiumServiceConfigs.PREMIUM_RATE_XP) + " (+" + Math.round(PremiumServiceConfigs.PREMIUM_RATE_XP - Config.RATE_XP) + ")<br1></font></td></tr>");
					html3.append("<tr><td>Rate SP: <font color=\"LEVEL\">x" + Math.round(PremiumServiceConfigs.PREMIUM_RATE_SP) + " (+" + Math.round(PremiumServiceConfigs.PREMIUM_RATE_SP - Config.RATE_SP) + ")<br1></font></td></tr>");
					html3.append("<tr><td>Rate Drop: <font color=\"LEVEL\">x" + Math.round(PremiumServiceConfigs.PREMIUM_RATE_DROP_ITEMS) + " (+" + Math.round(PremiumServiceConfigs.PREMIUM_RATE_DROP_ITEMS - Config.RATE_DEATH_DROP_AMOUNT_MULTIPLIER) + ")<br1></font></td></tr>");
					html3.append("<tr><td>Premium Buffs: <font color=\"LEVEL\">1 Scheme Max.<br><br></font></td></tr>");
					html3.append("<tr><td><font color=\"LEVEL\"><center>Premium Info & Rules<br1></font></center></td></tr>");
					html3.append("<tr><td> <font color=\"70FFCA\">1. Premium  benefits CAN NOT BE TRANSFERED.<br1></font></td></tr><br>");
					html3.append("<tr><td> <font color=\"70FFCA\">2. Premium benefits effect ALL characters in same account.<br1></font></td></tr><br>");
					html3.append("<tr><td> <font color=\"70FFCA\">3. Does not affect Party members.</font></td></tr>");
					html3.append("</table>");
					html3.append("</center></body></html>");
					
					preReply.setHtml(html3.toString());
					activeChar.sendPacket(preReply);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
