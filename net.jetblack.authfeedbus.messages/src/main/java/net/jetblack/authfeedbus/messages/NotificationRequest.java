package net.jetblack.authfeedbus.messages;

import java.io.IOException;

public class NotificationRequest extends Message {

	public final String Feed;
	public final boolean IsAdd;

	public NotificationRequest(String feed, boolean isAdd) {
		super(MessageType.NotificationRequest);
		Feed = feed;
		IsAdd = isAdd;
	}

	public static NotificationRequest readBody(MessageInputStream stream) throws IOException {
		String feed = stream.readUTF();
		boolean isAdd = stream.readBoolean();
		return new NotificationRequest(feed, isAdd);
	}

	@Override
	public MessageOutputStream write(MessageOutputStream stream) throws IOException {
		super.write(stream);
		stream.writeUTF(Feed);
		stream.writeBoolean(IsAdd);
		return stream;
	}

	@Override
	public String toString() {
		return String.format("%s, Feed=%s, IsAdd=%s", super.toString(), Feed, IsAdd);
	}
}
