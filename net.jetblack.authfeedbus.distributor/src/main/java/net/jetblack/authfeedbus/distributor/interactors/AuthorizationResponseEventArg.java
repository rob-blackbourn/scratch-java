package net.jetblack.authfeedbus.distributor.interactors;

import net.jetblack.authfeedbus.messages.AuthorizationResponse;
import net.jetblack.util.EventArgs;

public class AuthorizationResponseEventArg extends EventArgs {

	public AuthorizationResponseEventArg(Interactor authorizer, Interactor requester, AuthorizationResponse response) {
		Authorizer = authorizer;
		Requester = requester;
		Response = response;
	}

	public final Interactor Authorizer;
	public final Interactor Requester;
	public final AuthorizationResponse Response;
}
