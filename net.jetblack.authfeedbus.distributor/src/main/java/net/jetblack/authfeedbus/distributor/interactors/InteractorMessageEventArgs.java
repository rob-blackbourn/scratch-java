package net.jetblack.authfeedbus.distributor.interactors;

import net.jetblack.authfeedbus.messages.Message;

public class InteractorMessageEventArgs extends InteractorEventArgs {

	public InteractorMessageEventArgs(Interactor interactor, Message message) {
		super(interactor);
		Message = message;
	}

	public final Message Message;
	
	@Override
	public String toString() {
		return super.toString() + ", Message={" + Message + "}";
	}
}
