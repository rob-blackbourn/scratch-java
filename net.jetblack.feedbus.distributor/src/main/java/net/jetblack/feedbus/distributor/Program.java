package net.jetblack.feedbus.distributor;

import java.net.Inet4Address;

public class Program {

	public static void main(String[] args) {
    	
		try {
			Server server = new Server(Inet4Address.getByName("0.0.0.0"), 30011, 8096);
	        server.start(1000);
	        
	        System.out.println("Press <ENTER> to quit");
	        System.in.read();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
