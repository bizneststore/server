/*
 * Copyright Β© 2004-2023 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.npc.BlacksmithOfMammon;

import l2r.Config;
import l2r.gameserver.SevenSigns;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;

import ai.npc.AbstractNpcAI;

/**
 * Blacksmith of Mammon AI
 * @author Charus
 */
public final class BlacksmithOfMammon extends AbstractNpcAI
{
	// NPC
	private static final int BLACKSMITH_MAMMON = 31126;
	
	public BlacksmithOfMammon()
	{
		super(BlacksmithOfMammon.class.getSimpleName(), "ai/npc");
		addStartNpc(BLACKSMITH_MAMMON);
		addFirstTalkId(BLACKSMITH_MAMMON);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.endsWith(".htm"))
		{
			return "blacksmith_of_mammon" + event;
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		final int compWinner = SevenSigns.getInstance().getCabalHighestScore();
		final int sealGnosisOwner = SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_GNOSIS);
		final int playerCabal = SevenSigns.getInstance().getPlayerCabal(player.getObjectId());
		
		if (Config.ALT_STRICT_SEVENSIGNS)
		{
			switch (compWinner)
			{
				case SevenSigns.CABAL_DAWN:
					if ((playerCabal != compWinner) || (playerCabal != sealGnosisOwner))
					{
						return "blacksmith_of_mammon002.htm";
					}
					break;
				case SevenSigns.CABAL_DUSK:
					if ((playerCabal != compWinner) || (playerCabal != sealGnosisOwner))
					{
						return "blacksmith_of_mammon002.htm";
					}
					break;
			}
		}
		
		return "blacksmith_of_mammon001.htm";
	}
}