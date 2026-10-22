# SOQM — pinned mod list

Minecraft 1.21.1 / NeoForge 21.1.262. Third-party JARs are downloaded by the launcher; they are not embedded in this repository.

The table lists **129 pinned third-party dependencies**. SOQM 0.4.15 additionally
ships **13 authored compatibility modules**, listed with their source and exact
binary pins in [src/compat](src/compat/README.md) and
[authored-mods.json](metadata/authored-mods.json). The new
`qm-progressive-rewards-0.4.10` module records production evidence, preserves
personal claims, and delivers guaranteed recipe refunds. The client-only
`qm-quest-ui-0.4.5` module coordinates the minimap, quest tracker and equipment HUDs. The separately versioned
`qm-runtime-optimizations-0.4.7` module reduces repeated tunnel-height queries
and background encounter/dungeon work; `qm-quest-automation-0.4.4` skips
already completed evidence. See [the optimization notes](docs/PERFORMANCE_OPTIMIZATION.md).

`qm-foundry-compat-0.4.15` fixes positioned ingredient consumption in the pinned
Modern Foundry 4.1.6 Crafting Station. Install it on both client and server;
see [the crafting fix](docs/CRAFTING_STATION_FIX.md).

Inventory Profiles Next uses libIPN and Kotlin for Forge. All three are marked
`client` here because only the client inventory tools need them in SOQM; the
dedicated-server harness excludes this dependency chain. Kotlin for Forge can
also support server mods in other packs.

