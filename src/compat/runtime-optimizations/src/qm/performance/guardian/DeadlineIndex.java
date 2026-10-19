package qm.performance.guardian;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

/** One deadline per key; replacements never leave stale priority-queue entries. */
final class DeadlineIndex<K> {
    private final Map<K, Long> deadlines = new HashMap<>();
    private final TreeMap<Long, Set<K>> ordered = new TreeMap<>();

    void put(K key, long expires) {
        Long previous = deadlines.get(key);
        if (previous != null && previous.longValue() == expires) return;
        remove(key);
        deadlines.put(key, expires);
        ordered.computeIfAbsent(expires, ignored -> new HashSet<>()).add(key);
    }

    void remove(K key) {
        Long previous = deadlines.remove(key);
        if (previous == null) return;
        Set<K> bucket = ordered.get(previous);
        bucket.remove(key);
        if (bucket.isEmpty()) ordered.remove(previous);
    }

    int drain(long now, int budget, Consumer<K> expired) {
        int count = 0;
        while (count < budget && !ordered.isEmpty() && ordered.firstKey() <= now) {
            Map.Entry<Long, Set<K>> first = ordered.firstEntry();
            K key = first.getValue().iterator().next();
            // Remove before invoking code that can itself register/remove keys.
            remove(key);
            count++;
            expired.accept(key);
        }
        return count;
    }

    int size() { return deadlines.size(); }
    int buckets() { return ordered.size(); }
    boolean contains(K key) { return deadlines.containsKey(key); }
    void clear() { deadlines.clear(); ordered.clear(); }
}
