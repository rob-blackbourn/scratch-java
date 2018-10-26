package net.jetblack.authfeedbus.messages;

import java.io.IOException;
import java.util.UUID;

public class AuthorizationResponse extends Message {

    public AuthorizationResponse(UUID clientId, String feed, String topic, boolean isAuthorizationRequired, UUID[] entitlements) {
        super(MessageType.AuthorizationResponse);
	    ClientId = clientId;
	    Feed = feed;
	    Topic = topic;
	    IsAuthorizationRequired = isAuthorizationRequired;
	    Entitlements = entitlements;
	}

	public UUID ClientId;
	public String Feed;
	public String Topic;
	public boolean IsAuthorizationRequired;
	public UUID[] Entitlements;

	public static AuthorizationResponse readBody(MessageInputStream stream) throws IOException {
	    UUID clientId = stream.readUUID();
	    String feed = stream.readUTF();
	    String topic = stream.readUTF();
	    boolean isAuthorizationRequired = stream.readBoolean();
	    UUID[] entitlements = stream.readUUIDArray();
	    return new AuthorizationResponse(clientId, feed, topic, isAuthorizationRequired, entitlements);
	}
	
	@Override
	public MessageOutputStream write(MessageOutputStream stream) throws IOException {
	    super.write(stream);
	    stream.writeUUID(ClientId);
	    stream.writeUTF(Feed);
	    stream.writeUTF(Topic);
	    stream.writeBoolean(IsAuthorizationRequired);
	    stream.writeUUIDArray(Entitlements);
	    return stream;
	}
	
	@Override
	public String toString() {
	    return String.format(
	    		"%s, ClientId={ClientId}, Feed=%s, Topic=%s, IsAuthorizationRequired=%s, Entitlements=%s",
	    		super.toString(),
	    		ClientId,
	    		Feed,
	    		Topic,
	    		IsAuthorizationRequired,
	    		Entitlements);
	}

}
