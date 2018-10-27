package net.jetblack.authfeedbus.distributor.roles;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class DistributorRole {

	public DistributorRole(Role allow, Role deny, Map<String, FeedRole> feedRoles) {
		Allow = allow;
		Deny = deny;
		FeedRoles = feedRoles != null ? feedRoles : new HashMap<String, FeedRole>();
	}

	public final Role Allow;
	public final Role Deny;
	public final Map<String, FeedRole> FeedRoles;

	public boolean hasRole(InetAddress address, String user, String feed, Role role) {
		boolean decision = Allow.hasFlag(role);

		if (Deny.hasFlag(role))
			decision = false;

		FeedRole feedPermission = FeedRoles.get(feed);
		if (feedPermission != null)
			decision = feedPermission.hasRole(address, user, feed, role, decision);

		return decision;
	}

	@Override
	public String toString() {
		return String.format("Allow=%s, Deny=%s, FeedRoles=[%s]", Allow, Deny, FeedRoles);
	}
}
