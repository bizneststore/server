package l2nl.gameserver.network.l2.s2c.Interface;

import emudev.KeyChecker;
import l2nl.gameserver.network.l2.s2c.L2GameServerPacket;

public class KeyPacket extends L2GameServerPacket {
	byte[] arr;
	
	public KeyPacket sendKey(byte[] key, int size) {
		arr = KeyChecker.getKey(key, size);
		return this;
	}
	
	@Override
	protected void writeImpl() {
		writeEx(0xF5);//ch[0xFE:0xF5]
		writeH(arr.length);
		writeB(arr);
	}
}