package qm.villageguard;

/** Mutation notifications attached to the native object, with explicit ownership. */
public interface GeometryChangeSource {
    void qm$addGeometryListener(Runnable listener);
    void qm$removeGeometryListener(Runnable listener);
    int qm$geometryListenerCount();
}
