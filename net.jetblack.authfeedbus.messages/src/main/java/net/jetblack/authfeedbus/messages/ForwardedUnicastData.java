package net.jetblack.authfeedbus.messages;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

public class ForwardedUnicastData extends Message {

	public final String User;
	public final InetAddress Address;
	public final UUID ClientId;
	public final String Feed;
	public final String Topic;
	public final boolean IsImage;
	public final BinaryDataPacket[] Data;

	public ForwardedUnicastData(String user, InetAddress address, UUID clientId, String feed, String topic,
			boolean isImage, BinaryDataPacket[] data) {
		super(MessageType.ForwardedUnicastData);
		User = user;
		Address = address;
		ClientId = clientId;
		Feed = feed;
		Topic = topic;
		IsImage = isImage;
		Data = data;
	}

	public static ForwardedUnicastData readBody(MessageInputStream stream) throws IOException {
		String user = stream.readUTF();
		InetAddress address = stream.readInetAddress();
		UUID clientId = stream.readUUID();
		String feed = stream.readUTF();
		String topic = stream.readUTF();
		boolean isImage = stream.readBoolean();
		BinaryDataPacket[] data = stream.readBinaryDataPacketArray();
		return new ForwardedUnicastData(user, address, clientId, feed, topic, isImage, data);
	}

	public MessageOutputStream Write(MessageOutputStream stream) throws IOException {
		super.write(stream);
		stream.writeUTF(User);
		stream.writeInetAddress(Address);
		stream.writeUUID(ClientId);
		stream.writeUTF(Feed);
		stream.writeUTF(Topic);
		stream.writeBoolean(IsImage);
		stream.writeBinaryDataPacketArray(Data);
		return stream;
	}

	@Override
	public String toString() {
		return String.format("%s, User=%s, Address=%s, ClientId=%s, Feed=%s, Topic=%s, IsImage=%s, Data=%s",
				super.toString(), User, Address, ClientId, Feed, Topic, IsImage, Data);
	}

}
