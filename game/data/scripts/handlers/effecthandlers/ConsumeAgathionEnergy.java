package handlers.effecthandlers;

import java.util.Arrays;

import l2r.gameserver.data.xml.impl.AgathionData;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.agathion.Agathion;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.effects.OverTimeEffect;
import l2r.gameserver.model.itemcontainer.Inventory;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.skills.TickManager;
import l2r.gameserver.model.stats.Env;
import l2r.gameserver.network.serverpackets.ExBR_AgathionEnergyInfo;

public class ConsumeAgathionEnergy extends OverTimeEffect
{
	private final int energy;
	@SuppressWarnings("unused")
	private final int ticks;
	
	public ConsumeAgathionEnergy(Env env, EffectTemplate template)
	{
		super(env, template);
		
		energy = template.getParameters().getInt("energy", 0);
		ticks = template.getParameters().getInt("ticks", 0);
	}
	
	@Override
	public boolean onStart()
	{
		onTick();
		
		return super.onStart();
	}
	
	@Override
	public boolean onTick()
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
		
		final L2ItemInstance agathionItem = target.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LBRACELET);
		if ((agathionItem == null) || (agathionInfo.getItemId() != agathionItem.getId()))
		{
			return false;
		}
		
		final double consumed = energy * getTicksMultiplier();
		if ((consumed < 0) && ((agathionItem.getAgathionRemainingEnergy() + consumed) <= 0))
		{
			return false;
		}
		agathionItem.setAgathionRemainingEnergy(agathionItem.getAgathionRemainingEnergy() + (int) consumed);
		
		// If item is agathion with energy, then send info to client.
		getEffected().sendPacket(new ExBR_AgathionEnergyInfo(Arrays.asList(agathionItem)));
		
		TickManager.getInstance().addEffectPerTickTask(getSkill(), this);
		return true;
	}
}