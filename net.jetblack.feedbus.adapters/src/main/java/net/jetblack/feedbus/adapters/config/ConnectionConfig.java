package net.jetblack.feedbus.adapters.config;

import java.net.InetAddress;

public class ConnectionConfig {

    private String _name;
    
    public String getName() {
    	return _name;
    }
    
    public void setName(String value) {
    	_name = value;
    }

    private InetAddress _address;

    public InetAddress getAddress() {
    	return _address;
    }
    
    public void setAddress(InetAddress  value)  {
    	_address = value;
    }
    
    private int _port;
    public int getPort() {
    	return _port;
    }
    
    public void setPort(int value) {
    	_port = value;
    }

    private Class<?> _byteSerializerType;

    public Class<?> getByteSerializerType() {
    	return _byteSerializerType;
    }
    
    public void setByteSerializerType(Class<?> value) {
    	_byteSerializerType = value;
    }

    @Override
    public String toString() {
        return String.format(
        		"Name=\"{Name}\", Address={Address}, Port={Port}, ByteEncoderType={ByteEncoderType}", 
        		getName(), 
        		getAddress(),
        		getPort(), 
        		getByteSerializerType());
    }
}
