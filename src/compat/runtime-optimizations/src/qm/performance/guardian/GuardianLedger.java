package qm.performance.guardian;

import java.util.Iterator;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

/**
 * All authority stays in KubeJS's existing persistent-data file. Moving a ticket
 * between pending and active buckets therefore cannot resurrect a consumed
 * legacy ticket through a second independently saved migration file.
 *
 * The old flat UUID compound is wrapped, without copying or enumerating it.
 * Startup likewise moves the previous active bucket to pending. Each indexing
 * batch removes at most its budget of keys through a retained iterator. Spawn
 * writes to active; death and expiry replace pending values with a consumed
 * marker. Only the cursor structurally edits its pending map, so interleaved
 * events cannot invalidate it or restart scans of empty HashMap prefixes.
 */
final class GuardianLedger {
    static final String KEY = "qm_mayhem_guardians";
    private static final int FORMAT = 2;
    private final CompoundTag active;
    private final ListTag pending;
    private CompoundTag cursorBucket;
    private Iterator<String> cursor;

    record Ticket(UUID id, int tier, long expires) {}

    GuardianLedger(CompoundTag persistent) {
        CompoundTag stored = persistent.getCompound(KEY);
        if (stored.getInt("format") == FORMAT && stored.contains("pending", Tag.TAG_LIST)
                && stored.contains("active", Tag.TAG_COMPOUND)) {
            pending = stored.getList("pending", Tag.TAG_COMPOUND);
            CompoundTag previous = stored.getCompound("active");
            if (!previous.isEmpty()) pending.add(previous);
        } else {
            pending = new ListTag();
            if (!stored.isEmpty()) pending.add(stored);
            stored = new CompoundTag();
            stored.putInt("format", FORMAT);
            stored.put("pending", pending);
            persistent.put(KEY, stored);
        }
        active = new CompoundTag();
        stored.put("active", active);
    }

    Ticket find(UUID id) {
        String key = id.toString();
        if (active.contains(key)) return read(id, active.get(key));
        for (int i = pending.size() - 1; i >= 0; i--) {
            CompoundTag bucket = pending.getCompound(i);
            if (bucket.contains(key)) return read(id, bucket.get(key));
        }
        return null;
    }

    boolean contains(UUID id) {
        String key = id.toString();
        if (active.contains(key)) return true;
        for (int i = pending.size() - 1; i >= 0; i--) {
            if (pending.getCompound(i).contains(key)) return true;
        }
        return false;
    }

    void issue(UUID id, int tier, long expires, String dimension) {
        if (contains(id)) throw new IllegalStateException("Guardian UUID already has a ticket: " + id);
        CompoundTag record = new CompoundTag();
        record.putInt("tier", tier);
        record.putLong("expires", expires);
        record.putString("dimension", dimension);
        active.put(id.toString(), record);
    }

    void remove(UUID id) {
        String key = id.toString();
        active.remove(key);
        // Consume every occurrence defensively. Replacing existing values does
        // not invalidate the retained HashMap iterator; indexing removes these
        // small markers within the same bounded one-time migration pass.
        for (int i = pending.size() - 1; i >= 0; i--) {
            CompoundTag bucket = pending.getCompound(i);
            if (bucket.contains(key)) bucket.put(key, ByteTag.valueOf((byte) 0));
        }
    }

    int indexBatch(int budget, Consumer<Ticket> indexed) {
        int count = 0;
        while (count < budget && !pending.isEmpty()) {
            if (cursor == null) {
                cursorBucket = pending.getCompound(pending.size() - 1);
                cursor = cursorBucket.getAllKeys().iterator();
            }
            if (!cursor.hasNext()) {
                pending.remove(pending.size() - 1);
                cursor = null;
                cursorBucket = null;
                continue;
            }
            String key = cursor.next();
            Tag raw = cursorBucket.get(key);
            cursor.remove();
            count++;
            UUID id;
            try { id = UUID.fromString(key); }
            catch (IllegalArgumentException malformed) { continue; }
            Ticket ticket = read(id, raw);
            if (ticket == null || active.contains(key)) continue;
            active.put(key, raw);
            indexed.accept(ticket);
        }
        return count;
    }

    private static Ticket read(UUID id, Tag raw) {
        if (!(raw instanceof CompoundTag record) || !record.contains("expires", Tag.TAG_ANY_NUMERIC)
                || !record.contains("tier", Tag.TAG_ANY_NUMERIC)) return null;
        int tier = record.getInt("tier");
        return tier < 0 || tier > 6 ? null : new Ticket(id, tier, record.getLong("expires"));
    }

    boolean indexing() { return !pending.isEmpty(); }
    int activeSize() { return active.size(); }
    int pendingBatches() { return pending.size(); }
}
