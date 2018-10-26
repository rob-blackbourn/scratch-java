package net.jetblack.feedbus.distributor;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public class Program {

	public static void main(String[] args) {
	}

    static Server CreateServer(String[] args) throws UnknownHostException {
    	
        var server = new Server(Inet4Address.getByName("0.0.0.0"), 3000);
        server.start(1000);

        return server;
    }
}
