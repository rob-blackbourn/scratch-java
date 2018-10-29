package net.jetblack.messagebus.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MulticastData extends Message {

	public final String Feed;
	public final String Topic;
	public final boolean IsImage;
	public final byte[] Data;

    public MulticastData(String feed, String topic, boolean isImage, byte[] data) {
    	super(MessageType.MulticastData);
	    Feed = feed;
	    Topic = topic;
	    IsImage = isImage;
	    Data = data;
	}

	public static MulticastData readBody(DataInputStream stream) throws IOException {
	    String feed = stream.readUTF();
	    String topic = stream.readUTF();
	    boolean isImage = stream.readBoolean();
	    int len = stream.readInt();
	    byte[] data = new byte[len];
	    stream.readFully(data);
	    
	    return new MulticastData(feed, topic, isImage, data);
	}
	
	@Override
	public DataOutputStream write(DataOutputStream stream) throws IOException {
	    super.write(stream);
	    stream.writeUTF(Feed);
	    stream.writeUTF(Topic);
	    stream.writeBoolean(IsImage);
	    stream.writeInt(Data.length);
	    stream.write(Data);
	    return stream;
	}
	
	@Override
	public String toString() {
	    return super.toString() + ", Feed=" + Feed + ", Topic=" + Topic + ", IsImage=" + IsImage + ", Data=" + Data;
	}
}
