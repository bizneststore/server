package handlers.effecthandlers;

import l2r.gameserver.enums.TeleportWhereType;
import l2r.gameserver.instancemanager.MapRegionManager;
import l2r.gameserver.model.effects.EffectInstant;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.effects.L2EffectType;
import l2r.gameserver.model.stats.Env;

public class Escape extends EffectInstant
{
	private final TeleportWhereType _escapeType;
	
	public Escape(final Env env, final EffectTemplate template)
	{
		super(env, template);
		this._escapeType = ((template.getParameters() == null) ? null : ((TeleportWhereType) template.getParameters().getEnum("escapeType", TeleportWhereType.class, null)));
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.TELEPORT;
	}
	
	@Override
	public boolean onStart()
	{
		// if (((this._escapeType == null) && (this.getSkill().getId() != 2588)) || ((this.getSkill().getId() == 2588) && this.getEffected().isPlayer() && (this.getEffected().getActingPlayer().getBookmarkLocation() == null)))
		// {
		// return false;
		// }
		// if ((this.getSkill().getId() == 1255) && this.getEffected().isPlayer() && this.getEffected().getActingPlayer().getBlockPartyRecall())
		// {
		// this.getEffected().addScript(new SummonRequestHolder(this.getEffector().getActingPlayer(), this.getSkill(), false));
		// final ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.C1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT.getId());
		// confirm.addCharName(this.getEffector());
		// final Location loc = MapRegionManager.getInstance().getTeleToLocation(this.getEffected(), this._escapeType);
		// confirm.addZoneName(loc.getX(), loc.getY(), loc.getZ());
		// confirm.addTime(30000);
		// confirm.addRequesterId(this.getEffector().getObjectId());
		// this.getEffected().sendPacket(confirm);
		// }
		// else
		{
			// if (this.getEffected().getInstanceId() != 0)
			// {
			// this.getEffected().setInstanceId(0);
			// }
			// if ((this.getSkill().getId() == 2588) && this.getEffected().isPlayer())
			// {
			// this.getEffected().teleToLocation(this.getEffected().getActingPlayer().getBookmarkEx().getBookmarkLocation(), false);
			// this.getEffected().getActingPlayer().getBookmarkEx().setBookmarkLocation(null);
			// }
			// else
			{
				this.getEffected().teleToLocation(MapRegionManager.getInstance().getTeleToLocation(this.getEffected(), this._escapeType), true);
			}
		}
		return true;
	}
}
