package net.jetblack.authfeedbus.distributor.roles;

public enum Role {
    None(0x00),
    Subscribe(0x01),
    Publish(0x02),
    Notify(0x04),
    Authorize(0x08),
    All(Subscribe._value | Publish._value | Notify._value | Authorize._value);
    
    private final int _value;
    
    private Role(int value) {
    	_value = value;
    }
    
    public boolean hasFlag(Role role) {
    	return (_value & role._value) != 0;
    }
    
    public int value() {
    	return _value;
    }
}
