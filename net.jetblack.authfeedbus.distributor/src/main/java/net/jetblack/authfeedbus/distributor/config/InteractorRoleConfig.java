package net.jetblack.authfeedbus.distributor.config;

import java.net.InetAddress;

import net.jetblack.authfeedbus.distributor.roles.InteractorRole;
import net.jetblack.authfeedbus.distributor.roles.Role;

public class InteractorRoleConfig {

	private InetAddress _address;

	public InetAddress getAddress() {
		return _address;
	}

	public void setAddress(InetAddress value) {
		_address = value;
	}

	private String _user;

	public String getUser() {
		return _user;
	}

	public void setUser(String value) {
		_user = value;
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

	public InteractorRole toInteractorRole() {
		return new InteractorRole(_address, _user, _allow, _deny);
	}
}
