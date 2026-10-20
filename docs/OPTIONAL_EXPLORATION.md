# Optional Overworld exploration

Overworld structure and biome visits are rewarded side objectives. A random
world-generation result must not decide whether the next quest is available.
The campaign retains its material milestones, native dimension progression and
actual boss-kill requirements.

## What changed

| Chapter | Previous blocker | Current route |
| --- | --- | --- |
| 23 · Ruins, Settlements and Surprises | Underground Village was required before any vanilla villager trade. | Expedition kit → optional trade with any valid vanilla villager. |
| 23 | A bridge and Underground Village were required for the automatic readiness summary, Chance Cube branch and final compass task. The compass task also required a trade and opening a cube. | Expedition kit + Industrial Chassis → automatic equipment/workshop summary → compass checklist. Visits, trading and Chance Cubes sit in the separate optional panel. |
| 01 · The Adventure Begins | The final introductory summary required supplying a Millénaire settlement. | The summary requires production, DivineRPG hunting and first mana. Millénaire trade → supply remains a rewarded optional branch. |
| 22 · Millénaire | The first-contact visit blocked the friendship quest. | First contact is a leaf. Friendship checks native reputation after Base Foundation and Industrial Chassis. |
| 32 · Cannibal Villages | Mermaid Sea blocked shark/squid hunts; a large village blocked Shaman; Rutia Temple blocked Rutia. These edges also blocked trophy and equipment descendants. | Hunts open from the expedition kit and their existing material stage. Real kills, Shaman → Ozgum and kill → trophy/equipment links remain. Native spawning and temple mechanics are unchanged. |

The audit covered **all 37 chapters and 1,626 quests**. It identified **67
Overworld visit objectives**: 37 in chapter 23, 20 Terralith biome objectives,
five Cannibal Villages visits, four Millénaire discoveries and one Modern
Foundry Sky Slime Island visit. Six of those visits originally had dependents,
blocking 20 other quests directly or transitively. All 67 are now optional
leaves: no quest anywhere in the book depends on them.

Nine quests received dependency changes and ten received optional markers;
18 authored patches across four chapters also update explanations. The original
quest, task and reward IDs, objectives, quantities and personal claim scope are
preserved. Previously completed quests stay completed; unfinished quests use the
updated prerequisites. No new world scanning or runtime polling is added.

## Why the dependency edges changed

In the pinned FTB Quests 2101.1.36, `Quest.areDependenciesComplete` evaluates
the actual dependency objects through `checkDependencies`; it does not ignore
an explicit prerequisite because that prerequisite has `optional: true`.
Changing its visual role alone therefore cannot fix a blocked route.

Both existing automatic summaries use
[`QmQuestAutomation.reconcileObjective`](../src/compat/quest-automation/src/qm/questauto/QmQuestAutomation.java).
They read the current quest dependencies rather than a separate hard-coded
bridge/village list. Updating the quest graph updates those checks without
replacing tasks or changing the observer.

Reviewed native resources are recorded in
[`metadata/quest_branches/exploration.json`](../metadata/quest_branches/exploration.json).
The FTB Quests, Dungeons and Taverns, Millénaire, Modern Foundry and Structory
Towers JARs were checked against their pinned SHA-256 values in
[`mods.lock.json`](../metadata/mods.lock.json). Native Nova `find_*` advancements
use location conditions; Millénaire's first contact, lone-building, three-culture
and all-culture discoveries are distinct from its reputation objective.
Structory's **Warped Greatsword is an Overworld structure**, despite its name.

## Verification and maintenance

[`test_optional_exploration.py`](../tests/test_optional_exploration.py) checks
that every audited visit has no dependents and still has its reward. It then
simulates all visit objectives remaining unavailable: every other quest remains
reachable. A second scenario also excludes trading and opening Chance Cubes and
confirms that all required quests can still proceed. Additional checks retain
the Twilight boss sequence, lunar preparation and material milestones, and
verify that regeneration repairs reintroduced old visit edges while preserving
the existing objectives and rewards. Both automatic-summary task labels are
authored in `metadata/campaign_guidance.json`; the regression also injects the
old Ukrainian labels and checks that regeneration replaces the bridge/village
requirement and four-route count with the current preparation and three-route
conditions.

The authored source runs through the normal `campaign_refresh.py` and
`localize_english.py` pipeline. Chapter 23's preparation and optional discovery
panels reuse existing persistent image IDs. The coordinate preview was rendered
and inspected; this is layout verification, not an in-game playthrough.

For a release playtest, use an existing save with the bridge and Underground
Village unfinished, complete the kit and Industrial Chassis, and check that the
summary and compass task unlock. Verify a hunt without first completing the
Mermaid Sea/large-village visit, and confirm that an optional visit still gives
its normal personal reward once.

## Українською

Мости, села, таверни, окремі споруди й біоми звичайного світу тепер є
додатковими цілями з нагородою, від яких не залежить жоден наступний квест.
У главі 23 основа — дорожній набір і промислове шасі. Торгівля, відвідування
локацій та Chance Cubes винесені в окрему секцію. Поставка Millénaire більше
не блокує підсумок вступу. У Cannibal Villages перевірка відвідування конкретної
локації більше не стоїть перед полюванням; справжні перемоги й трофейні
ланцюги залишаються потрібними.
