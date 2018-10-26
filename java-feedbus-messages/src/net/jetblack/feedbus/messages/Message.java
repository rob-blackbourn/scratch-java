package net.jetblack.feedbus.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Message {
	
	public final MessageType Type;
	
	protected Message(MessageType type) {
		Type = type;
	}
	
	public static Message read(DataInputStream stream) throws IOException {
		MessageType type = readHeader(stream);
		
		switch (type) {
		case MulticastData:
			return MulticastData.readBody(stream);
        case UnicastData:
            return UnicastData.readBody(stream);
        case ForwardedSubscriptionRequest:
            return ForwardedSubscriptionRequest.readBody(stream);
        case NotificationRequest:
            return NotificationRequest.readBody(stream);
        case SubscriptionRequest:
            return SubscriptionRequest.readBody(stream);
        case MonitorRequest:
            return MonitorRequest.readBody(stream);
        default:
            throw new IOException("unknown message type");			
		}
	}

	public static MessageType readHeader(DataInputStream stream) throws IOException {
		Byte b = stream.readByte();
		return MessageType.values()[b];
	}
	
    public DataOutputStream write(DataOutputStream stream) throws IOException {
        stream.write((byte)Type.ordinal());
        return stream;
    }

    @Override
    public String toString() {
        return "MessageType=" + Type;
    }
}
