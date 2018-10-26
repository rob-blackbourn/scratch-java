package net.jetblack.feedbus.distributor.interactors;

import net.jetblack.feedbus.messages.Message;

public class InteractorMessageEventArgs extends InteractorEventArgs {

	public InteractorMessageEventArgs(Interactor interactor, Message message) {
		super(interactor);
		Message = message;
	}
	
	public final Message Message;
}
