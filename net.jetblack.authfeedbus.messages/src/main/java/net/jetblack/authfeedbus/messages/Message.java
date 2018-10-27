package net.jetblack.authfeedbus.messages;

import java.io.IOException;

public abstract class Message {

	public final MessageType Type;

	protected Message(MessageType type) {
		Type = type;
	}

	public static Message read(MessageInputStream stream) throws IOException {
		MessageType type = readHeader(stream);

		switch (type) {
		case InteractorAdvertisement:
			return InteractorAdvertisement.readBody(stream);
		case AuthorizationRequest:
			return AuthorizationRequest.readBody(stream);
		case AuthorizationResponse:
			return AuthorizationResponse.readBody(stream);
		case MulticastData:
			return MulticastData.readBody(stream);
		case UnicastData:
			return UnicastData.readBody(stream);
		case ForwardedMulticastData:
			return ForwardedMulticastData.readBody(stream);
		case ForwardedUnicastData:
			return ForwardedUnicastData.readBody(stream);
		case ForwardedSubscriptionRequest:
			return ForwardedSubscriptionRequest.readBody(stream);
		case NotificationRequest:
			return NotificationRequest.readBody(stream);
		case SubscriptionRequest:
			return SubscriptionRequest.readBody(stream);
		default:
			throw new IOException("unknown message type");
		}
	}

	public static MessageType readHeader(MessageInputStream stream) throws IOException {
		Byte b = stream.readByte();
		return MessageType.values()[b];
	}

	public MessageOutputStream write(MessageOutputStream stream) throws IOException {
		stream.write((byte) Type.ordinal());
		return stream;
	}

	@Override
	public String toString() {
		return "MessageType=" + Type;
	}
}