LambDynamicLights is also marked `client` and excluded from the dedicated-server
harness. Its official JAR includes the required NeoForge libraries. Held-item
lighting is enabled by default; see [dynamic lighting](docs/INSTALL.en.md#dynamic-lighting).

| Mod | Version | Side | Exact file |
| --- | --- | --- | --- |
| [Fabric/Forge/Neo] 1.21.1-4.3 | Skeleton Horse Spawn | 4.3 | both | [project 346219 / file 9223613](https://www.curseforge.com/minecraft/mc-mods/skeleton-horse-spawn/files/9223613) |
| [Fabric/Forge/Neo] 1.21.1-5.5 | Giant Spawn | 5.5 | both | [project 347921 / file 9224661](https://www.curseforge.com/minecraft/mc-mods/giant-spawn/files/9224661) |
| Abandoned Tunnels | 1.0.0 | both | [project 1733257 / file 9109672](https://www.curseforge.com/minecraft/mc-mods/abandoned-tunnels/files/9109672) |
| Abridged | 2.0.2 | both (baseline) | [project 948999 / file 8327782](https://www.curseforge.com/minecraft/mc-mods/abridged/files/8327782) |
| Ad Astra | 1.16.26 | both (baseline) | [project 635042 / file 8874894](https://www.curseforge.com/minecraft/mc-mods/ad-astra/files/8874894) |
| Ad Astra: More Structures | 1.0.2 | both | [project 1311898 / file 8762665](https://www.curseforge.com/minecraft/mc-mods/ad-astra-more-structures/files/8762665) |
| AE_Better_Villagers | 1.15.1.b | both (baseline) | [project 1148167 / file 5956265](https://www.curseforge.com/minecraft/mc-mods/ae2-better-villagers/files/5956265) |
| Apotheosis | 8.9.0 | both (baseline) | [project 313970 / file 8993983](https://www.curseforge.com/minecraft/mc-mods/apotheosis/files/8993983) |
| Apothic Attributes | 2.11.0 | both (baseline) | [project 898963 / file 9006294](https://www.curseforge.com/minecraft/mc-mods/apothic-attributes/files/9006294) |
| Apothic Enchanting | 1.6.4 | both (baseline) | [project 1063926 / file 9095862](https://www.curseforge.com/minecraft/mc-mods/apothic-enchanting/files/9095862) |
| Apothic Spawners | 1.4.0 | both (baseline) | [project 986583 / file 8469405](https://www.curseforge.com/minecraft/mc-mods/apothic-spawners/files/8469405) |
| Applied Energistics 2 | 19.2.18 | both (baseline) | [project 223794 / file 8992605](https://www.curseforge.com/minecraft/mc-mods/applied-energistics-2/files/8992605) |
| AppliedE | 1.0.8-beta | both (baseline) | [project 1009940 / file 6500056](https://www.curseforge.com/minecraft/mc-mods/appliede/files/6500056) |
| Architectury | 13.0.8 | both (baseline) | [project 419699 / file 5786327](https://www.curseforge.com/minecraft/mc-mods/architectury-api/files/5786327) |
| Artifacts | 13.2.5 | both (baseline) | [project 312353 / file 8791899](https://www.curseforge.com/minecraft/mc-mods/artifacts/files/8791899) |
| Balm | 21.0.66 | both | [project 531761 / file 8969738](https://www.curseforge.com/minecraft/mc-mods/balm/files/8969738) |
| Big Reactors / Extreme Reactors | 2.4.28 | both (baseline) | [project 250277 / file 7344744](https://www.curseforge.com/minecraft/mc-mods/extreme-reactors/files/7344744) |
| Bloxy's Structures | 1.17.1 | both | [project 503374 / file 6182925](https://www.curseforge.com/minecraft/mc-mods/bloxys-structures/files/6182925) |
| Botania Revived | 1.0.2 | both (baseline) | [project 1725641 / file 9144042](https://www.curseforge.com/minecraft/mc-mods/botania-revived/files/9144042) |
| BuildCraft Community Edition | 8.0.23+1.21.1+neoforge | both (baseline) | [project 1592400 / file 9035819](https://www.curseforge.com/minecraft/mc-mods/buildcraft-community-edition/files/9035819) |
| Cannibal Villages | 2.4.9 | both | [project 1578651 / file 9089795](https://www.curseforge.com/minecraft/mc-mods/cannibalvillages/files/9089795) |
| Chance Cubes | 1.21.1-5.0.2.517 | both (baseline) | [project 233113 / file 6558787](https://www.curseforge.com/minecraft/mc-mods/chance-cubes/files/6558787) |
| Classic Pipes | 1.0.7 | both (baseline) | [project 1351745 / file 7126867](https://www.curseforge.com/minecraft/mc-mods/classic-pipes/files/7126867) |
| Cloth Config v15 API | 15.0.140 | both | [project 348521 / file 5729127](https://www.curseforge.com/minecraft/mc-mods/cloth-config/files/5729127) |
| CodeChicken Lib | 4.6.1.529 | both (baseline) | [project 242818 / file 8491811](https://www.curseforge.com/minecraft/mc-mods/codechicken-lib-1-8/files/8491811) |
| collective | 8.58 | both | [project 342584 / file 9233764](https://www.curseforge.com/minecraft/mc-mods/collective/files/9233764) |
| Common Storage Lib | 0.0.10 | both (baseline) | [project 1028211 / file 8421542](https://www.curseforge.com/minecraft/mc-mods/common-storage-lib/files/8421542) |
| Controlling | 19.0.5 | client | [project 250398 / file 6368976](https://www.curseforge.com/minecraft/mc-mods/controlling/files/6368976) |
| Curios API | 9.5.1+1.21.1 | both (baseline) | [project 309927 / file 6529130](https://www.curseforge.com/minecraft/mc-mods/curios/files/6529130) |
| Cyclops Core | 1.30.0 | both (baseline) | [project 232758 / file 8859263](https://www.curseforge.com/minecraft/mc-mods/cyclops-core/files/8859263) |
| DivineRPG | 1.10.10 | both (baseline) | [project 363543 / file 8871764](https://www.curseforge.com/minecraft/mc-mods/official-divinerpg/files/8871764) |
| Dungeons and Taverns | 1-v4.4.4 | both (baseline) | [project 853794 / file 5768823](https://www.curseforge.com/minecraft/mc-mods/dungeon-and-taverns/files/5768823) |
| Ecologics | 2.3.7 | both (baseline) | [project 570463 / file 8415537](https://www.curseforge.com/minecraft/mc-mods/ecologics/files/8415537) |
| Elite Mobs 6.2.3 (NeoForge 1.21.1) | 6.2.3 | both | [project 1583529 / file 9191508](https://www.curseforge.com/minecraft/mc-mods/elite-mobs/files/9191508) |
| Ember's Underground Rooms | 2.3.2-neoforge | both | [project 1197753 / file 8924057](https://www.curseforge.com/minecraft/mc-mods/embers-underground-rooms/files/8924057) |
| Ender IO | 8.2.12-beta | both (baseline) | [project 64578 / file 8832691](https://www.curseforge.com/minecraft/mc-mods/ender-io/files/8832691) |
| Ender Storage | 2.13.0.191 | both (baseline) | [project 245174 / file 6159037](https://www.curseforge.com/minecraft/mc-mods/ender-storage-1-8/files/6159037) |
| Entity Culling Fabric/Forge | 1.11.3 | client | [project 448233 / file 9100286](https://www.curseforge.com/minecraft/mc-mods/entityculling/files/9100286) |
| Equipment Compare | 1.3.13 | client | [project 502561 / file 6375501](https://www.curseforge.com/minecraft/mc-mods/equipment-compare/files/6375501) |
| Ethereal Dreamscape | 1.0.0 | client | [project 1537148 / file 9237441](https://www.curseforge.com/minecraft/mc-mods/ethereal-dreamscape/files/9237441) |
| EvilCraft | 1.2.99 | both (baseline) | [project 74610 / file 9203431](https://www.curseforge.com/minecraft/mc-mods/evilcraft/files/9203431) |
| Ex Deorum | 3.12 | both | [project 901420 / file 8384464](https://www.curseforge.com/minecraft/mc-mods/ex-deorum/files/8384464) |
| Ex Machinis: Divitiae Deorum | 4.0.3 | both | [project 980111 / file 8349644](https://www.curseforge.com/minecraft/mc-mods/ex-machinis-divitiae-deorum/files/8349644) |
| Extra Utilities Reborn | 1.0.1 | both (baseline) | [project 656254 / file 8825711](https://www.curseforge.com/minecraft/mc-mods/extra-utilities-reborn/files/8825711) |
| FerriteCore ((Neo)Forge) | 7.0.3 | both | [project 429235 / file 7524151](https://www.curseforge.com/minecraft/mc-mods/ferritecore/files/7524151) |
| FTB Library | 2101.1.37 | both (baseline) | [project 404465 / file 9008089](https://www.curseforge.com/minecraft/mc-mods/ftb-library-forge/files/9008089) |
| FTB Quests | 2101.1.36 | both (baseline) | [project 289412 / file 8885017](https://www.curseforge.com/minecraft/mc-mods/ftb-quests-forge/files/8885017) |
| FTB Teams | 2101.1.9 | both (baseline) | [project 404468 / file 7369021](https://www.curseforge.com/minecraft/mc-mods/ftb-teams-forge/files/7369021) |
| FTB Ultimine | 2101.1.15 | both (baseline) | [project 386134 / file 8231400](https://www.curseforge.com/minecraft/mc-mods/ftb-ultimine-forge/files/8231400) |
| FTB XMod Compat | 21.1.12 | both (baseline) | [project 889915 / file 8909889](https://www.curseforge.com/minecraft/mc-mods/ftb-xmod-compat/files/8909889) |
| Giant Plane Wreck | 1.0.0 | both | [project 1336925 / file 6939623](https://www.curseforge.com/minecraft/mc-mods/giant-plane-wreck/files/6939623) |
| GuideME | 21.1.19 | both (baseline) | [project 1173950 / file 8897145](https://www.curseforge.com/minecraft/mc-mods/guideme/files/8897145) |
| Hilt | 1.14 | both (baseline) | [project 1653251 / file 8731944](https://www.curseforge.com/minecraft/mc-mods/hilt/files/8731944) |
| Iceberg | 1.3.2 | client | [project 520110 / file 6423863](https://www.curseforge.com/minecraft/mc-mods/iceberg/files/6423863) |
| ImmediatelyFast | 1.6.15+1.21.1 | client | [project 686911 / file 9165518](https://www.curseforge.com/minecraft/mc-mods/immediatelyfast/files/9165518) |
| Immersive Engineering | 12.4.2-194 | both (baseline) | [project 231951 / file 6733669](https://www.curseforge.com/minecraft/mc-mods/immersive-engineering/files/6733669) |
| Industrial Foregoing | 3.6.39 | both (baseline) | [project 266515 / file 8370717](https://www.curseforge.com/minecraft/mc-mods/industrial-foregoing/files/8370717) |
| Inventory Profiles Next | 2.2.5 | client | [project 495267 / file 7810574](https://www.curseforge.com/minecraft/mc-mods/inventory-profiles-next/files/7810574) |
| Iron Furnaces | 4.3.2 | both | [project 237664 / file 7034259](https://www.curseforge.com/minecraft/mc-mods/iron-furnaces/files/7034259) |
| Jade Addons (Neo/Forge) | 6.1.2+neoforge | both | [project 583345 / file 9056575](https://www.curseforge.com/minecraft/mc-mods/jade-addons/files/9056575) |
| Jade 🔍 | 15.10.6+neoforge | both | [project 324717 / file 8591319](https://www.curseforge.com/minecraft/mc-mods/jade/files/8591319) |
| JEI | 19.64.0.455 | both (baseline) | [project 238222 / file 9205121](https://www.curseforge.com/minecraft/mc-mods/jei/files/9205121) |
| Kobolds | 4.0.1 | both (baseline) | [project 484967 / file 8198851](https://www.curseforge.com/minecraft/mc-mods/kobolds/files/8198851) |
| Kotlin for Forge | 5.12.0 | client | [project 351264 / file 8335665](https://www.curseforge.com/minecraft/mc-mods/kotlin-for-forge/files/8335665) |
| KubeJS | 2101.7.2-build.377 | both (baseline) | [project 238086 / file 8843626](https://www.curseforge.com/minecraft/mc-mods/kubejs/files/8843626) |
| LambDynamicLights | 4.8.11+1.21.1 | client | [project 393442 / file 8780227](https://www.curseforge.com/minecraft/mc-mods/lambdynamiclights/files/8780227) |
| libIPN | 6.6.3 | client | [project 679177 / file 7810552](https://www.curseforge.com/minecraft/mc-mods/libipn/files/7810552) |
| Lithium (Fabric/NeoForge) | 0.15.4+mc1.21.1 | both | [project 360438 / file 8330365](https://www.curseforge.com/minecraft/mc-mods/lithium/files/8330365) |
| Lithostitched | 1.8.1 | both (baseline) | [project 936015 / file 9170709](https://www.curseforge.com/minecraft/mc-mods/lithostitched/files/9170709) |
| LootJS | 1.21.1-3.7.0 | both (baseline) | [project 570630 / file 8009262](https://www.curseforge.com/minecraft/mc-mods/lootjs/files/8009262) |
| Lootr | 1.21.1-1.11.38.127 | both (baseline) | [project 361276 / file 8968561](https://www.curseforge.com/minecraft/mc-mods/lootr/files/8968561) |
| McJtyLib | 9.0.21 | both (baseline) | [project 233105 / file 8256201](https://www.curseforge.com/minecraft/mc-mods/mcjtylib/files/8256201) |
| Mekanism | 10.7.19.85 | both (baseline) | [project 268560 / file 7904058](https://www.curseforge.com/minecraft/mc-mods/mekanism/files/7904058) |
| Mekanism: Ad Astra Ores | 1.1.0 | both | [project 945470 / file 9050989](https://www.curseforge.com/minecraft/mc-mods/mekanism-ad-astra-ores/files/9050989) |
| Mekanism: Additions | 10.7.19 | both (baseline) | [project 345425 / file 7904060](https://www.curseforge.com/minecraft/mc-mods/mekanism-additions/files/7904060) |
| Mekanism: Generators | 10.7.19 | both (baseline) | [project 268566 / file 7904061](https://www.curseforge.com/minecraft/mc-mods/mekanism-generators/files/7904061) |
| Mekanism: Tools | 10.7.19 | both (baseline) | [project 268567 / file 7904062](https://www.curseforge.com/minecraft/mc-mods/mekanism-tools/files/7904062) |
| MezzConfig | 0.6.8 | both (baseline) | [project 1689768 / file 9048666](https://www.curseforge.com/minecraft/mc-mods/mezz-config/files/9048666) |
| Millénaire | 9.0.2 | both (baseline) | [project 270871 / file 8913592](https://www.curseforge.com/minecraft/mc-mods/millenaire/files/8913592) |
| Mob Grinding Utils | 1.1.10+mc1.21.1 | both | [project 254241 / file 6804869](https://www.curseforge.com/minecraft/mc-mods/mob-grinding-utils/files/6804869) |
| Modern Foundry | 4.1.6 | both (baseline) | [project 1651634 / file 8675380](https://www.curseforge.com/minecraft/mc-mods/modern-foundry/files/8675380) |
| ModernFix | 5.27.26+mc1.21.1 | both | [project 790626 / file 9101836](https://www.curseforge.com/minecraft/mc-mods/modernfix/files/9101836) |
| Mouse Tweaks | 2.26.1 | client | [project 60089 / file 5637846](https://www.curseforge.com/minecraft/mc-mods/mouse-tweaks/files/5637846) |
| Moyai Structures | 1.0.1 | both | [project 1083418 / file 5852869](https://www.curseforge.com/minecraft/mc-mods/moyai-structures/files/5852869) |
| Natural Sizes 1.2 - 1.21.1 NeoForge | 1.2 | both | [project 1516625 / file 8596871](https://www.curseforge.com/minecraft/mc-mods/natural-sizes/files/8596871) |
| Nether's Exoticism | 1.2.9 | both | [project 486029 / file 5826015](https://www.curseforge.com/minecraft/mc-mods/nethers-exoticism/files/5826015) |
| Ocean Lily Pad Village | 1.0.1 | both | [project 1394882 / file 7278596](https://www.curseforge.com/minecraft/mc-mods/ocean-lily-pad-village/files/7278596) |
| OctoLib | 0.6.2 | both (baseline) | [project 916747 / file 8040848](https://www.curseforge.com/minecraft/mc-mods/shatterbyte-lib/files/8040848) |
| owo-lib | 0.12.15.6-beta.1+1.21 | both (baseline) | [project 532610 / file 9215303](https://www.curseforge.com/minecraft/mc-mods/owo-lib/files/9215303) |
| Patchouli | 1.21.1-93-NEOFORGE | both (baseline) | [project 306770 / file 7730942](https://www.curseforge.com/minecraft/mc-mods/patchouli/files/7730942) |
| Placebo | 9.9.4 | both (baseline) | [project 283644 / file 9153039](https://www.curseforge.com/minecraft/mc-mods/placebo/files/9153039) |
| ProjectE | PE1.1.0 | both (baseline) | [project 226410 / file 6611984](https://www.curseforge.com/minecraft/mc-mods/projecte/files/6611984) |
| Pufferfish's Skills | 0.19.2 | both | [project 835091 / file 9101377](https://www.curseforge.com/minecraft/mc-mods/puffish-skills/files/9101377) |
| Railcraft Reborn | 1.2.12 | both (baseline) | [project 901491 / file 9175020](https://www.curseforge.com/minecraft/mc-mods/railcraft-reborn/files/9175020) |
| Re-Avaritia | 1.4.2 | both (baseline) | [project 623969 / file 8838599](https://www.curseforge.com/minecraft/mc-mods/re-avaritia/files/8838599) |
| Relics | 0.12.8 | both (baseline) | [project 445274 / file 8158315](https://www.curseforge.com/minecraft/mc-mods/relics-mod/files/8158315) |
| reliquified_artifacts | 1.0.9 | both (baseline) | [project 1100379 / file 9143545](https://www.curseforge.com/minecraft/mc-mods/reliquified-artifacts/files/9143545) |
| Resourceful Config | 3.0.11 | both (baseline) | [project 714059 / file 6467772](https://www.curseforge.com/minecraft/mc-mods/resourceful-config/files/6467772) |
| Resourceful Lib | 3.0.12 | both (baseline) | [project 570073 / file 5973188](https://www.curseforge.com/minecraft/mc-mods/resourceful-lib/files/5973188) |
| RFTools Base | 6.0.11 | both (baseline) | [project 326041 / file 8256204](https://www.curseforge.com/minecraft/mc-mods/rftools-base/files/8256204) |
| RFTools Builder | 7.0.7 | both (baseline) | [project 347706 / file 9044325](https://www.curseforge.com/minecraft/mc-mods/rftools-builder/files/9044325) |
| RFTools Dimensions | 12.0.4 | both (baseline) | [project 240950 / file 8256210](https://www.curseforge.com/minecraft/mc-mods/rftools-dimensions/files/8256210) |
| RFTools Power | 7.0.6 | both (baseline) | [project 290209 / file 8256213](https://www.curseforge.com/minecraft/mc-mods/rftools-power/files/8256213) |
| RFTools Storage | 6.0.5 | both (baseline) | [project 350006 / file 8256218](https://www.curseforge.com/minecraft/mc-mods/rftools-storage/files/8256218) |
| RFTools Utility | 7.0.12 | both (baseline) | [project 342466 / file 8256212](https://www.curseforge.com/minecraft/mc-mods/rftools-utility/files/8256212) |
| Rhino | 2101.2.7-build.85 | both (baseline) | [project 416294 / file 8218748](https://www.curseforge.com/minecraft/mc-mods/rhino/files/8218748) |
| Searchables | 1.0.2 | client | [project 858542 / file 5831692](https://www.curseforge.com/minecraft/mc-mods/searchables/files/5831692) |
| Sodium | 0.8.13+mc1.21.1 | client | [project 394468 / file 8756580](https://www.curseforge.com/minecraft/mc-mods/sodium/files/8756580) |
| Sophisticated Backpacks | 3.26.12.2208 | both | [project 422301 / file 9154769](https://www.curseforge.com/minecraft/mc-mods/sophisticated-backpacks/files/9154769) |
| Sophisticated Core | 1.5.14.2413 | both | [project 618298 / file 9229216](https://www.curseforge.com/minecraft/mc-mods/sophisticated-core/files/9229216) |
| Sophisticated Storage | 1.6.7.2184 | both | [project 619320 / file 9201424](https://www.curseforge.com/minecraft/mc-mods/sophisticated-storage/files/9201424) |
| spark | 1.10.124 | both | [project 361579 / file 6225208](https://www.curseforge.com/minecraft/mc-mods/spark/files/6225208) |
| sqdclib | 2.2.0 | both | [project 1743037 / file 9236644](https://www.curseforge.com/minecraft/mc-mods/sqdclib/files/9236644) |
| Storage Drawers | 13.11.4 | both (baseline) | [project 223852 / file 6995432](https://www.curseforge.com/minecraft/mc-mods/storage-drawers/files/6995432) |
| Structory: Towers | 1.0.16 | both (baseline) | [project 783522 / file 7842394](https://www.curseforge.com/minecraft/mc-mods/structory-towers/files/7842394) |
| Terralith | 2.6.2 | both | [project 513688 / file 8222737](https://www.curseforge.com/minecraft/mc-mods/terralith/files/8222737) |
| The Aether | 1.5.10 | both (baseline) | [project 255308 / file 7043502](https://www.curseforge.com/minecraft/mc-mods/aether/files/7043502) |
| The Twilight Forest | 4.8.3345 | both (baseline) | [project 227639 / file 7797302](https://www.curseforge.com/minecraft/mc-mods/the-twilight-forest/files/7797302) |
| Titanium | 4.0.51 | both (baseline) | [project 287342 / file 9129904](https://www.curseforge.com/minecraft/mc-mods/titanium/files/9129904) |
| Torchmaster | 21.1.13 | both | [project 254268 / file 8949981](https://www.curseforge.com/minecraft/mc-mods/torchmaster/files/8949981) |
| Twilight Treehouses | 1.0.2 | both | [project 1227052 / file 6341110](https://www.curseforge.com/minecraft/mc-mods/twilight-treehouses/files/6341110) |
| Underground Villages Mod | 4.0.3 | both (baseline) | [project 606989 / file 6722723](https://www.curseforge.com/minecraft/mc-mods/underground-villages/files/6722723) |
| Waystones | 21.1.46 | both | [project 245755 / file 8969751](https://www.curseforge.com/minecraft/mc-mods/waystones/files/8969751) |
| Xaero's Minimap | 26.6.0 | client | [project 263420 / file 9067173](https://www.curseforge.com/minecraft/mc-mods/xaeros-minimap/files/9067173) |
| Xaero's World Map | 1.47.0 | client | [project 317780 / file 9067277](https://www.curseforge.com/minecraft/mc-mods/xaeros-world-map/files/9067277) |
| ymondf-0.2.9-9.jar | 0.2.9-9 | both | [project 1744821 / file 9235220](https://www.curseforge.com/minecraft/mc-mods/ymondf/files/9235220) |
| You're in Grave Danger | 2.0.13 | both | [project 544912 / file 6684166](https://www.curseforge.com/minecraft/mc-mods/youre-in-grave-danger/files/6684166) |
| ZeroCore 2 | 2.4.21 | both (baseline) | [project 247921 / file 7344742](https://www.curseforge.com/minecraft/mc-mods/zerocore/files/7344742) |
| Zume | 1.2.2 | client | [project 927564 / file 7983502](https://www.curseforge.com/minecraft/mc-mods/zume/files/7983502) |
