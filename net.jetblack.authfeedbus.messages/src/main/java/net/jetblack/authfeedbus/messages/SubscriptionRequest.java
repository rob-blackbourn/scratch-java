package net.jetblack.authfeedbus.messages;

import java.io.IOException;

public class SubscriptionRequest extends Message {

	public final String Feed;
	public final String Topic;
	public final boolean IsAdd;

	public SubscriptionRequest(String feed, String topic, boolean isAdd) {
		super(MessageType.SubscriptionRequest);
		Feed = feed;
		Topic = topic;
		IsAdd = isAdd;
	}

	public static SubscriptionRequest readBody(MessageInputStream stream) throws IOException {
		String feed = stream.readUTF();
		String topic = stream.readUTF();
		boolean isAdd = stream.readBoolean();
		return new SubscriptionRequest(feed, topic, isAdd);
	}

	@Override
	public MessageOutputStream write(MessageOutputStream stream) throws IOException {
		super.write(stream);
		stream.writeUTF(Feed);
		stream.writeUTF(Topic);
		stream.writeBoolean(IsAdd);
		return stream;
	}

	@Override
	public String toString() {
		return String.format("%s, Feed=%s, Topic=%s, IsAdd=%b", super.toString(), Feed, Topic, IsAdd);
	}

}
