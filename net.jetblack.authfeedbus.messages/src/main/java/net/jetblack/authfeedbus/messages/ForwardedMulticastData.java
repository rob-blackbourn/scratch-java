package net.jetblack.authfeedbus.messages;

import java.io.IOException;
import java.net.InetAddress;

public class ForwardedMulticastData extends Message {

    public ForwardedMulticastData(String user, InetAddress address, String feed, String topic, boolean isImage, BinaryDataPacket[] data) {
        super(MessageType.ForwardedMulticastData);
	    User = user;
	    Address = address;
	    Feed = feed;
	    Topic = topic;
	    IsImage = isImage;
	    Data = data;
	}

	public final String User;
	public final InetAddress Address;
	public final String Feed;
	public final String Topic;
	public final boolean IsImage;
	public final BinaryDataPacket[] Data;

	public static ForwardedMulticastData readBody(MessageInputStream stream) throws IOException {
	    String user = stream.readUTF();
	    InetAddress address = stream.readInetAddress();
	    String feed = stream.readUTF();
	    String topic = stream.readUTF();
	    boolean isImage = stream.readBoolean();
	    BinaryDataPacket[] data = stream.readBinaryDataPacketArray();
	    return new ForwardedMulticastData(user, address, feed, topic, isImage, data);
	}

	@Override
	public MessageOutputStream write(MessageOutputStream stream) throws IOException {
	    super.write(stream);
	    stream.writeUTF(User);
	    stream.writeInetAddress(Address);
	    stream.writeUTF(Feed);
	    stream.writeUTF(Topic);
	    stream.writeBoolean(IsImage);
	    stream.writeBinaryDataPacketArray(Data);
	    return stream;
	}

	@Override
	public String toString() {
	    return String.format(
	    		"%s, User=%s, Address=%s, Feed=%s, Topic=%s, IsImage=%s, Data=%s",
	    		super.toString(),
	    		User,
	    		Address,
	    		Feed,
	    		Topic,
	    		IsImage,
	    		Data);
	}

}
