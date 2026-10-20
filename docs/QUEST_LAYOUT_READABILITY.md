# Quest map readability

The map now shows short local sequences by default. Longer prerequisite links
remain part of quest progression and appear when the player hovers the quest.
Opening the quest remains the way to inspect requirements from other chapters.
The six material milestones retain their visible spine in a clear gutter beside
the Main Route cards.

## What changed

- The previous renderer enabled every dependency line in every authored
  production chapter. A machine that depended on components several panels above
  therefore drew long diagonal lines through unrelated production sequences.
- `campaign_layout.py` now selects visibility **after** positioning the quests.
  A visible incoming group must be short, remain in its section, proceed in
  reading order, and avoid other icons, every caption (including its own source
  caption), heading bars and other visible lines. Branches may meet inside a
  shared quest icon; they may not cross again farther along their paths.
- A straight line is preferred when it is clear. Otherwise short links use the
  native `dep_control_pts` cubic curves to pass beside captions. Their four
  floating-point coordinates are stored on the child quest, keyed by the
  prerequisite's ID. No prerequisites or synthetic relay quests are added for
  these curves.
- FTB Quests controls visibility per quest, not per individual dependency. When
  one incoming prerequisite would clutter the map, all that quest's incoming
  lines wait for hover. The script does not remove prerequisites to change the
  picture.
- Authored production rows keep their meaning. Related items are aligned in
  nearby lanes, and their vertical spacing is reduced from 3.5 to 3.1 map units.
- Ordinary cards use dependency order instead of putting every route-role quest
  before its prerequisite components. Their existing persistent IDs are reused.
- Salvaging precedes the first Apotheosis Invader milestone. Exploration's
  combined milestone follows its random-event prerequisites. The Ad Astra fan is
  beside early engine components; the oxygen distributor follows lunar Desh.
  Rune-dependent Botania automation follows the elemental runes. Full Cannibal
  diving suits follow Coralina and Abyssal Pearls, while basic oxygen equipment
  remains near the beginning.

## Primary-source comparison

These examples informed the presentation; they are not imported quest content.

| Source | Observed pattern | SOQM application |
| --- | --- | --- |
| [ATM10: Hostile Neural Networks](https://github.com/AllTheMods/ATM-10/blob/main/config/ftbquests/quests/chapters/hostile_neural_networks.snbt) | The chapter defaults to `default_hide_dependency_lines: true`, with explicit `hide_dependency_lines: false` on the compact core sequence. Many optional model quests inherit hidden incoming lines. | Show readable local sequences and reveal the full prerequisite group on hover. |
| [Craftoria: Mekanism](https://github.com/TeamAOF/Craftoria/blob/main/config/ftbquests/quests/chapters/Mekanism.snbt), [logistics](https://github.com/TeamAOF/Craftoria/blob/main/config/ftbquests/quests/chapters/mekanism_logistics.snbt), [generators](https://github.com/TeamAOF/Craftoria/blob/main/config/ftbquests/quests/chapters/mekanism_generators.snbt) | Production, logistics and power have separate chapter surfaces. Individual entries commonly follow one immediate prerequisite. | Keep existing SOQM chapters and progress IDs while using distinct production sections and short local links. |
| [FTB Quests 2101.1.36: QuestPanel](https://github.com/FTBTeam/FTB-Quests/blob/v2101.1.36/common/src/main/java/dev/ftb/mods/ftbquests/client/gui/quests/QuestPanel.java) | The render condition includes `!shouldHideDependencyLines() || isMouseOver()`. Hidden incoming lines are still drawn while hovering their quest. | Use the supported native field; no client script or custom line renderer is needed. |
| [FTB Quests 2101.1.36: Quest](https://github.com/FTBTeam/FTB-Quests/blob/v2101.1.36/common/src/main/java/dev/ftb/mods/ftbquests/quest/Quest.java), [QuestButton](https://github.com/FTBTeam/FTB-Quests/blob/v2101.1.36/common/src/main/java/dev/ftb/mods/ftbquests/client/gui/quests/QuestButton.java) | `dep_control_pts` stores two cubic controls for each prerequisite. The native renderer samples the curve into at least 20 segments. | Route a short sequence around its source caption with native data, and reproduce that path in the preview. |
| [FTB Quests 2101.1.36: ChapterImageButton](https://github.com/FTBTeam/FTB-Quests/blob/v2101.1.36/common/src/main/java/dev/ftb/mods/ftbquests/client/gui/quests/ChapterImageButton.java), [FTB Library: Panel](https://github.com/FTBTeam/FTB-Library/blob/1.21.1/main/common/src/main/java/dev/ftb/mods/ftblibrary/ui/Panel.java) | Every chapter image uses the background draw layer. Image order sorts images among themselves; it never moves a caption above a connection. | Check source captions and section headings as obstacles. Draw all images and text before connections in the SVG, with quest icons last. |
| [FTB Quests dependency settings](https://github.com/FTBTeam/docs/blob/main/mod-docs/mods/suite/Quests/Developer/Quests/Settings.md) | All/one completed and all/one started are separate unlock rules. | Visibility does not modify these rules, the dependency list, or `min_required_dependencies`. |

### Pinned control-point transform

In `QuestButton.positionControlPoints()` the stored controls are transformed
relative to the child's top-left icon position, whereas connection endpoints
use icon centers. SOQM sets `quest_spacing: 0.0`, so adding half the child's icon
size to both axes when writing a control point exactly compensates for that
offset at every zoom. The reader requests four `DoubleTag` values; the generator
therefore writes four JSON/SNBT floats even when a coordinate is integral.
This behavior is verified against the installed FTB Quests `2101.1.36` source.
Recheck this transform and the spacing assertion when updating FTB or the theme.

## Verification

`tests/test_quest_line_readability.py` checks retained tasks, rewards and unlock
requirements, prerequisite reading order, short local links, and absence of
lines crossing unrelated icons, any text box or other links outside a shared icon. Both
the native minimum 20 segments and an 80-segment curve sample are checked.
The tests also verify native control-point types and coordinate transforms,
the Main Route gutter, and actual SVG path coordinates and draw order. They cover the
Mekanism buffer that produced the long lines in the reported screenshot.

`scripts/render_campaign.py` draws the same idle visibility policy in the SVG
coordinate previews and records hidden prerequisite counts and native curve
controls. All captions precede connection paths in the SVG, so the preview cannot
hide a collision by painting a label over it. The previews use
numbered markers instead of item icons and are not Minecraft screenshots.
The line color is neutral and omits the native animated texture. An in-game check
remains necessary for font rendering and the native hover interaction at the
player's actual UI scale.
