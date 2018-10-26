package net.jetblack.feedbus.distributor.interactors;

public class InteractorErrorEventArgs extends InteractorEventArgs {

	public InteractorErrorEventArgs(Interactor interactor, Exception error) {
		super(interactor);
		Error = error;
	}
	
	public final Exception Error;

}
