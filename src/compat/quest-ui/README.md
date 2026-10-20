# Quest and HUD Help 0.4.4

Pack-owned client adapter for the exact SOQM Minecraft 1.21.1 / NeoForge
21.1.262 dependency set. Existing quest-item names, mod names and JEI recipe/use
keys remain supported. The adapter coordinates Xaero, Ultimine, FTB pinned
quests, Jade, Ad Astra, Mekanism and RFTools HUD geometry. Version 0.4.4 fixes
the first-world-render stack overflow with Xaero's automatic minimap size.

See [HUD_LAYOUT.md](../../../docs/HUD_LAYOUT.md) for player controls, automatic
existing-instance behavior, audited overlays, pinned sources and limitations.

## Design

`HudGeometry` contains deterministic pixel placement independently of Minecraft.
`HudCoordinator` measures native state and reserves visible regions. The Mixin
hooks change only render coordinates/scales, leaving the individual mods'
configuration and quest state intact. The `coordinateHud` client config defaults
to true, can be opted out, and does not require deleting FTB `local/` overrides.

Xaero's automatic map size is calculated from its native user UI scale via
`MinimapAutoSizeMixin`; only the final rendered scale is capped by
`MinimapScaleMixin`. Keeping those two stages separate prevents
`getMinimapScale -> getConfiguredWidth -> getAutoMinimapSize -> getMinimapScale`
recursion and stops a smaller render scale from increasing the source map size.
The native automatic-size formula, fixed sizes and enlarged map remain intact.
With coordination disabled, the redirect delegates to the original scale path.

`HudQuestTracker` displays the exact upstream collected lines in a bounded
viewport. It retains styled codepoints when wrapping, caches wrapping until
content/width changes, uses scissoring, and supports page scrolling without
changing selected/pinned quests. When a full row cannot fit, a compact indicator
searches for space outside the map, lower instruments and vanilla hotbar.

The only reflective read is the private upstream `PinnedQuestsTracker.renderData`
field, resolved once; its private record is accessed through a typed Mixin
interface. Dynamic render rectangles expire after 150 ms. No per-frame file I/O
or config rewriting occurs. All hooks are in the client list of the production
Mixin config. The mod entrypoint registers only a common config definition and
does not load rendering classes on a dedicated server.

## Reproducible build and native gate

Install NeoForge 21.1.262's client runtime with the official installer and obtain
the hash-verified mod cache used by the pack. Then run:

```sh
python3 src/compat/quest-ui/build.py \
  --runtime /path/to/neoforge-client-instance \
  --javac /path/to/jdk-21/bin/javac \
  --verify \
  --output /tmp/qm-quest-ui-0.4.4.jar
```

The builder checks Java 21, every `metadata/mods.lock.json` mod artifact, and the
mapped client at
`libraries/net/neoforged/neoforge/21.1.262/neoforge-21.1.262-client.jar`:

```text
sha256 aff67d0c3bbd015ecd881bb37416b740ada2e1e854433501ec5bb9381dbdd1c2
```

The classpath includes only verified mod artifacts and their embedded jarjar
dependencies, plus the installed native client libraries. Native Minecraft
classes precede other libraries; a mapped server JAR is never used as a client
API substitute. JAR entry order, timestamps and modes are normalized. The
builder prints output SHA-256, byte size, compiler version and client hash.

`--verify` runs three separate gates before writing the artifact:

1. `HudMixinProbe` hosts the real installed Sponge Mixin service and transforms
   the 16 pinned target classes using the production JSON. This verifies the
   exact method/field signatures, required injection sites and successful
   transformations, including the pre-existing quest UI hooks. It does not
   launch Minecraft or render OpenGL frames.
2. `HudGeometryProbe` executes production placement methods at logical GUI
   sizes 320×180, 640×360 and 960×540, including wide content, Aether, status
   effects, 10 000 tracker lines, compact fallback, hotbar and lower reserves.
3. `HudMinimapScaleProbe` executes the transformed native Xaero scale and
   automatic-size methods with headless config/window boundaries. It covers
   automatic and fixed sizes, UI and GUI scale changes, repeated size queries,
   missing sessions, enlarged maps and disabled coordination. It checks stable
   source dimensions and the final rendered width, without creating an OpenGL
   client. The old 0.4.3 adapter fails the automatic-size scenario.

The verification host and fixtures stay outside the published JAR. The default
Python suite also compiles/runs the same geometry with Java 17+ and checks
published resource equality, Java 21 bytecode, required client Mixin classes
and absence of duplicate old adapter versions.

This gate is stricter than compilation alone, but it is **not a graphical
gameplay test**. A full client visual pass remains required when any pinned
mod, native GUI scale behavior, texture/font pack or injection target changes.
