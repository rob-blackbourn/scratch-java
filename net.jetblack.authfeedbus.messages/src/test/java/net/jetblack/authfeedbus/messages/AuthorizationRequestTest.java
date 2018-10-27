package net.jetblack.authfeedbus.messages;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.UUID;

import org.junit.Test;

public class AuthorizationRequestTest {

	@Test
	public void smokeTest() {
		try {
			AuthorizationRequest message = new AuthorizationRequest(UUID.randomUUID(), Inet4Address.getLocalHost(), "rob", "MyFeed", "MyTopic");
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			message.write(new MessageOutputStream(outputStream));
			outputStream.toByteArray();
			ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
			Message roundtrip = Message.read(new MessageInputStream(inputStream));
			assertEquals(message.Type, roundtrip.Type);
			AuthorizationRequest message1 = (AuthorizationRequest)roundtrip;
			assertEquals(message.ClientId, message1.ClientId);
			
			String s = message.toString();
			System.out.println(s);
			
		} catch (UnknownHostException e) {
			fail("threw unknown host");
		} catch (IOException e) {
			fail("threw io exception");
		}
	}

}
