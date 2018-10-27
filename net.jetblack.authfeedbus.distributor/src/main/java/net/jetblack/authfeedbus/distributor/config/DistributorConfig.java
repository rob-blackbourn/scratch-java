package net.jetblack.authfeedbus.distributor.config;

import java.net.InetAddress;
import java.util.List;

import net.jetblack.authfeedbus.distributor.roles.DistributorRole;
import net.jetblack.authfeedbus.distributor.roles.Role;
import net.jetblack.util.Enumerable;

public class DistributorConfig {

	private InetAddress _address;

	public InetAddress getAddress() {
		return _address;
	}

	public void setAddress(InetAddress value) {
		_address = value;
	}

	private int _port;

	public int getPort() {
		return _port;
	}

	public void setPort(int value) {
		_port = value;
	}

	private long _heartbeatInterval;

	public long getHeartbeatInterval() {
		return _heartbeatInterval;
	}

	public void setHeartbeatInterval(long value) {
		_heartbeatInterval = value;
	}

	private Role _allow;

	public Role getAllow() {
		return _allow;
	}

	public void setAllow(Role value) {
		_allow = value;
	}

	private Role _deny;

	public Role getDeny() {
		return _deny;
	}

	public void setDeny(Role value) {
		_deny = value;
	}

	private List<FeedRoleConfig> _feedRoles;

	public List<FeedRoleConfig> getFeedRoles() {
		return _feedRoles;
	}

	public void setFeedRoles(List<FeedRoleConfig> value) {
		_feedRoles = value;
	}

	public DistributorRole ToDistributorRole() {
		return new DistributorRole(_allow, _deny,
				Enumerable.create(_feedRoles).select(x -> x.toFeedRole()).toMap(x -> x.Feed, x -> x));
	}
}
