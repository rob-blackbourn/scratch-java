import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;

import net.jetblack.feedbus.adapters.Client;
import net.jetblack.feedbus.adapters.DataReceivedEventArgs;
import net.jetblack.util.EventListener;
import net.jetblack.util.io.StringSerializer;

public class ExampleSubscriber {

	public static void main(String[] args) {
		
		try {
			Client client = Client.Create(Inet4Address.getLocalHost(), 30011, new StringSerializer(), 8096);
			
			client.DataReceived.add(new EventListener<DataReceivedEventArgs>() {
				
				@Override
				public void onEvent(DataReceivedEventArgs event) {
					System.out.println("Data received: " + event.Data);
				}
			});
			
			client.addSubscription("FOO", "BAR");
			
			System.out.println("Press ENTER to quit");
			System.in.read();
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
