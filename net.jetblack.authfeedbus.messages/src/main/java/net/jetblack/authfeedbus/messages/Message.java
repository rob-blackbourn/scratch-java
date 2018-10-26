package net.jetblack.authfeedbus.messages;

import java.io.IOException;

public abstract class Message {

	public final MessageType Type;
	
	protected Message(MessageType type) {
		Type = type;
	}

    public static Message read(MessageInputStream stream) throws IOException {
        MessageType type = readHeader(stream);

        switch (type)
        {
            case InteractorAdvertisement:
                return InteractorAdvertisement.ReadBody(stream);
            case AuthorizationRequest:
                return AuthorizationRequest.ReadBody(stream);
            case AuthorizationResponse:
                return AuthorizationResponse.ReadBody(stream);
            case MulticastData:
                return MulticastData.ReadBody(stream);
            case UnicastData:
                return UnicastData.ReadBody(stream);
            case ForwardedMulticastData:
                return ForwardedMulticastData.ReadBody(stream);
            case ForwardedUnicastData:
                return ForwardedUnicastData.ReadBody(stream);
            case ForwardedSubscriptionRequest:
                return ForwardedSubscriptionRequest.ReadBody(stream);
            case NotificationRequest:
                return NotificationRequest.ReadBody(stream);
            case SubscriptionRequest:
                return SubscriptionRequest.ReadBody(stream);
            default:
                throw new IOException("unknown message type");
        }
    }

	public static MessageType readHeader(MessageInputStream stream) throws IOException {
		Byte b = stream.readByte();
		return MessageType.values()[b];
	}
	
    public MessageOutputStream write(MessageOutputStream stream) throws IOException {
        stream.write((byte)Type.ordinal());
        return stream;
    }

    @Override
    public String toString() {
        return "MessageType=" + Type;
    }
}
