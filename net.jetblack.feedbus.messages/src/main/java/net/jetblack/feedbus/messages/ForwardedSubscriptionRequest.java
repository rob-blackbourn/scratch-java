package net.jetblack.feedbus.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ForwardedSubscriptionRequest extends Message {

    public final String ClientId;
    public final String Feed;
    public final String Topic;
    public final boolean IsAdd;
    
	public ForwardedSubscriptionRequest(String clientId, String feed, String topic, boolean isAdd) {
		super(MessageType.ForwardedSubscriptionRequest);
        ClientId = clientId;
        Feed = feed;
        Topic = topic;
        IsAdd = isAdd;
    }

    public static ForwardedSubscriptionRequest readBody(DataInputStream stream) throws IOException {
        String clientId = stream.readUTF();
        String feed = stream.readUTF();
        String topic = stream.readUTF();
        boolean isAdd = stream.readBoolean();
        return new ForwardedSubscriptionRequest(clientId, feed, topic, isAdd);
    }

    @Override
    public DataOutputStream write(DataOutputStream stream) throws IOException {
    	super.write(stream);
        stream.writeUTF(ClientId);
        stream.writeUTF(Feed);
        stream.writeUTF(Topic);
        stream.writeBoolean(IsAdd);
        return stream;
    }

    @Override
    public String toString() {
        return super.toString() + ", ClientId=" + ClientId + ", Feed=" + Feed + ", Topic=" + Topic + ", IsAdd=" + IsAdd;
    }
}
