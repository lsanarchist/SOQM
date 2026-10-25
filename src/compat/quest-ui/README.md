# Quest and HUD Help 0.4.8

Pack-owned client adapter for the exact SOQM Minecraft 1.21.1 / NeoForge
21.1.262 dependency set. Existing quest-item names, mod names and JEI recipe/use
keys remain supported. The adapter coordinates Xaero, Ultimine, FTB pinned
quests, Jade, Ad Astra, Mekanism and RFTools HUD geometry. Version 0.4.4 fixes
the first-world-render stack overflow with Xaero's automatic minimap size.
Version 0.4.6 removed Masa from the reward catalogue and explained the server's
one-copy limit for each identical unstackable item per claim. Version 0.4.7
replaces backpack and boat prizes with reviewed consumable rewards. Version 0.4.8
adds previews for the 12 personal Dimensional Dungeons rewards. These resource
updates preserve every class from the reviewed 0.4.5 artifact.

See [HUD_LAYOUT.md](../../../docs/HUD_LAYOUT.md) for player controls, automatic
existing-instance behavior, audited overlays, pinned sources and limitations.

## Visible quest rewards

The quest reward section now shows the exact guaranteed refund stacks beside
the existing claim icon, with item counts. A bundled cube keeps its original
native icon; its refund ingredients receive separate display icons. Clicking a
display icon opens the catalogue. The original reward button still performs the
normal FTB claim, using the same reward ID and personal claim history.

The labelled **View rewards / Переглянути нагороди** button appears above the
quest description. **Ctrl+Shift+V** opens the same view while that quest is open;
the shortcut does not intercept focused text fields or other modal panels. The
scrollable catalogue shows every guaranteed stack and every possible entry in
the quest's random pool, including count, rarity, production level and exact
manufacturing conditions. It explains that a random pack makes 3–5 draws,
reserves one of those draws for an eligible uncommon-or-better prize, and
does not grant every displayed possibility. Repeated draws of an identical
unstackable item produce only one copy per claim; this final delivery cap leaves
the selection probabilities unchanged and does not promise 3–5 distinct items.
The visible conditions describe
eligibility; the server remains responsible for the actual unlock state and
claim. JEI recipe/use keys work on catalogue items.

The current catalogue contains no backpack, boat, raft or Masa prizes. The new
consumable entries show their actual quantities and production requirements
from the shared server policy. Backpack upgrade components remain separate items.

`scripts/campaign_rewards.py` generates the shipped
`assets/qm_quest_ui/reward_catalog.json` from the same chapter rewards, random
pools and refund metadata as the server policy. The Java parser has no claim,
team or inventory API. Catalogue loading is cached and invalidated by resource
reload; a bad display resource leaves native claiming available. All catalogue
hooks are client mixins.

The fixed cube award adjustment is recorded in
`metadata/cube_reward_bonus.json`: 29 existing direct cube rewards increase
from one to two, raising the original 193 guaranteed cubes to 222 (+15.03%).
The number of milestone quests remains 193. The allocation uses stable quest
IDs, excludes bundled recipe refunds, and does not change cube contents or
random rolls. Existing claims keep their IDs, so already claimed rewards are
not issued again. The 15 added equipment quests use ordinary personal packs
and do not alter this fixed baseline. The 12 optional dungeon quests also use
ordinary personal Expedition packs without adding cube milestones.

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

## Reproducible 0.4.8 resource update

The shipped 0.4.8 artifact is a resource-only update of the reviewed 0.4.5 JAR.
Save that JAR outside `overrides/mods` before updating; its required SHA-256 is:

```text
66688e1cca5af8e5218afeaa57c21dc7251225b36b73eff82082444e2472b9c0
```

After regenerating the campaign catalogue, reproduce the update with Python 3.11+:

```sh
python3 src/compat/quest-ui/build.py \
  --resource-only-from /path/to/saved/qm-quest-ui-0.4.5.jar \
  --output /tmp/qm-quest-ui-0.4.8.jar
python3 -m unittest discover -s tests -p test_quest_ui_resource_build.py
```

This mode verifies the baseline JAR hash and a pinned digest of every Java source
path and its contents. It copies the exact original set of 41 class files without
modifying any bytes, replaces resources from this source tree, and verifies the
output class set again. Only the version in `neoforge.mods.toml`, the generated
catalogue and the two language files may change. Mixin configuration, dependencies,
resource paths, manifest and license must remain identical to the baseline.
Class files cannot be supplied through the resource directory.

The builder normalizes entry order, timestamps and modes and reports
`build_mode: resource_only`, baseline/source/class digests, output SHA-256 and
byte size. It rejects native compilation or verification flags in this mode.
The focused tests cover deterministic output, current resource equality, actual
published class identity and rejection of changed sources, mixins, dependencies,
resource paths or an unpinned baseline. A Java source change requires native
compilation instead. The shipped catalogue check rejects backpack, boat, raft
and Masa prizes in both random pools and guaranteed item lists.

The native 0.4.5 build and probe results are historical evidence for the unchanged
classes. The 0.4.8 module does not recompile Java or rerun native Mixin transforms.
See the campaign's [verification report](../../../reports/README.md) for current
catalogue and server checks; no graphical client playtest is claimed.

## Native compilation and native gate

Install NeoForge 21.1.262's client runtime with the official installer and obtain
the hash-verified mod cache used by the pack. Then run:

```sh
python3 src/compat/quest-ui/build.py \
  --runtime /path/to/neoforge-client-instance \
  --javac /path/to/jdk-21/bin/javac \
  --verify \
  --output /tmp/qm-quest-ui-0.4.7-native.jar
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

Generate campaign outputs with `python3 scripts/campaign_refresh.py` before building;
the preview catalogue is one of those generated resources. `--verify` runs four
separate gates before writing the artifact:

1. `HudMixinProbe` hosts the real installed Sponge Mixin service and transforms
   the 18 pinned target classes using the production JSON. This verifies the
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
4. `RewardCatalogueProbe` parses the exact generated resource and verifies all
   1,643 real reward IDs, 14 possible-item pools, 222 guaranteed cube units,
   29 doubled cube claims and 41 guarded recipe refunds. It rejects malformed
   catalogue references and checks that exact production requirements remain
   attached to KubeJS reward entries, without launching a client or claiming an
   item.

The verification host and fixtures stay outside the published JAR. The default
Python suite also compiles/runs the same geometry with Java 17+ and checks
published resource equality, Java 21 bytecode, required client Mixin classes
and absence of duplicate old adapter versions.

This gate is stricter than compilation alone, but it is **not a graphical
gameplay test**. A full client visual pass remains required when any pinned
mod, native GUI scale behavior, texture/font pack or injection target changes.
