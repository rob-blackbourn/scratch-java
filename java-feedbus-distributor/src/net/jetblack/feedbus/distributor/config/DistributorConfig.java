package net.jetblack.feedbus.distributor.config;

import java.net.InetAddress;

public class DistributorConfig {

    private InetAddress _address;
    private int _port;
    private long _heartbeatInterval;
    
    public DistributorConfig() {
    }
    
    public DistributorConfig(InetAddress address, int port, long heartbeatInterval) {
    	_address = address;
    	_port = port;
    	_heartbeatInterval = heartbeatInterval;
    }
    
    public InetAddress getAddress() {
    	return _address;
    }
    
    public int getPort() {
    	return _port;
    }
    
    public long getHeartbeatInterval() {
    	return _heartbeatInterval;
    }
    
    public void setAddress(InetAddress value) {
    	_address = value;
    }
    
    public void setPort(int value) {
    	_port = value;
    }
    
    public void setHeartbeatInterval(long value) {
    	_heartbeatInterval = value;
    }

}
