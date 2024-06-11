package handlers.skillhandlers;

import l2r.gameserver.handler.ISkillHandler;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.model.skills.L2SkillType;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.SystemMessage;

public class EnergyReplenish implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS;
	
	@Override
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		final int energy = skill.getEnergyConsume();
		boolean emptyEnergy = false;
		final L2ItemInstance item = activeChar.getInventory().getPaperdollItem(15);
		if (item != null)
		{
			if (item.getAgathionEnergy() == 0)
			{
				emptyEnergy = true;
			}
			if (energy > 0)
			{
				item.setAgathionEnergy(item.getAgathionEnergy() + energy);
				activeChar.sendPacket((SystemMessage.getSystemMessage(SystemMessageId.ENERGY_S1_REPLENISHED)).addInt(energy));
				activeChar.getActingPlayer().sendItemList(false);
				if (emptyEnergy)
				{
					item.decreaseEnergy(false);
				}
			}
			else
			{
				activeChar.sendPacket(SystemMessageId.NOTHING_HAPPENED);
			}
		}
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return EnergyReplenish.SKILL_IDS;
	}
	
	static
	{
		SKILL_IDS = new L2SkillType[]
		{
			L2SkillType.ENERGY_REPLENISH
		};
	}
}
