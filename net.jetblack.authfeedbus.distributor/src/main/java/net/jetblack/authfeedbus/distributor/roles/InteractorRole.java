package net.jetblack.authfeedbus.distributor.roles;

import java.net.InetAddress;

public class InteractorRole {
	
	public InteractorRole(InetAddress address, String user, Role allow, Role deny) {
        Address = address;
        User = user;
        Allow = allow;
        Deny = deny;
    }

    public final InetAddress Address;
    public final String User;
    public final Role Allow;
    public final Role Deny;

    public boolean hasRole(Role role, boolean decision) {
        if (Allow.hasFlag(role))
            decision = true;

        if (Deny.hasFlag(role))
            decision = false;

        return decision;
    }

    @Override
    public String toString() {
        return String.format(
        		"Address=%s, User=\"%s\", Allow=%s, Deny=%s", 
        		Address, User, Allow, Deny);
    }

    public static class Key implements Comparable<Key>
    {
        public Key(InetAddress address, String user) {
            Address = address;
            User = user;
        }

        public InetAddress Address;
        public String User;

        public int compareTo(Key other) {
            int diff = Address.toString().compareTo(other.Address.toString());
            if (diff == 0)
                diff = User.compareTo(other.User);
            return diff;
        }

        public boolean equals(Key other) {
            return Address.equals(other.Address) && User.equals(other.User);
        }

        @Override
        public boolean equals(Object obj)
        {
            return obj instanceof Key && equals((Key)obj);
        }

        @Override
        public int hashCode() {
            return (Address == null ? 0 : Address.hashCode()) ^ (User == null ? 0 : User.hashCode());
        }

        @Override
        public String toString() {
            return String.format("Address=%s, User=%s", Address, User);
        }
    }
}
