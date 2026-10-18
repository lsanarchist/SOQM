# Source archive release audit

Release integration: the two identified historical adapters are excluded from
SOQM. Their independent replacements pass the local policy regression and the
fresh dedicated-server startup/reload recorded in the
[published alpha evidence](https://github.com/lsanarchist/SOQM/blob/6c4c7c43543a43ca772c5cd2af3e6f2ba10de833/reports/smoke-server.json).
Rendered equipment slots and player loot outcomes still need graphical testing.

Inspected 2026-10-18 without executing archive scripts. Input:
`Questing_Mayhem_1.21.1_Expert_0.4.4_Sources.zip`, SHA256
`5da305ff631ab872209ea9672aca4c2128218a500ec57738de686de20424b354`.

## Archive lineage

| Layer | Embedded member | Entries | Uncompressed bytes |
|---|---|---:|---:|
| 0.4.4 sources | input | 309 | 74,750,009 |
| 0.4.3 sources | `authoring/baseline/Questing_Mayhem_1.21.1_Expert_0.4.3_Sources.zip` | 271 | 193,098,403 |
| 0.4.2 pack | previous layer's `authoring/baseline/Questing_Mayhem_1.21.1_Expert_0.4.2.zip` | 3,448 | 89,582,814 |
| 0.4.1 pack | previous layer's `authoring/baseline/Questing_Mayhem_1.21.1_Expert_0.4.1.zip` | 605 | 35,101,458 |
| quest authoring | previous layer's `build_notes/quests/Questing_Mayhem_1600_Quest_Authoring.zip` | 220 | 30,217,065 |

All five ZIPs passed CRC inspection and had no duplicate member names, absolute
paths, parent-directory traversal, backslash paths, or symlink entries.

## Concrete release findings

1. The 0.4.1 layer's `build_notes/retained_v030/CRAFTORIA_ATTRIBUTION.txt`
   explicitly records that two adapter scripts were derived from a user-supplied
   Craftoria portable ZIP and that redistribution permission was not established.
   The two payload paths are
   `overrides/kubejs/server_scripts/craftoria_accessory_slots.js` and
   `overrides/kubejs/server_scripts/craftoria_accessory_food.js`. They remain in
   the 0.4.2 payload and current override hash manifests. A payload text scan
   found no other Craftoria-referencing override files. This is a targeted trace
   scan, not proof that no unmarked derivative content exists.
2. Craftoria's official [license page](https://www.curseforge.com/minecraft/modpacks/craftoria/license)
   currently declares All Rights Reserved unless otherwise explicitly stated.
   Do not republish those two historical scripts or nested archives containing
   them. The accompanying independent API-based implementation is ready for
   integration and local policy tests; new runtime validation is still required.
3. The current `BUILD_LOCK.json` references 111 third-party mod files, all with
   CurseForge project and file IDs. Of those, 105 lack a top-level license field;
   three Sophisticated mods declare All Rights Reserved, Abandoned Tunnels has a
   copyright-only declaration, Bloxy's Structures declares MIT, and Zume declares
   OSL-3.0. Missing metadata is not evidence that using the mod is prohibited.
   Use a CurseForge manifest to resolve official files, preserving project IDs
   and file IDs, rather than mirroring third-party JARs in GitHub.
4. No third-party mod JARs were found in the five recursively inspected ZIPs.
   There are 8 unique small pack compatibility JARs, sometimes duplicated across
   layers. Every one contains only `qm/...` class paths and a complete MIT license
   (1,088 to 1,104 bytes). This is a packaging observation, not independent proof
   of authorship. Preserve each existing license when flattening their sources.
5. There is no root license covering quests, scripts, textures and documentation
   as a whole. Several test-only resources contain only the 12-byte text
   `MIT License`, which is not a full license. Add a clearly scoped root license
   for the user's own materials, with third-party exclusions, and preserve the
   compatibility modules' notices. Do not label third-party mods as MIT.
6. Current authoring docs explicitly state that graphics, texture atlas rendering,
   physical R/U/F6 keys, a full campaign playthrough and long-term economy were
   not tested. Keep those limits in current release notes. Historical server
   audit files do not validate the new adapters or a newly assembled public pack.

## Recommended public package

Publish flat reviewed overrides, the 111-entry CurseForge manifest, source code
for the eight own compatibility modules, their full licenses, a scoped project
license, current mod credits, reproducible local packaging/check scripts and new
validation reports. Keep source ZIPs, legacy baselines, server worlds, old native
logs, compiler/runtime downloads and extracted mod recipe inventories outside
public Git. They are build inputs and historical evidence, not player content.

The [official CurseForge export rules](https://support.curseforge.com/support/solutions/articles/9000198500-exporting-a-modpack-for-curseforge-project-submission)
describe manifest-based distribution for CurseForge mods and special conditions
for override mods. A GitHub source release and a CurseForge-hosted project are
different publication steps; private proof-of-concept imports do not establish
CurseForge moderation acceptance.

The new name should describe an unofficial homage to the original
[Questing Mayhem](https://www.curseforge.com/minecraft/modpacks/questing-mayhem)
by onemantic, without implying authorship, endorsement or an official port. No
copying from that 1.7.10 modpack was established by this audit.
