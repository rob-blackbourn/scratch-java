package net.jetblack.authfeedbus.distributor.interactors;

public class InteractorErrorEventArgs extends InteractorEventArgs {

	public InteractorErrorEventArgs(Interactor interactor, Exception error) {
		super(interactor);
		Error = error;
	}

	public final Exception Error;

	@Override
	public String toString() {
		return super.toString() + ", Error=" + Error.getMessage();
	}
}
