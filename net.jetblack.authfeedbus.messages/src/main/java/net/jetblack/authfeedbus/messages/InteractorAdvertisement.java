package net.jetblack.authfeedbus.messages;

import java.io.IOException;
import java.net.InetAddress;

public class InteractorAdvertisement extends Message {

	public final String User;
	public final InetAddress Address;
	public final boolean IsJoining;

	public InteractorAdvertisement(String user, InetAddress address, boolean isJoining) {
		super(MessageType.InteractorAdvertisement);
		User = user;
		Address = address;
		IsJoining = isJoining;
	}

	public static InteractorAdvertisement readBody(MessageInputStream stream) throws IOException {
		String user = stream.readUTF();
		InetAddress address = stream.readInetAddress();
		boolean isJoining = stream.readBoolean();
		return new InteractorAdvertisement(user, address, isJoining);
	}

	@Override
	public MessageOutputStream write(MessageOutputStream stream) throws IOException {
		super.write(stream);
		stream.writeUTF(User);
		stream.writeInetAddress(Address);
		stream.writeBoolean(IsJoining);
		return stream;
	}

	@Override
	public String toString() {
		return String.format("%s, User=%s, Address=%s, IsJoining=%b", super.toString(), User, Address, IsJoining);
	}

}
