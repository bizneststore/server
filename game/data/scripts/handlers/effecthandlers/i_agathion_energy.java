package handlers.effecthandlers;

import static l2r.gameserver.enums.EffectCalculationType.DIFF;
import static l2r.gameserver.model.itemcontainer.Inventory.PAPERDOLL_LBRACELET;

import java.util.Arrays;

import l2r.gameserver.data.xml.impl.AgathionData;
import l2r.gameserver.enums.EffectCalculationType;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.agathion.Agathion;
import l2r.gameserver.model.effects.EffectInstant;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.stats.Env;
import l2r.gameserver.network.serverpackets.ExBR_AgathionEnergyInfo;

public final class i_agathion_energy extends EffectInstant
{
	private final double energy;
	private final EffectCalculationType mode;
	
	public i_agathion_energy(Env env, EffectTemplate template)
	{
		super(env, template);
		
		energy = template.getParameters().getInt("energy", 100);
		mode = template.getParameters().getEnum("mode", EffectCalculationType.class, DIFF);
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected().isDead())
		{
			return false;
		}
		
		if (!getEffected().isPlayer())
		{
			return false;
		}
		
		final L2PcInstance target = getEffected().getActingPlayer();
		final Agathion agathionInfo = AgathionData.getInstance().getByNpcId(target.getAgathionId());
		if ((agathionInfo == null) || (agathionInfo.getMaxEnergy() <= 0))
		{
			return false;
		}
		
		final L2ItemInstance agathionItem = target.getInventory().getPaperdollItem(PAPERDOLL_LBRACELET);
		if ((agathionItem == null) || (agathionInfo.getItemId() != agathionItem.getId()))
		{
			return false;
		}
		
		int agathionEnergy = 0;
		switch (mode)
		{
			case DIFF:
			{
				agathionEnergy = (int) Math.max(0, agathionItem.getAgathionRemainingEnergy() + energy);
				break;
			}
			case PER:
			{
				agathionEnergy = (int) ((agathionItem.getAgathionRemainingEnergy() * energy) / 100.0);
				break;
			}
		}
		agathionItem.setAgathionRemainingEnergy(agathionEnergy);
		
		// If item is agathion with energy, then send info to client.
		target.sendPacket(new ExBR_AgathionEnergyInfo(Arrays.asList(agathionItem)));
		return true;
	}
}
