package net.jetblack.feedbus.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
	
	public static SubscriptionRequest readBody(DataInputStream stream) throws IOException {
	    String feed = stream.readUTF();
	    String topic = stream.readUTF();
	    boolean isAdd = stream.readBoolean();
	    return new SubscriptionRequest(feed, topic, isAdd);
	}
	
	@Override
	public DataOutputStream write(DataOutputStream stream) throws IOException {
	    super.write(stream);
	    stream.writeUTF(Feed);
	    stream.writeUTF(Topic);
	    stream.writeBoolean(IsAdd);
	    return stream;
	}
	
	@Override
	public String toString() {
	    return super.toString() + ", Feed=" + Feed + ", Topic=" + Topic + ", IsAdd=" + IsAdd;
	}
}
