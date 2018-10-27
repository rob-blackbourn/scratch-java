package net.jetblack.authfeedbus.messages;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.UUID;

public class MessageInputStream extends DataInputStream {

	public MessageInputStream(InputStream in) {
		super(in);
	}

	public byte[] readByteArray() throws IOException {
		int len = readInt();
		byte[] array = new byte[len];
		readFully(array);
		return array;
	}

	public UUID readUUID() throws IOException {
		long clientIdMostSigBits = readLong();
		long clientIdLeastSigBits = readLong();
		return new UUID(clientIdMostSigBits, clientIdLeastSigBits);
	}

	public UUID[] readUUIDArray() throws IOException {
		int len = readInt();
		UUID[] array = new UUID[len];
		for (int i = 0; i < len; ++i) {
			array[i] = readUUID();
		}
		return array;
	}

	public InetAddress readInetAddress() throws IOException {
		int addressLength = readInt();
		byte[] addressBytes = new byte[addressLength];
		readFully(addressBytes);
		return InetAddress.getByAddress(addressBytes);
	}

	public InetAddress[] readInetAddressArray() throws IOException {
		int len = readInt();
		InetAddress[] array = new InetAddress[len];
		for (int i = 0; i < len; ++i) {
			array[i] = readInetAddress();
		}
		return array;
	}

	public BinaryDataPacket readBinaryDataPacket() throws IOException {
		UUID header = readUUID();
		byte[] body = readByteArray();
		return new BinaryDataPacket(header, body);
	}

	public BinaryDataPacket[] readBinaryDataPacketArray() throws IOException {
		int len = readInt();
		if (len == 0)
			return null;

		BinaryDataPacket[] array = new BinaryDataPacket[len];
		for (int i = 0; i < len; ++i)
			array[i] = readBinaryDataPacket();
		return array;
	}
}
