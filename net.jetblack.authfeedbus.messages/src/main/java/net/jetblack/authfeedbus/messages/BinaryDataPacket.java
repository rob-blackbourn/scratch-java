package net.jetblack.authfeedbus.messages;

import java.util.UUID;

public class BinaryDataPacket {

	public final UUID Header;
	public byte[] Body;

	public BinaryDataPacket(UUID header, byte[] body) {
		Header = header;
		Body = body;
	}
}
