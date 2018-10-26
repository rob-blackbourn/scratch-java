import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;

import net.jetblack.feedbus.adapters.Client;
import net.jetblack.util.io.StringSerializer;

public class ExamplePublisher {

	public static void main(String[] args) {
		
		try {
			Client client = Client.Create(Inet4Address.getLocalHost(), 30011, new StringSerializer(), 8096);
			
			client.Publish("FOO", "BAR", true, "This is not a test");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
