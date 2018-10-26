package net.jetblack.feedbus.distributor.interactors;

import java.util.HashMap;
import java.util.Map;

public class InteractorRepository {

    private final Map<String, Interactor> _interactors = new HashMap<String, Interactor>();

    public InteractorRepository() {
    }

    public void add(Interactor interactor) {
        _interactors.put(interactor.Id, interactor);
    }

    public Interactor remove(Interactor interactor) {
        return _interactors.remove(interactor.Id);
    }

    public void dispose() throws Exception {
        for (Interactor interactor : _interactors.values()) {
            interactor.dispose();
        }
        _interactors.clear();
    }
}
