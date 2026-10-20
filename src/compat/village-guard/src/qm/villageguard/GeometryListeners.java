package qm.villageguard;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/** A village/building may be observed by more than one native manager. */
public final class GeometryListeners {
    private Set<Runnable> listeners;

    public void add(Runnable listener) {
        if (listeners == null) {
            listeners = Collections.newSetFromMap(new IdentityHashMap<>());
        }
        listeners.add(listener);
    }

    public void remove(Runnable listener) {
        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.isEmpty()) listeners = null;
        }
    }

    public int size() {
        return listeners == null ? 0 : listeners.size();
    }

    public void fire() {
        if (listeners == null) return;
        // Callbacks may detach subscriptions while processing a native mutation.
        for (Runnable listener : listeners.toArray(Runnable[]::new)) listener.run();
    }
}
