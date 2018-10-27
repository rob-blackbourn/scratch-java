package net.jetblack.authfeedbus.messages;

import java.io.IOException;
import java.util.UUID;

public class UnicastData extends Message {

	public final UUID ClientId;
	public final String Feed;
	public final String Topic;
	public final boolean IsImage;
	public final BinaryDataPacket[] Data;

	public UnicastData(UUID clientId, String feed, String topic, boolean isImage, BinaryDataPacket[] data) {
		super(MessageType.UnicastData);
		ClientId = clientId;
		Feed = feed;
		Topic = topic;
		IsImage = isImage;
		Data = data;
	}

	public static UnicastData readBody(MessageInputStream stream) throws IOException {
		UUID clientId = stream.readUUID();
		String feed = stream.readUTF();
		String topic = stream.readUTF();
		boolean isImage = stream.readBoolean();
		BinaryDataPacket[] data = stream.readBinaryDataPacketArray();
		return new UnicastData(clientId, feed, topic, isImage, data);
	}

	@Override
	public MessageOutputStream write(MessageOutputStream stream) throws IOException {
		super.write(stream);
		stream.writeUUID(ClientId);
		stream.writeUTF(Feed);
		stream.writeUTF(Topic);
		stream.writeBoolean(IsImage);
		stream.writeBinaryDataPacketArray(Data);
		return stream;
	}

	@Override
	public String toString() {
		return String.format("%s, ClientId=%s, Feed=%s, Topic=%s, IsImage=%b, Data=%s", super.toString(), ClientId,
				Feed, Topic, IsImage, Data);
	}

}
