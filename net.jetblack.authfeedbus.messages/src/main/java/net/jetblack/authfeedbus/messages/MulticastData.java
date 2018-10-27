package net.jetblack.authfeedbus.messages;

import java.io.IOException;

public class MulticastData extends Message {

	public final String Feed;
	public final String Topic;
	public boolean IsImage;
	public final BinaryDataPacket[] Data;

	public MulticastData(String feed, String topic, boolean isImage, BinaryDataPacket[] data) {
		super(MessageType.MulticastData);
		Feed = feed;
		Topic = topic;
		IsImage = isImage;
		Data = data;
	}

	public static MulticastData readBody(MessageInputStream stream) throws IOException {
		String feed = stream.readUTF();
		String topic = stream.readUTF();
		boolean isImage = stream.readBoolean();
		BinaryDataPacket[] data = stream.readBinaryDataPacketArray();
		return new MulticastData(feed, topic, isImage, data);
	}

	@Override
	public MessageOutputStream write(MessageOutputStream stream) throws IOException {
		super.write(stream);
		stream.writeUTF(Feed);
		stream.writeUTF(Topic);
		stream.writeBoolean(IsImage);
		stream.writeBinaryDataPacketArray(Data);
		return stream;
	}

	@Override
	public String toString() {
		return String.format("%s, Feed=%s, Topic=%s, IsImage=%s, Data=%s", super.toString(), Feed, Topic, IsImage,
				Data);
	}
}
