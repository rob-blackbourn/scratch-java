package net.jetblack.authfeedbus.messages;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

public class AuthorizationRequest extends Message {

    public AuthorizationRequest(UUID clientId, InetAddress address, String user, String feed, String topic) {
        super(MessageType.AuthorizationRequest);
	    ClientId = clientId;
	    Address = address;
	    User = user;
	    Feed = feed;
	    Topic = topic;
	}

	public final UUID ClientId;
	public final InetAddress Address;
	public final String User;
	public final String Feed;
	public final String Topic;

	public static AuthorizationRequest readBody(MessageInputStream stream) throws IOException {
	    UUID clientId = stream.readUUID();
	    InetAddress address = stream.readInetAddress();
	    String user = stream.readUTF();
	    String feed = stream.readUTF();
	    String topic = stream.readUTF();
	    return new AuthorizationRequest(clientId, address, user, feed, topic);
	}

	@Override
	public MessageOutputStream write(MessageOutputStream stream) throws IOException {
	    super.write(stream);
	    stream.writeUUID(ClientId);
	    stream.writeInetAddress(Address);
	    stream.writeUTF(User);
	    stream.writeUTF(Feed);
	    stream.writeUTF(Topic);
	    return stream;
	}
	
	@Override
	public String toString() {
	    return String.format("%s, ClientId=%s, Address=%s, User=%s, Feed=%s, Topic=%s", super.toString(), ClientId, Address, User, Feed, Topic);
	}
}
