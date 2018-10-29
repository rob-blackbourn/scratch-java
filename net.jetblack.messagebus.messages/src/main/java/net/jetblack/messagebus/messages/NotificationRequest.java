package net.jetblack.messagebus.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NotificationRequest extends Message {

	public final String Feed;
	public final boolean IsAdd;

    public NotificationRequest(String feed, boolean isAdd) {
        super(MessageType.NotificationRequest);
	    Feed = feed;
	    IsAdd = isAdd;
	}

	public static NotificationRequest readBody(DataInputStream stream) throws IOException {
	    String feed = stream.readUTF();
	    boolean isAdd = stream.readBoolean();
	    return new NotificationRequest(feed, isAdd);
	}
	
	@Override
	public DataOutputStream write(DataOutputStream stream) throws IOException {
	    super.write(stream);
	    stream.writeUTF(Feed);
	    stream.writeBoolean(IsAdd);
	    return stream;
	}
	
	@Override
	public String toString() {
	    return super.toString() + ", Feed=" + Feed + ", IsAdd=" + IsAdd;
	}
}
