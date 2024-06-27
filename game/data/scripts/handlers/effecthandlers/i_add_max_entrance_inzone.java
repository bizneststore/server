package handlers.effecthandlers;

import java.util.ArrayList;
import java.util.List;

import l2r.gameserver.instancemanager.InstanceManager;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.effects.EffectInstant;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.effects.L2EffectType;
import l2r.gameserver.model.stats.Env;

public class i_add_max_entrance_inzone extends EffectInstant
{
	private final int _sharedReuseGroup;
	@SuppressWarnings("unused")
	private final int _attempts;
	
	public i_add_max_entrance_inzone(final Env env, final EffectTemplate template)
	{
		super(env, template);
		this._sharedReuseGroup = template.getParameters().getInt("sharedReuseGroup");
		this._attempts = template.getParameters().getInt("attempts");
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.NONE;
	}
	
	@Override
	public boolean onStart()
	{
		if ((this.getEffected() == null) || !this.getEffected().isPlayer())
		{
			return false;
		}
		final L2PcInstance player = this.getEffected().getActingPlayer();
		
		final List<Integer> instanceids = new ArrayList<>();
		
		// TODO unhardcode these
		switch (this._sharedReuseGroup)
		{
			case 1:
				instanceids.add(57);
				instanceids.add(58);
				instanceids.add(60);
				instanceids.add(61);
				instanceids.add(63);
				instanceids.add(64);
				instanceids.add(66);
				instanceids.add(67);
				instanceids.add(69);
				instanceids.add(70);
				instanceids.add(72);
				break;
			case 2:
				instanceids.add(46);
				instanceids.add(47);
				instanceids.add(48);
				instanceids.add(49);
				instanceids.add(50);
				instanceids.add(51);
				instanceids.add(52);
				instanceids.add(53);
				instanceids.add(54);
				instanceids.add(55);
				instanceids.add(56);
				break;
			case 3:
				instanceids.add(59);
				instanceids.add(62);
				instanceids.add(65);
				instanceids.add(68);
				instanceids.add(71);
				break;
			case 4:
				instanceids.add(73);
				instanceids.add(74);
				instanceids.add(75);
				instanceids.add(76);
				instanceids.add(77);
				instanceids.add(78);
				instanceids.add(79);
				
				instanceids.add(134);
				break;
		}
		
		for (int instanceId : instanceids)
		{
			InstanceManager.getInstance().deleteInstanceTime(player.getObjectId(), instanceId);
		}
		
		// TODO message
		player.sendMessage("Instance restrictions removed.");
		
		// TODO max attemps
		return true;
	}
}
