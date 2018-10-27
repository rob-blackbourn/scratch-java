package net.jetblack.authfeedbus.messages;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

public class ForwardedSubscriptionRequest extends Message {

	public final String User;
	public final InetAddress Address;
	public final UUID ClientId;
	public final String Feed;
	public final String Topic;
	public final boolean IsAdd;

	public ForwardedSubscriptionRequest(String user, InetAddress address, UUID clientId, String feed, String topic,
			boolean isAdd) {
		super(MessageType.ForwardedSubscriptionRequest);
		User = user;
		Address = address;
		ClientId = clientId;
		Feed = feed;
		Topic = topic;
		IsAdd = isAdd;
	}

	public static ForwardedSubscriptionRequest readBody(MessageInputStream stream) throws IOException {
		String user = stream.readUTF();
		InetAddress address = stream.readInetAddress();
		UUID clientId = stream.readUUID();
		String feed = stream.readUTF();
		String topic = stream.readUTF();
		boolean isAdd = stream.readBoolean();
		return new ForwardedSubscriptionRequest(user, address, clientId, feed, topic, isAdd);
	}

	@Override
	public MessageOutputStream write(MessageOutputStream stream) throws IOException {
		super.write(stream);
		stream.writeUTF(User);
		stream.writeInetAddress(Address);
		stream.writeUUID(ClientId);
		stream.writeUTF(Feed);
		stream.writeUTF(Topic);
		stream.writeBoolean(IsAdd);
		return stream;
	}

	@Override
	public String toString() {
		return String.format("%s, User=%s, Address=%s, ClientId=%s, Feed=%s, Topic=%s, IsAdd=%s", super.toString(),
				User, Address, ClientId, Feed, Topic, IsAdd);
	}
}
