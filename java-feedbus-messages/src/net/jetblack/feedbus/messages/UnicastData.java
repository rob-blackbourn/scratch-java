package net.jetblack.feedbus.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UnicastData extends Message {

	public final String ClientId;
	public final String Feed;
	public final String Topic;
	public final boolean IsImage;
	public final byte[] Data;

    public UnicastData(String clientId, String feed, String topic, boolean isImage, byte[] data) {
        super(MessageType.UnicastData);
	    ClientId = clientId;
	    Feed = feed;
	    Topic = topic;
	    IsImage = isImage;
	    Data = data;
	}
	
	public static UnicastData readBody(DataInputStream stream) throws IOException {
	    String clientId = stream.readUTF();
	    String feed = stream.readUTF();
	    String topic = stream.readUTF();
	    boolean isImage = stream.readBoolean();
	    int len = stream.readInt();
	    byte[] data = new byte[len];
	    stream.readFully(data);
	    return new UnicastData(clientId, feed, topic, isImage, data);
	}

	@Override
	public DataOutputStream write(DataOutputStream stream) throws IOException {
	    super.write(stream);
	    stream.writeUTF(ClientId);
	    stream.writeUTF(Feed);
	    stream.writeUTF(Topic);
	    stream.writeBoolean(IsImage);
	    stream.writeInt(Data.length);
	    stream.write(Data);
	    return stream;
	}
	
	@Override
	public String toString() {
	    return super.toString() + ", ClientId=" + ClientId + ", Feed=" + Feed + ", Topic=" + Topic + ", IsImage=" + IsImage + ", Data=" + Data;
	}
}
