package net.jetblack.authfeedbus.messages;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.UUID;

public class MessageOutputStream extends DataOutputStream {

	public MessageOutputStream(OutputStream out) {
		super(out);
	}

	public void writeByteArray(byte[] array) throws IOException {
		writeInt(array.length);
		write(array);
	}

	public void writeUUID(UUID value) throws IOException {
		writeLong(value.getMostSignificantBits());
		writeLong(value.getLeastSignificantBits());
	}

	public void writeUUIDArray(UUID[] array) throws IOException {
		writeInt(array.length);
		for (int i = 0; i < array.length; ++i) {
			writeUUID(array[i]);
		}
	}

	public void writeInetAddress(InetAddress value) throws IOException {
		byte[] addressBytes = value.getAddress();
		writeInt(addressBytes.length);
		write(addressBytes);
	}

	public void writeInetAddressArray(InetAddress[] array) throws IOException {
		writeInt(array.length);
		for (int i = 0; i < array.length; ++i) {
			writeInetAddress(array[i]);
		}
	}

	public void writeBinaryDataPacket(BinaryDataPacket dataPacket) throws IOException {
		writeUUID(dataPacket.Header);
		writeByteArray(dataPacket.Body);
	}

	public void writeBinaryDataPacketArray(BinaryDataPacket[] array) throws IOException {
		if (array == null)
			writeInt(0);
		else {
			writeInt(array.length);
			for (int i = 0; i < array.length; ++i)
				writeBinaryDataPacket(array[i]);
		}
	}
}
