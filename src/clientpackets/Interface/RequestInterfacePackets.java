package l2nl.gameserver.network.l2.c2s.Interface;

import l2nl.gameserver.network.l2.s2c.Interface.*;
import l2nl.gameserver.model.Player;
import l2nl.gameserver.network.l2.c2s.L2GameClientPacket;

//Opcodes: D0[c]:83[h]:10[d]
public class RequestInterfacePackets extends L2GameClientPacket 
{
	byte[] data = null;
	int data_size;
	
	@Override
	protected void readImpl() {
		if(_buf.remaining() > 2) {
			data_size = readH();
			if(_buf.remaining() >= data_size) {
				data = new byte[data_size];
				readB(data);
			}
		}
	}

	@Override
	protected void runImpl() {
		Player activeChar = getClient().getActiveChar();

		if(activeChar == null)
			return;
		activeChar.sendPacket(new KeyPacket().sendKey(data, data_size));
		activeChar.sendPacket(new ConfigPacket());
		activeChar.sendPacket(new CustomFontsPacket().sendFontInfos());
		activeChar.sendPacket(new ScreenTextInfoPacket().sendTextInfos());
	}
}
