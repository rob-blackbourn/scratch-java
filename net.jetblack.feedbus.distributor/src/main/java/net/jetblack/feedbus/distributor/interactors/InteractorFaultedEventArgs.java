package net.jetblack.feedbus.distributor.interactors;

public class InteractorFaultedEventArgs extends InteractorErrorEventArgs {

	public InteractorFaultedEventArgs(Interactor interactor, Exception error) {
		super(interactor, error);
	}
}
