# Preserve D&T's exact schedule phase for short-lived lightning bolts.
# The native dispatcher only visits entities tracked by lifecycle events.
qm_runtime_optimizations dungeons_tick
schedule function nova_structures:tick 5t
