# SOQM: особисті прогресивні нагороди / Progressive personal rewards

**Кожен учасник отримує власний пакунок за завершений звичайний квест. Пакунок виконує 3–5 незалежних жеребкувань тематичних призів; дорогий предмет стає доступним лише після фактичного виготовлення цього самого предмета командою.** Кількість жеребкувань і ваги рідкостей зростають із виробничими відкриттями. Отримання ручне, одноразове й особисте.

**Every member can claim a personal pack for a completed ordinary quest. A pack performs 3–5 independent themed prize draws; an expensive item becomes eligible only after the team has actually manufactured that exact item.** Production discoveries increase the number of draws and the weights of higher rarities. Claims are manual, personal and one-time.

**41 квест за дорогі крафти також повертає фіксовану частину реально витрачених компонентів.** Компенсація додається до наявного пакунка або куба після підтвердженого виготовлення потрібного виробу вами чи вашою командою. Повний перелік рецептів, кількостей і SHA256 містить [`metadata/recipe_refunds.json`](../metadata/recipe_refunds.json).

**41 expensive crafting quests also return fixed quantities of components consumed by their actual recipes.** The refund accompanies the existing pack or cube after you or your team manufacture the required output. Exact recipes, quantities and SHA256 evidence are recorded in [`metadata/recipe_refunds.json`](../metadata/recipe_refunds.json).

Каталог і правила задає [`metadata/reward_pools.json`](../metadata/reward_pools.json). Підтвердження предметів і продуктивних рецептів із закріплених версій модів містить [`metadata/reward_item_evidence.json`](../metadata/reward_item_evidence.json). Компілятор створює [`kubejs/config/soqm_rewards.json`](../overrides/kubejs/config/soqm_rewards.json); обробник нагород і спостерігач виробництва читають цей спільний runtime-файл.

The catalogue and rules live in [`metadata/reward_pools.json`](../metadata/reward_pools.json). Exact pinned item and productive recipe evidence is recorded in [`metadata/reward_item_evidence.json`](../metadata/reward_item_evidence.json). The compiler produces [`kubejs/config/soqm_rewards.json`](../overrides/kubejs/config/soqm_rewards.json), the shared runtime policy read by the reward handler and production observer.

## Зміни 0.4.18 / Changes in 0.4.18

За один пакунок видається не більше одного однакового предмета, що не складається у стек: наплічника, човна чи іншого спорядження з нативним максимальним розміром стека 1. Ліміт застосовується після всіх 3–5 жеребкувань, тому повторні випадіння можуть дати менше різних призів. Інший квест може дати ще один такий предмет; наявність його в інвентарі не впливає на нагороду. Партії звичайних ресурсів і кількості фіксованих компенсацій зберігаються. «Маса» (`millenaire:masa`) прибрана з обох пулів, серверної політики та клієнтського каталогу.

Each pack delivers at most one of each identical unstackable item, including backpacks, boats and other equipment with native maximum stack size 1. The cap is applied after all 3–5 draws, so repeated selections can yield fewer distinct prizes. Another quest can award another copy; inventory possession does not suppress a reward. Ordinary resource batches and fixed refund quantities are retained. Masa (`millenaire:masa`) is removed from both pools, the server policy and the client catalogue.

Поточна зміна зберігає всі **1643** наявні зовнішні ID та історію особистих отримань. Уже видані предмети залишаються в гравця. Поділ на 1628 попередніх і 15 доданих ID нижче описує попереднє розширення книги; 0.4.18 не додає нових claim slots.

This change preserves all **1643** existing outer IDs and personal claim history. Previously awarded items remain with the player. The 1628 retained plus 15 added IDs below describe the earlier questbook expansion; 0.4.18 adds no claim slots.

| Частина системи / System part | Кількість / Count |
| --- | ---: |
| Зовнішні ID нагород загалом / Total outer reward IDs | 1643 |
| Зовнішні ID нагород, збережені при цьому оновленні / Outer reward IDs preserved by this update | 1628 |
| Нові особисті нагороди квестів спорядження / New personal equipment quest rewards | 15 |
| Звичайні квести з особистим прогресивним пакунком / Ordinary quests with a personal progressive pack | 1449 |
| Рубежі з особистим фіксованим Mayhem Cube / Milestones with a personal fixed Mayhem Cube | 193 |
| Гарантовані куби загалом на гравця / Total guaranteed cube units per player | 222 |
| Наявні нагороди, збільшені з одного до двох кубів / Existing claims increased from one to two cubes | 29 |
| Особиста вступна книга / Personal introductory book | 1 |
| Квести з гарантованою компенсацією / Quests with guaranteed crafting refunds | 41 |
| Компенсація разом із пакунком / Refund plus the original pack | 32 |
| Компенсація разом із кубом / Refund plus the original cube | 9 |
| Нативні custom reward objects / Native custom reward objects | 1458 |
| Окремі фіксовані cube item reward objects / Standalone fixed cube item reward objects | 184 |
| Тематичні пули / Themes | 14 |
| Позиції повного каталогу / Catalogue entries | 324 |
| Унікальні предмети, з них модові / Distinct items, including modded items | 237 / 209 |
| Модові позиції з повтореннями між темами / Modded entries across themes | 254 |
| Позиції з умовою власного виготовлення / Entries requiring exact manufacture | 240 |
| Унікальні відстежувані виробничі результати / Distinct tracked production outputs | 242 |

У кожного звичайного квесту один пакунок. Вступний квест додатково зберігає особисту книгу. 193 рубежі гілок, босів, мультиблоків і великих виробничих кроків дають фіксовані куби замість пакунка; повний список задає `milestones` у каталозі. У 29 наявних прямих нагородах кількість збільшено з одного до двох: загальний бюджет зріс із 193 до **222 кубів на гравця, на 15,0259%**. Решта 164 рубежів зберігає по одному кубу, зокрема дев’ять наборів із компенсацією. 15 нових квестів спорядження дають звичайні особисті пакунки. Числа походять із каталогу, фіксованої вибірки [`cube_reward_bonus.json`](../metadata/cube_reward_bonus.json) та застосування [`campaign_rewards.apply`](../scripts/campaign_rewards.py) до книги.

Each ordinary quest has one pack. The introductory quest also retains its personal book. The 193 branch, boss, multiblock and substantial production milestones award fixed cubes in place of a pack; the catalogue’s `milestones` map is the complete list. Twenty-nine existing direct rewards increase from one to two cubes, raising the total budget from 193 to **222 cubes per player, a 15.0259% increase**. The remaining 164 milestones retain one cube each, including the nine refund bundles. The 15 new equipment quests award ordinary personal packs. These counts come from the catalogue, the fixed selection in [`cube_reward_bonus.json`](../metadata/cube_reward_bonus.json), and [`campaign_rewards.apply`](../scripts/campaign_rewards.py) applied to the book.

## Гарантовані компенсації крафту / Guaranteed crafting refunds

Фіксовані матеріали не залежать від жеребкування. У 34 квестах повертається 25–50% вибраного повторюваного інгредієнта: одне з двох ядер телепортації Digital Miner, один із двох незеритових злитків печі, дві із шести енергокомірок Dense Energy Cell, один із трьох попередніх QIO-дисків або компонентів ME. У семи інших квестах повертається один неподільний процесор чи вторинна збірка; щонайменше два інші авторські компоненти розвитку залишаються витраченими. Це частка конкретних інгредієнтів, а не оцінка загальної ціни рецепта у відсотках.

Fixed materials do not depend on the random roll. In 34 quests, the refund returns 25–50% of one repeated ingredient: one of the Digital Miner's two Teleportation Cores, one of the furnace's two Netherite Ingots, two of the Dense Energy Cell's six Energy Cells, or one of the three preceding QIO drives or ME components. Seven other quests return one indivisible processor or secondary assembly, while at least two other authored campaign components remain consumed. These fractions describe specific ingredients, not an estimated percentage of the complete recipe's value.

Повернення прив’язане до одного основного квесту для кожного output ID. Дублікати цього виробу в підсумкових або будівельних завданнях не створюють ще одну компенсацію. Компілятор перевіряє всі власні альтернативні рецепти, точні позиції інгредієнтів, мінімальну витрату між варіантами й SHA256 рецепта. Тег матеріалу не перетворюється на здогаданий конкретний предмет. Live RecipeManager також перевіряється native-тестом: активна дешевша альтернатива не може непомітно залишити стару компенсацію.

Each output ID has one primary refund quest. Its appearances in convergence or construction objectives do not create extra refunds. The compiler checks every authored recipe alternative, exact ingredient positions, the minimum consumed across variants, and the recipe SHA256. Material tags are not guessed into specific returned items. A native test also audits the live RecipeManager, so an active cheaper alternative cannot silently retain an outdated refund.

### Справжнє виготовлення та особисте отримання / Manufacture and personal claims

Для компенсації потрібен виробничий факт **готового виробу**. Якщо гравець легально використав отриманий від іншого гравця процесор у рецепті ME Drive, справжнє виготовлення дисковода вже дозволяє повернути витрачений процесор. Отримання цієї нагороди не записує факт виготовлення процесора: у випадковому пулі він залишиться закритим до власного виготовлення. Виробництво поточної партії дозволяє отримати особисту компенсацію кожному її учаснику.

The refund requires manufacture evidence for the **finished output**. If a player legitimately uses an obtained Engineering Processor in a ME Drive recipe, actually manufacturing the drive is sufficient to return a processor that recipe consumed. The payout does not record processor manufacture; the processor remains locked in random pools until its own production is observed. Current-party manufacture lets each member claim their personal compensation.

Власником отримання залишається FTB Quests і той самий `(player UUID, outer reward ID)`. Для цих 41 слотів native gate перевіряє виробництво **до** запису отриманої нагороди. Завчасне натискання, невідомий mapping, невірна батьківська quest ID або пошкоджена політика залишають нагороду неотриманою й пояснюють відмову. Решта нагород зберігає звичайну поведінку. Після успішного запису scoped callback видає фіксовані компоненти та, за потреби, початковий куб; KubeJS робить лише початкові 3–5 випадкових жеребкувань для пакунка. Повторний custom event не має нового дозволу на видачу. Scoped context прибирається у `finally`, без додаткового постійного claim ledger.

FTB Quests and the same `(player UUID, outer reward ID)` remain responsible for claims. A native gate checks manufacture **before** FTB marks these 41 slots claimed. An early click, unknown mapping, wrong parent quest ID or invalid policy leaves the reward unclaimed and explains the refusal. Other rewards retain their normal behavior. After the native claim succeeds, a scoped callback delivers the fixed components and any original cube; KubeJS performs only the pack's original 3–5 random draws. A repeated custom event has no fresh delivery authorization. The context is cleared in `finally`, without another persistent claim ledger.

Апгрейд установленої обсидіанової печі до незеритової теж підтримується. Observer закріпленого Iron Furnaces 4.3.2 перевіряє нативний перехід блока, фактичну витрату upgrade-предмета й кінцевий block entity. Прямий рецепт печі та рецепт поліпшення витрачають по два незеритові злитки; компенсація в обох випадках — один. Сам крафт поліпшення без його застосування ще не означає виготовлення печі. Інші підтримувані маршрути наведено нижче.

Upgrading a placed Obsidian Furnace to Netherite is also supported. The pinned Iron Furnaces 4.3.2 observer verifies the native block transition, actual upgrade-item consumption and resulting block entity. Both the direct furnace recipe and the upgrade recipe consume two Netherite Ingots; either route earns one ingot back. Crafting the upgrade item without applying it does not manufacture the furnace. Other supported routes appear below.

### Оновлення існуючих світів / Existing worlds

Усі 1628 наявних зовнішніх ID і персональні claim keys збережені; 15 нових квестів спорядження додають власні нагороди з новими ID. **Уже отримана нагорода не видається повторно й не отримує автоматичної доплати.** Два куби у 29 вибраних слотах доступні лише за ще не отриманими нагородами. Дев’ять кубів, об’єднаних із компенсацією в історичному оновленні 0.4.10, і далі видаються через custom reward з тим самим ID, кількістю й tier.

All 1628 existing outer IDs and personal claim keys are retained; the 15 new equipment quests add their own rewards with new IDs. **Already claimed rewards remain claimed and receive no automatic top-up.** The doubled cube quantities in 29 selected slots apply only to unclaimed rewards. The nine cubes bundled with refunds in the historical 0.4.10 update continue to use custom rewards with the same ID, count and tier.

Історична умова переходу з версій до запровадження компенсацій у 0.4.10: для завершеного, але ще не отриманого квесту потрібен виробничий факт. До того оновлення 40 із 41 потрібного output ID не відстежувалися; минулий крафт неможливо відновити з інвентарю чи прогресу. Для таких старих світів потрібне нове справжнє виготовлення або підтримуваний апгрейд. Уже записані виробничі факти, зокрема `mekanism:ultimate_energy_cube`, залишаються придатними; поточне оновлення їх не скидає. Зміна команди не скидає отриманих персональних нагород і не копіює чужі виробничі факти у власний запис.

Historical migration condition for versions predating the refunds introduced in 0.4.10: a completed but unclaimed refund quest needs manufacture evidence. Before that update, forty of the 41 required outputs were not observed; inventory or quest progress cannot reconstruct their past crafting. Those older worlds need a new real manufacture or supported upgrade. Already recorded production facts, including `mekanism:ultimate_energy_cube`, remain valid; the current update does not reset them. Team changes do not reset personal claims or copy other players' production facts into the player's own record.

### Первинні приклади / Primary-source precedents

Досліджено квестбуки Craftoria на commit [`7d940d2`](https://github.com/TeamAOF/Craftoria/tree/7d940d2111eb39e8b24c463c9e9e3b254ed151a0/config/ftbquests/quests/chapters) та офіційний [Questing Mayhem 1.0.3 для Minecraft 1.7.10](https://www.curseforge.com/minecraft/modpacks/questing-mayhem/files/3374264). Craftoria дає чотири Engineering Processors за Spatial IO Port, вісім Quartz Glass за ME Drive, вісім Clean Stainless Steel Machine Casings за Distillation Tower; це конкретні матеріальні нагороди в [AE2](https://github.com/TeamAOF/Craftoria/blob/7d940d2111eb39e8b24c463c9e9e3b254ed151a0/config/ftbquests/quests/chapters/applied_energistics.snbt) та [MI](https://github.com/TeamAOF/Craftoria/blob/7d940d2111eb39e8b24c463c9e9e3b254ed151a0/config/ftbquests/quests/chapters/mi_electric_age.snbt).

У Questing Mayhem квест 554 повертає один із 40 витрачених Deep Storage Units за Infinity Chest; 540 повертає один із чотирьох Molecular Assemblers у ланцюжку EMC Crafter; квести крафту Signalum/Terrasteel Seeds повертають один із чотирьох відповідних блоків поряд із reward bag. Fluix Seeds повертає один із двох Fluix Blocks. Джерела — `overrides/config/betterquesting/DefaultQuests.json` і точні `.zs` рецепти офіційного ZIP, SHA256 `22c584d549132744d6f48292a78c315cb256c05a05521de75ba3e4a7c992dbcf`. У metadata записані file ID, URL, рядки рецептів та quest IDs. Атрибуцію «by spirit» у цьому джерелі не підтверджено: manifest указує DaZombieBoss, поточна сторінка CurseForge — onemantic.

These primary questbooks establish fixed material rewards alongside other prizes. Questing Mayhem's checked examples return specific consumed components in varying proportions, with separate personal party rewards. SOQM applies that principle to its own pinned recipes and keeps its stronger actual-manufacture evidence requirement. The research does not imply a universal percentage refund of complete recipe value.

### Повний перелік компенсацій / Complete refund list

`Витрата → повернення` описує лише зазначений прямий інгредієнт. `*` означає повернення одного неподільного компонента з дорожчого рецепта, не 100% вартості всього крафту. Кожен рядок зберігає одну особисту нагороду за своїм наявним ID. / `Consumed → returned` refers only to the named direct ingredient. `*` marks one indivisible component returned from a larger expensive recipe, never the full craft cost. Each row retains one personal claim with its existing ID.

| Quest ID | Виріб / Output | Повернений інгредієнт / Returned ingredient | Витрата → повернення / Consumed → returned | Наявна нагорода / Existing prize |
| --- | --- | --- | ---: | --- |
| `51301125F300B5D5` | NASA Workbench — `ad_astra:nasa_workbench` | `kubejs:control_assembly` | 1 → 1 * | Пакунок / Pack |
| `513011BBCA06FEA6` | 16k ME Storage Component — `ae2:cell_component_16k` | `ae2:cell_component_4k` | 3 → 1 | Пакунок / Pack |
| `5130110B5D09E918` | 256k ME Storage Component — `ae2:cell_component_256k` | `ae2:cell_component_64k` | 3 → 1 | Пакунок / Pack |
| `5130114905904323` | 4k ME Storage Component — `ae2:cell_component_4k` | `ae2:cell_component_1k` | 3 → 1 | Пакунок / Pack |
| `513011DA15992023` | 64k ME Storage Component — `ae2:cell_component_64k` | `ae2:cell_component_16k` | 3 → 1 | Пакунок / Pack |
| `5130119BD717861F` | ME Controller — `ae2:controller` | `ae2:engineering_processor` | 1 → 1 * | Пакунок / Pack |
| `513011B5F7F592D8` | Crafting Unit — `ae2:crafting_unit` | `ae2:calculation_processor` | 1 → 1 * | Пакунок / Pack |
| `5130117C8CDC4800` | Dense Energy Cell — `ae2:dense_energy_cell` | `ae2:energy_cell` | 6 → 2 | Пакунок / Pack |
| `5130117C40A8D2E1` | ME Drive — `ae2:drive` | `ae2:engineering_processor` | 2 → 1 | Пакунок / Pack |
| `5130112D00658B23` | Molecular Assembler — `ae2:molecular_assembler` | `ae2:annihilation_core` | 1 → 1 * | Пакунок / Pack |
| `5130112D86FBD3AF` | ME Quantum Link Chamber — `ae2:quantum_link` | `ae2:fluix_pearl` | 4 → 1 | Пакунок / Pack |
| `513011EAB08A97F7` | Runic Altar — `botania:runic_altar` | `kubejs:arcane_conduit` | 1 → 1 * | Mayhem Cube T2 |
| `5130111A90908B6C` | BuildCraft Laser — `buildcraftsilicon:laser` | `minecraft:diamond` | 2 → 1 | Пакунок / Pack |
| `5130111D94D99E2B` | Basic Capacitor Bank — `enderio:basic_capacitor_bank` | `enderio:basic_capacitor` | 2 → 1 | Пакунок / Pack |
| `51301167AE8889AA` | Powered Spawner — `enderio:powered_spawner` | `enderio:soularium_block` | 2 → 1 | Mayhem Cube T3 |
| `51301189AE52B1F5` | SAG Mill — `enderio:sag_mill` | `kubejs:electromechanical_drive` | 1 → 1 * | Пакунок / Pack |
| `51301148615D2591` | Soul Binder — `enderio:soul_binder` | `enderio:soularium_block` | 2 → 1 | Mayhem Cube T3 |
| `514011CA86FD67E8` | Mechanical Hammer — `exdeorum:mechanical_hammer` | `enderio:dark_steel_ingot` | 3 → 1 | Пакунок / Pack |
| `5130117AB9046CD2` | Enchantment Extractor — `industrialforegoing:enchantment_extractor` | `minecraft:diamond` | 2 → 1 | Пакунок / Pack |
| `51401117A75D3720` | Netherite Furnace — `ironfurnaces:netherite_furnace` | `minecraft:netherite_ingot` | 2 → 1 | Mayhem Cube T4 |
| `513011E2552BA5EA` | Basic Induction Cell — `mekanism:basic_induction_cell` | `mekanism:energy_tablet` | 3 → 1 | Пакунок / Pack |
| `514D115314DFB698` | Chemical Crystallizer — `mekanism:chemical_crystallizer` | `kubejs:sealed_process_chamber` | 1 → 1 * | Пакунок / Pack |
| `514D110A613D8197` | Chemical Infuser — `mekanism:chemical_infuser` | `mekanism:advanced_chemical_tank` | 2 → 1 | Mayhem Cube T3 |
| `514D11AB5959BBF5` | Digital Miner — `mekanism:digital_miner` | `mekanism:teleportation_core` | 2 → 1 | Пакунок / Pack |
| `513011F6F0D21BC6` | Mekanism Laser — `mekanism:laser` | `mekanism:energy_tablet` | 2 → 1 | Пакунок / Pack |
| `513011156F29CC4E` | Pressurized Reaction Chamber — `mekanism:pressurized_reaction_chamber` | `mekanism:basic_chemical_tank` | 2 → 1 | Пакунок / Pack |
| `5130116A8EF3B812` | QIO Drive Array — `mekanism:qio_drive_array` | `mekanism:teleportation_core` | 2 → 1 | Пакунок / Pack |
| `513011835D892AF4` | Base QIO Drive — `mekanism:qio_drive_base` | `mekanism:ultimate_control_circuit` | 3 → 1 | Пакунок / Pack |
| `5130112203442BAE` | Hyper-Dense QIO Drive — `mekanism:qio_drive_hyper_dense` | `mekanism:qio_drive_base` | 3 → 1 | Пакунок / Pack |
| `5130119895187FB1` | Supermassive QIO Drive — `mekanism:qio_drive_supermassive` | `mekanism:qio_drive_time_dilating` | 3 → 1 | Пакунок / Pack |
| `513011C1C75B6080` | Time-Dilating QIO Drive — `mekanism:qio_drive_time_dilating` | `mekanism:qio_drive_hyper_dense` | 3 → 1 | Пакунок / Pack |
| `513011C1277D6FAE` | Teleporter — `mekanism:teleporter` | `mekanism:steel_casing` | 3 → 1 | Пакунок / Pack |
| `514D11F90042E7F4` | Thermal Evaporation Controller — `mekanism:thermal_evaporation_controller` | `mekanism:thermal_evaporation_block` | 3 → 1 | Mayhem Cube T2 |
| `513011D2DC7B4947` | Ultimate Energy Cube — `mekanism:ultimate_energy_cube` | `minecraft:diamond_block` | 2 → 1 | Пакунок / Pack |
| `514D113E1E8CF8B1` | Fusion Reactor Controller — `mekanismgenerators:fusion_reactor_controller` | `mekanismgenerators:fusion_reactor_frame` | 4 → 1 | Mayhem Cube T5 |
| `51301151F34C43A4` | Gas-Burning Generator — `mekanismgenerators:gas_burning_generator` | `mekanism:steel_casing` | 2 → 1 | Пакунок / Pack |
| `514D1121D23494BB` | Wind Generator — `mekanismgenerators:wind_generator` | `mekanism:energy_tablet` | 2 → 1 | Пакунок / Pack |
| `513011675037BC60` | Dimension Builder — `rftoolsdim:dimension_builder` | `minecraft:diamond` | 2 → 1 | Mayhem Cube T5 |
| `513011D1C9E27BE4` | RFTools Power Cell II — `rftoolspower:cell2` | `rftoolspower:power_core2` | 3 → 1 | Пакунок / Pack |
| `51301100A7D9F653` | RFTools Power Cell III — `rftoolspower:cell3` | `rftoolspower:power_core3` | 3 → 1 | Пакунок / Pack |
| `5130119B9A6344A6` | Advanced Dimensional Cell — `rftoolspower:dimensionalcell_advanced` | `rftoolsbase:infused_diamond` | 3 → 1 | Mayhem Cube T3 |

Recipe IDs, canonical SHA256 values and the native world-upgrade alternative are recorded per quest in [`recipe_refunds.json`](../metadata/recipe_refunds.json). The compiler validates this authored evidence before generating either reward mappings or localized guidance.

## Як це працює

1. **Тему задає глава.** Майстерня, логістика, магія, експедиції та інші гілки мають власні набори. Колишній статичний перехід на інший пул через тег `stage_N` прибрано: тема залишається темою глави, а доступність і якість залежать від поточного виробництва команди.
2. **Рівень визначається виробництвом.** Система бере найвищий рівень із шести рубежів, для якого записано справжнє виготовлення відповідного KubeJS-компонента. До першого такого запису діє рівень 0. Це окрема шкала нагород; завершення предметного завдання в квестбуці саме собою не створює виробничий запис.
3. **Кожен виріб має власне відкриття.** Для позиції з `requires_craft` потрібні всі зазначені item ID у виробничих фактах команди та рівень не нижчий за `min_level`. Виготовлена залізна бочка відкриває саме залізну бочку; золоту й незеритову потрібно реально виготовити окремо. Рівень 6 також не відкриває ще не виготовлені процесори, шасі чи поліпшення.
4. **Пакунок розігрується під час отримання.** Враховується стан команди в момент натискання «отримати», а не стан під час завершення квесту. Перший із 3–5 розіграшів обирає приз серед відкритих незвичайних або кращих позицій. Якщо таких позицій ще немає, використовується звичайний пул. Решта жеребкувань обирає доступну рідкість, потім предмет усередині неї. Повтори дозволені. Кількості однакових ресурсів додаються; кожен однаковий предмет із максимальним розміром стека 1 видається щонайбільше один раз за пакунок. Повторного жеребкування замість зайвих копій немає. Видача предметів не створює виробничих відкриттів.

Доказом служить завершена підтримувана операція виробництва з фактичним результатом. Підбирання, передавання, відкривання скрині, видача нагороди, наявність предмета в інвентарі, JEI, ME-склад і завершена заявка CPU без підтвердженої виробничої операції не є таким доказом. Перейменування, скидання чи зміна оформлення з тим самим item ID серед входів і виходу відсікаються. Для нового дорогого типу виробництва потрібен окремий надійний observer; замінювати його перевіркою інвентарю не можна.

Спостерігач зберігає особисті виробничі факти гравця та окреме об’єднання фактів його партії FTB Teams. Власні відкриття можна принести в партію; читання відкриттів партії не копіює чужі факти назад у власний запис. Виробництво машини приписується її справжньому власнику/зареєстрованому виробнику та команді, записаній при прив’язці машини, а не найближчому гравцеві. Creative, spectator і fake-player дії не створюють виробничих фактів. Джерело контракту: [`ProductionEvidence`](../src/compat/progressive-rewards/src/qm/rewards/ProductionEvidence.java).

**Після вступу в партію або виходу з неї власнику потрібно забрати й повторно встановити раніше прив’язану підтримувану машину в поточній команді, у survival/adventure.** Це прив’язує наступні справжні завершені цикли до нової команди. До повторного встановлення стара командна прив’язка не записує нові відкриття; уже записані особисті виробничі факти зберігаються.

**Старі інвентарі автоматично не індексуються.** Після оновлення старий запас, пройдені квести й уже готові машини не вважаються доказом минулого виробництва. Потрібно виконати нову підтримувану операцію. Раніше встановлена машина з придатним native-власником може записати наступний справжній завершений результат; це не зарахування старого вмісту. Без придатного власника або виробничого observer дорогі позиції залишаються закритими.

Кожна тема має 5–10 доступних стартових припасів, тож за відсутності відкриттів пакунок усе одно має вміст. Корисні партії їжі, деревини, паперу та звичайних матеріалів служать базою; виготовлені деталі й дорогі готові речі відкриваються поступово. Порожній або несправний пул переходить до `survival`, а обробник має окремий аварійний запас базових vanilla-припасів. Це захист від некоректної runtime-політики, а не колишня система зміни теми за етапом.

## How it works

1. **The chapter determines the theme.** Workshop, logistics, magic, expedition and the other branches have their own contents. The old static `stage_N` fallback is removed: the chapter keeps its theme while the team's current production facts determine eligibility and quality.
2. **Manufacture determines the level.** The system uses the highest of the six milestone levels whose exact KubeJS component has a recorded real manufacture. Before the first such fact, the level is 0. This is a separate reward scale; completing an item-detection quest does not create a production fact.
3. **Every manufactured prize has its own unlock.** An entry requires every exact ID in `requires_craft` and a level at least as high as `min_level`. Producing an iron barrel unlocks the iron barrel itself; gold and netherite barrels must each be manufactured separately. Level 6 alone does not unlock an unmade processor, chassis or upgrade.
4. **The pack is rolled when claimed.** Eligibility uses team state at the moment of claiming, not at quest completion. The first of 3–5 draws uses eligible uncommon-or-better prizes. With no eligible premium prize, it uses the ordinary pool. The remaining draws choose an eligible rarity and then an item within that rarity. Draws are with replacement. Stackable duplicate quantities combine; each identical item with native maximum stack size 1 is delivered at most once per pack. Surplus copies do not trigger replacement draws. Reward delivery never creates production facts.

Evidence comes from a completed supported production operation with a real output. Pickup, transfers, chest loot, reward delivery, inventory possession, JEI, ME storage and CPU job completion without an observed production operation are insufficient. Rename, reset or cosmetic operations with the same item ID in an input and the output are rejected. Adding a new expensive production family requires a trustworthy observer rather than an inventory check.

The observer keeps the player's own production facts and a separate union for the current FTB Teams party. A player can bring their own discoveries into a party; reading a party's discoveries does not copy other players' facts into their personal record. Machine production is attributed to its real owner/recorded producer and captured team, not a nearby player. Creative, spectator and fake-player actions do not create production facts. The contract is implemented in [`ProductionEvidence`](../src/compat/progressive-rewards/src/qm/rewards/ProductionEvidence.java).

**After joining or leaving a party, the owner must pick up and re-place a previously bound supported machine in the current team, in survival/adventure.** This binds its next real completed operations to the new team. Until re-placement, the old team binding does not record new discoveries; previously recorded personal production facts remain available.

**Existing inventories are not automatically backfilled.** Old stock, completed quests and already-built machines do not prove earlier production after upgrading. A new supported operation is required. An existing machine with a valid native owner can record its next real completed output; this does not credit its old contents. Missing valid ownership or an unsupported production observer leaves manufactured prizes locked.

Every theme contains 5–10 eligible starter supplies, so a pack has contents even without discoveries. Useful batches of food, wood, paper and ordinary materials provide the base; manufactured parts and expensive finished items unlock progressively. An empty or invalid pool falls back to `survival`, and the handler also retains emergency basic vanilla supplies. This protects against invalid runtime policy rather than changing the chapter theme by its static stage.

## Рівні та кількість призів / Levels and draw counts

Рівень — найвищий реально виготовлений рубіж у таблиці. Ваги рідкостей у чотирьох останніх стовпцях дорівнюють їхнім відсоткам **лише коли всі чотири рідкості мають доступні предмети**. За відсутності доступних предметів певної рідкості решта ваг нормалізуються; формула наведена нижче. Таблиця описує звичайні жеребкування; перше не включає common, якщо вже є відкриті кращі призи.

The level is the highest actually manufactured milestone in this table. The last four columns become rarity percentages **only when all four rarities have eligible items**. Empty rarity groups are omitted and the remaining weights are normalized, as described below. These weights describe ordinary draws; the first draw excludes common prizes when a higher-quality eligible prize exists.

| Рівень / Level | Рубіж / Milestone | Призів / Draws | Common | Uncommon | Rare | Epic |
| ---: | --- | ---: | ---: | ---: | ---: | ---: |
| 0 | До першого виробничого рубежу / Before the first production milestone | 3 | 50 | 35 | 12 | 3 |
| 1 | `kubejs:industrial_chassis` — Промислове шасі / Industrial Chassis | 3 | 45 | 36 | 15 | 4 |
| 2 | `kubejs:precision_mechanism` — Точний механізм / Precision Mechanism | 4 | 38 | 38 | 19 | 5 |
| 3 | `kubejs:dimensional_matrix` — Вимірна матриця / Dimensional Matrix | 4 | 30 | 40 | 23 | 7 |
| 4 | `kubejs:stellar_alloy` — Зоряний композит / Stellar Alloy | 4 | 24 | 40 | 27 | 9 |
| 5 | `kubejs:mythic_circuit` — Міфічна схема / Mythic Circuit | 5 | 18 | 40 | 30 | 12 |
| 6 | `kubejs:emc_core` — Ядро EMC / EMC Core | 5 | 12 | 40 | 33 | 15 |

## Усі нагороди видно в книзі / Visible reward contents in the questbook

Гарантовані компоненти повернення показуються **окремими іконками з точною кількістю в секції нагород квесту**, поруч з основною кнопкою отримання. Звичайні куби та вступна книга зберігають нативні item-іконки; у 29 збільшених нагородах видно кількість 2. У наборі «куб + компенсація» основна іконка показує куб, а додаткові — кожен гарантований стек компонентів. Куб не дублюється другою іконкою.

Guaranteed refund components appear as **separate item icons with exact counts in the quest's reward section**, beside the main claim button. Standalone cubes and the introductory book retain native item icons; the 29 increased cube rewards show a count of 2. In a cube-and-refund bundle, the original icon shows the cube and the additional icons show each guaranteed component stack. The cube icon is not duplicated.

Натисніть **«Переглянути нагороди»** над описом відкритого квесту або **Ctrl+Shift+V**. У книзі відкриється прокручуваний каталог: усі гарантовані стеки та всі можливі позиції відповідного тематичного пулу, включно з ще недоступними. Для кожного предмета показано назву, кількість, рідкість, мінімальний виробничий рівень і конкретні умови виготовлення; повний текст умов є в підказці. Клавіші рецептів і застосувань JEI працюють при наведенні на предмет. Клавіша відкриття каталогу не перехоплює введення у сфокусованому текстовому полі чи іншому модальному вікні.

Click **View rewards** above the open quest's description, or press **Ctrl+Shift+V**. The book opens a scrollable catalogue containing every guaranteed stack and every possible entry in that quest's themed pool, including locked entries. Each item shows its name, quantity, rarity, minimum production level and exact manufacturing conditions; its tooltip contains the complete conditions. JEI recipe and use keys work while hovering over an item. The catalogue shortcut does not intercept a focused text field or another modal window.

Список можливостей **не означає видачу всіх предметів**. Звичайний пакунок дає 3–5 випадкових призів відповідно до виробничого розвитку; один із цих призів обирається серед доступних незвичайних або кращих предметів, якщо такі є. Каталог пояснює ці правила й показує статичні умови доступу. Поточні відкриття та нормалізовані відсотки перевіряються командами з наступного розділу; фактичну доступність і видачу визначає сервер при отриманні.

The list of possibilities **does not grant every displayed item**. An ordinary pack gives 3–5 random prizes according to production progress; one of those draws uses eligible uncommon-or-better items when any exist. The catalogue explains those rules and displays the eligibility conditions. The commands in the next section show current discoveries and normalized percentages; the server determines actual eligibility and delivery when claiming.

Додаткові іконки та кнопка каталогу лише відкривають перегляд. Основна кнопка FTB Quests і далі отримує весь набір за тим самим персональним reward ID, один раз для кожного учасника. Вони не створюють окремих нагород, не змінюють позначку отримання та не записують виробничі факти. Компілятор створює [`reward_catalog.json`](../src/compat/quest-ui/resources/assets/qm_quest_ui/reward_catalog.json) з тих самих глав, runtime-пулів і metadata компенсацій. Реалізація перегляду: [`QuestRewardPreview`](../src/compat/quest-ui/src/qm/questui/client/QuestRewardPreview.java), [`RewardCataloguePanel`](../src/compat/quest-ui/src/qm/questui/client/RewardCataloguePanel.java).

The extra icons and catalogue button only open a preview. The original FTB Quests button still claims the entire bundle under the same personal reward ID, once for each member. Preview controls do not create separate rewards, change claim records or record manufacture. The compiler generates [`reward_catalog.json`](../src/compat/quest-ui/resources/assets/qm_quest_ui/reward_catalog.json) from the same chapters, runtime pools and refund metadata. The view is implemented by [`QuestRewardPreview`](../src/compat/quest-ui/src/qm/questui/client/QuestRewardPreview.java) and [`RewardCataloguePanel`](../src/compat/quest-ui/src/qm/questui/client/RewardCataloguePanel.java).

## Як подивитися поточні шанси / Viewing current chances

Команди доступні гравцеві й лише читають стан. Вони не отримують нагород, не виконують жеребкування та не змінюють відкриття чи позначки отримання. Каталог у книзі показує гарантований і можливий вміст та його умови; фактичну поточну доступність і відсотки показують ці команди:

These player commands are read-only. They do not claim rewards, draw prizes, or change production facts or claim records. The in-book catalogue shows guaranteed and possible contents with their conditions; these commands provide actual current availability and percentages:

```text
/soqm rewards status
/soqm rewards pool logistics
/soqm rewards pool engineering
/soqm rewards pool arcane
```

`/soqm rewards status` показує рівень, кількість жеребкувань, шість рубежів та список тем із доступними й закритими позиціями. `/soqm rewards pool <key>` показує кількість, рідкість і поточний нормалізований шанс **за звичайне жеребкування** та окремий шанс для першого; закриті предмети перелічують потрібний рівень і відсутні виробничі факти. Ключі всіх тем є у наступному розділі. Інтерфейс використовує українську для української мови клієнта, англійську — для інших мов.

`/soqm rewards status` shows the level, draw count, six milestones and theme availability counts. `/soqm rewards pool <key>` shows quantities, rarities and the current normalized chance **per ordinary draw** and a separate first-draw chance, followed by locked items and their missing level/production requirements. All theme keys appear below. The interface uses Ukrainian for Ukrainian client language and English for other languages.

Джерело команд і розрахунку: [`soqm_progressive_rewards.js`](../overrides/kubejs/server_scripts/soqm_progressive_rewards.js). Підрахунок у команді та розіграш використовують той самий відбір доступних позицій.

Command and calculation source: [`soqm_progressive_rewards.js`](../overrides/kubejs/server_scripts/soqm_progressive_rewards.js). The command and the draw use the same eligibility calculation.

## Коротко про 14 тем / The fourteen themes

| Ключ / Key | Тема / Theme | Позицій / Entries | Вміст / Contents |
| --- | --- | ---: | --- |
| [survival](#survival) | Припаси для старту / Starting Supplies | 20 | Їжа, базові інструменти, перший наплічник / Food, basic tools, first backpack |
| [workshop](#workshop) | Запас майстерні / Workshop Supplies | 24 | Пластини IE, приводи, шасі, модулі печей / IE plates, drives, chassis, furnace augments |
| [engineering](#engineering) | Запас для виробництва / Factory Supplies | 24 | Процесори AE2, схеми й сплави Mekanism, конденсатори / AE2 processors, Mekanism circuits/alloys, capacitors |
| [power](#power) | Запас енергетика / Power Workshop Supplies | 24 | Дроти, роз’єми, накопичувачі й енергокуби / Wires, connectors, accumulators and energy cubes |
| [logistics](#logistics) | Припаси для складу / Storage Supplies | 24 | Шість бочок Sophisticated, поліпшення стосів, деталі AE2 / Six Sophisticated barrels, stack upgrades, AE2 parts |
| [scholar](#scholar) | Матеріали дослідника / Research Supplies | 20 | Картки AE2, книжкові полиці, квантовий процесор / AE2 cards, bookshelves, quantum processor |
| [arcane](#arcane) | Запас для магічної майстерні / Arcane Workshop Supplies | 24 | Пелюстки, лінзи, пахощі, вироби EvilCraft, магічні компоненти / Petals, lenses, incense, EvilCraft items, arcane components |
| [expedition](#expedition) | Припаси для експедиції / Expedition Supplies | 22 | Їжа, сувої Waystones, парашути, дорожні аксесуари / Food, Waystones scrolls, parachutes, travel accessories |
| [settlement](#settlement) | Посилка для будівельника / Settlement Supplies | 24 | Модова деревина й плитка, ящики, бочки, ліхтарі / Modded wood and tiles, crates, barrels, lanterns |
| [space](#space) | Запас космічної майстерні / Space Workshop Supplies | 24 | Деталі Ad Astra, космічні пластини, турбіна, захист / Ad Astra parts, space-metal plates, turbine, shielding |
| [processing](#processing) | Сировина для переробки / Processing Supplies | 24 | Сітки й молоти, Ex Machinis, модулі плавлення / Meshes and hammers, Ex Machinis, smelting augments |
| [hunting](#hunting) | Припаси мисливця / Hunting Supplies | 24 | Деталі ферми мобів, поліпшення, збір предметів і XP / Mob-farm parts, upgrades, item and XP collection |
| [equipment](#equipment) | Запас для спорядження / Equipment Supplies | 24 | Наплічники, поліпшення, інструменти, аксесуари й сигіли / Backpacks, upgrades, tools, accessories and sigils |
| [masterwork](#masterwork) | Запас майстра / Masterwork Supplies | 24 | Усі головні компоненти, ProjectE та матеріали Avaritia / All main components, ProjectE and Avaritia materials |

## Формула шансів і бюджет / Probability formula and budget

Нехай `L` — поточний рівень, `D` — множина виробничих фактів команди, `d` — кількість жеребкувань. Для предмета `i` маємо рідкість `g(i)`, кількість `c_i` і внутрішню вагу `w_i`. Доступність означає `min_level_i ≤ L` та `requires_craft_i ⊆ D`.

Let `L` be the current level, `D` the team's production facts, and `d` the draw count. Entry `i` has rarity `g(i)`, quantity `c_i`, and entry weight `w_i`. Eligibility requires `min_level_i ≤ L` and `requires_craft_i ⊆ D`.

Позначимо доступні предмети рідкості `g` як `S_g`, рідкості з непорожнім `S_g` як `R`, а вагу рідкості на рівні `L` як `W_{L,g}`. **Недоступний предмет має шанс 0.** Для доступного:

Write `S_g` for the eligible entries in rarity `g`, `R` for rarities with nonempty `S_g`, and `W_{L,g}` for the rarity weight at level `L`. **An ineligible item has probability 0.** For an eligible item:

$$
P(i\mid L,D)=
\frac{W_{L,g(i)}}{\sum_{h\in R}W_{L,h}}
\cdot
\frac{w_i}{\sum_{j\in S_{g(i)}}w_j}
$$

Перший приз має окремий розподіл `Q(i)`: та сама формула лише для доступних uncommon, rare та epic. Якщо жодна така група не доступна, `Q(i)=P(i)`. Решта `d−1` призів використовує `P(i)`. Повтори дозволені.

The first prize has distribution `Q(i)`: the same formula restricted to eligible uncommon, rare and epic entries. If all three are empty, `Q(i)=P(i)`. The other `d−1` prizes use `P(i)`, with replacement.

$$
P(N_i>0)=1-(1-Q(i))(1-P(i))^{d-1},\qquad
E[N_i]=\begin{cases}
P(N_i>0), & \text{maximum stack size }1\\
c_i\bigl(Q(i)+(d-1)P(i)\bigr), & \text{otherwise}
\end{cases}
$$

Внутрішня вага в каталозі **не є готовим відсотком**. Відкриття ще одного предмета тієї самої рідкості ділить вагу цієї рідкості між більшою кількістю результатів. Саме тому інтерфейс показує актуальний розрахунок, а каталог зберігає ваги та умови.

An entry's catalogue weight **is not its final percentage**. Unlocking another entry in the same rarity distributes that rarity's weight across more eligible results. The interface therefore shows the current calculation while the catalogue records weights and conditions.

| Приклад / Example | Звичайне / Ordinary draw | Перше / Reserved draw | Хоча б один за пакунок / At least one per pack |
| --- | ---: | ---: | ---: |
| Logistics, рівень 2, виготовлена лише золота бочка з manufactured-позицій / Only the gold barrel manufactured | 19 / (38 + 19) = 33.3333% | 100% | 100% за 4 / over 4 draws |
| Logistics, рівень 6, усе відкрито: незеритова бочка / Level 6, everything unlocked: netherite barrel | 3.7500% | 4.2614% | 17.8344% за 5 / over 5 draws |
| Рівень 6, усі рідкості доступні: rare або epic / Level 6, all rarities available: rare or epic | 48% | 48 / 88 = 54.5455% | 96.6765% за 5 / over 5 draws |

Кількість за одне жеребкування — 1–16 одиниць. Усі rare/epic, KubeJS-компоненти, бочки, наплічники та їхні поліпшення даються по одному. Звичайні ресурси мають практичні партії: наприклад, 6 заліза, 8 міді, 16 редстоуну; базові схеми та сплави — по 4, кабелі — по 8. Кожен виготовлений компонент усе ще вимагає власного виробничого запису. Максимум для суто звичайних припасів — 80 предметів за п'ять жеребкувань; це межа, а не гарантована кількість чи вартість.

A draw contains 1–16 units. Rare/epic prizes, KubeJS components, barrels, backpacks and upgrades remain single-item draws. Ordinary supplies have practical batch sizes: for example, 6 iron, 8 copper or 16 redstone; basic circuits/alloys give 4 and cables give 8. Every manufactured component still needs its own production fact. Five ordinary supply draws have a hard upper bound of 80 items; this is neither guaranteed quantity nor value.

| Пул / Pool | Середня кількість до ліміту спорядження, рівень 6, усе відкрито / Expected selected units before the equipment limit, level 6, everything unlocked |
| --- | ---: |
| engineering | 14.327 |
| logistics | 15.635 |
| arcane | 9.320 |
| space | 13.931 |
| masterwork | 6.248 |

Ці значення — умовні математичні сподівання до застосування ліміту предметів із розміром стека 1; для фактичної видачі вони є верхньою межею. Це не результати ігрового проходження. Їхню загальну видачу визначають момент отримання, відкриті вироби та кількість гравців. Окремі гарантовані компенсації мають фіксований бюджет: 42 одиниці компонентів у 41 квесті на кожного гравця, який виконає всі відповідні умови й забере всі ці нагороди. Особистий режим множить загальну командну видачу на кількість учасників, які отримають власні нагороди. Більша нагорода не стає повторюваним квестом; випадковий дорогий приз не може випасти до відповідного виробничого факту.

These are conditional expectations before the maximum-stack-size-1 limit, so they are upper bounds on expected delivery. They are not playthrough results. Claim timing, discovered outputs and player count determine their total delivery. The separate guaranteed refunds have a fixed budget: 42 component units across 41 quests per player who satisfies every corresponding condition and claims all these rewards. Personal claims increase total team delivery with the number of members claiming their own rewards. Larger packs do not make quests repeatable; a random expensive prize cannot drop before its exact production fact exists.

**Пластик і HDPE виключені з призів, зокрема після відкриття.** Готовий радіаційний захист може бути призом після його фактичного виготовлення: HDPE у рецепті захисту не перетворюється на окрему нагороду. Контейнери, енергокуби, планшети мани й інші предмети створюються свіжими, із порожнім/default станом. Копіювати зі зразка вміст, storage UUID, енергію, ману, поліпшення чи афікси заборонено.

**Plastic and HDPE are excluded from prizes, including after unlock.** A finished radiation shield can be rewarded after its actual manufacture; HDPE used in its recipe is not itself a prize. Containers, energy cubes, mana tablets and other items are created fresh with empty/default state. Their contents, storage UUIDs, energy, mana, upgrades and affixes are never copied from the manufactured specimen.

## Особисті отримання та оновлення старого світу / Personal claims and existing-world upgrades

**Поточне перебалансування не дає повторного отримання наявних нагород.** Усі 1628 наявних зовнішніх ID та особисті області збережено. Уже забраний пакунок або куб залишається забраним; додатковий куб у 29 вибраних слотах застосовується лише до ще не отриманих призів. 15 нових квестів спорядження мають нові ID власних нагород; каталог не додає нових claim slots.

**This rebalance does not reopen existing rewards.** All 1628 existing outer IDs and personal scopes are retained. An already claimed pack or cube stays claimed; the additional cube in 29 selected slots applies only to unclaimed rewards. The 15 new equipment quests have new IDs for their own rewards; the catalogue adds no claim slots.

### Історична міграція 0.4.8 / Historical 0.4.8 migration

FTB Quests зберігає позначку отримання за **зовнішнім ID нагороди та областю власника**. Історичне оновлення 0.4.8 установило для наявних нагород `team_reward: false`, `auto: "disabled"`, а для звичайного пакунка — `type: "custom"`, зі збереженням ID квестів, задач і нагород. Та навмисна зміна командної області на особисту створила інше право отримання. Поточне оновлення вже особисті області не змінює; таблиця нижче описує саме перехід 0.4.8.

FTB Quests keys a claim by the **outer reward ID and owner scope**. The historical 0.4.8 update set existing rewards to `team_reward: false`, `auto: "disabled"`, and ordinary packs to `type: "custom"`, retaining quest, task and reward IDs. That deliberate move from team scope to personal scope created a different claim entitlement. The current update preserves the already personal scopes; the following table describes the 0.4.8 transition specifically.

| Старий стан / Previous state | Поведінка 0.4.8 / Version 0.4.8 behavior |
| --- | --- |
| Командний приз уже отримано під ключем `(NIL_UUID, reward ID)` / A shared reward was already claimed under `(NIL_UUID, reward ID)` | **Кожен гравець отримує одне нове особисте право** під `(player UUID, reward ID)`. Старий NIL-запис не зараховується як його особистий claim. / **Each player gets one new personal entitlement** under `(player UUID, reward ID)`; the old NIL record is not their personal claim. |
| Особистий приз уже отримано під `(player UUID, reward ID)` / A personal reward was already claimed under `(player UUID, reward ID)` | Залишається отриманим; повторної видачі немає. / Remains claimed, with no second delivery. |
| Квест завершено, особисту нагороду ще не отримано / Quest completed, personal reward still unclaimed | Дає новий вміст за поточними відкриттями при ручному отриманні. / Gives the new contents using current discoveries when manually claimed. |
| Особиста вступна книга вже отримана / Personal introductory book already claimed | Її старе особисте отримання зберігається. / Its existing personal claim remains recorded. |
| Старі припаси, машини чи компоненти вже є в інвентарі / Old supplies, machines or components already exist in inventory | Залишаються у світі, але автоматичних виробничих відкриттів із них немає. / Remain in the world, without automatic production-fact backfill. |

У 0.4.8 ця міграція від спільного подарунка до особистого охопила і фіксовані куби. Власні вже отримані нагороди не скидалися; старі спільні отримання не копіювалися у всі UUID. Повторне натискання після особистого claim не дає ще один пакунок. Збереження особистих отримань при вході/виході з партії, збереженні та перезапуску залишається вимогою native-перевірки, описаної нижче.

That 0.4.8 migration from a shared gift to personal gifts also covered fixed cubes. Existing personal claims were not cleared, and previous shared claims were not copied to every UUID. Clicking again after a personal claim cannot award another pack. Preserving personal claims through party changes, saves and restarts remains part of the native validation contract described below.

Стан виробництва й стан отримання — окремі записи. Видача використовує native `ItemReward` для нових порожніх/default стеків, а FTB Quests зберігає справжній зовнішній claim ID. Тимчасові об’єкти видачі не створюють нових постійних нагород. Джерела: [`campaign_rewards.py`](../scripts/campaign_rewards.py), [`soqm_progressive_rewards.js`](../overrides/kubejs/server_scripts/soqm_progressive_rewards.js) та закріплені FTB Quests/Teams у [`mods.lock.json`](../metadata/mods.lock.json).

Production facts and claim state are separate records. Delivery uses native `ItemReward` with new empty/default stacks, while FTB Quests owns the real outer claim ID. Transient delivery objects do not introduce persistent reward IDs. Sources: [`campaign_rewards.py`](../scripts/campaign_rewards.py), [`soqm_progressive_rewards.js`](../overrides/kubejs/server_scripts/soqm_progressive_rewards.js), and pinned FTB Quests/Teams in [`mods.lock.json`](../metadata/mods.lock.json).

## Фіксовані рубежі / Fixed milestones

Куби є у **193 із 1642 квестів: 11,75%, або приблизно 11,8%**. Ці рубежі охоплюють зібрані установки IE, ключові кроки Mekanism та AE2, автоматизацію Ex Deorum/Ex Machinis, розвиток сховищ, босів і нові виміри. Рівень куба не перевищує внутрішнього рівня квесту. У главі навичок кубів немає: вона повторює вже винагороджені матеріальні рубежі.

Cubes appear in **193 of 1642 quests: 11.75%, or approximately 11.8%**. These milestones cover IE structures, major Mekanism/AE2 steps, Ex Deorum/Ex Machinis automation, storage progress, bosses and new dimensions. A cube's tier never exceeds the quest's internal progression level. The skills chapter has no extra cubes because it repeats already rewarded material milestones.

Поточне збільшення стосується кількості кубів, а не кількості рубежів: **193 → 222**, тобто **29 / 193 × 100 = 15,0259%**. [`cube_reward_bonus.json`](../metadata/cube_reward_bonus.json) фіксує базовий commit, усі 193 початкові рубежі та 29 вибраних quest ID. За рівнями T0–T6 додано відповідно **1, 4, 6, 7, 5, 4, 2** куби. У 184 прямих item reward objects тепер міститься 213 кубів; дев’ять custom-наборів із компенсацією зберігають ще дев’ять. Кількість і вибір не залежать від випадкового роллу та не перераховуються через додавання нових квестів. Внутрішні події, вибухи й випадковий вміст кубів не змінено.

The current increase changes cube quantities, not the number of milestones: **193 → 222**, or **29 / 193 × 100 = 15.0259%**. [`cube_reward_bonus.json`](../metadata/cube_reward_bonus.json) records the baseline commit, all 193 original milestones and the 29 selected quest IDs. Tiers T0–T6 receive **1, 4, 6, 7, 5, 4, 2** additional cubes respectively. The 184 direct item reward objects now contain 213 cubes; the nine custom refund bundles retain another nine. Quantities and selection do not depend on a random roll or recalculate when new quests are added. Internal cube events, explosions and random contents are unchanged.

Рівні IE узгоджено з потрібними рецептами: коксова піч, початкова домна й піч сплавів дають T0; базові промислові мультиблоки після шасі — T1. Від них не вимагається точний механізм лише заради нагороди. Пізні установки з вимірною матрицею зберігають відповідний вищий рівень.

IE cube tiers follow the required recipes: the coke oven, initial blast furnace and alloy kiln award T0; basic industrial multiblocks after Industrial Chassis award T1. Precision Mechanism is not added as a requirement merely to justify a reward tier. Later structures that require Dimensional Matrix retain their higher tiers.

Нижче наведено рівні кубів для початкових 14 прикладів; повний список має 193 рубежі. Кількість у конкретній нагороді — один куб, або два для 29 вибраних прямих слотів. Кубовий рубіж не виконує 3–5 жеребкувань звичайного пакунка. Власні події кубів не включено в наведені очікування припасів. / The table shows cube tiers for the original 14 examples; the complete list contains 193 milestones. A specific reward contains one cube, or two in the 29 selected direct slots. A cube milestone does not perform the ordinary pack's 3–5 draws. Cube events are outside the supply expectations above.

| Рубіж / Milestone | Cube |
| --- | ---: |
| Промислове шасі / Industrial Chassis | T1 |
| Точний механізм / Precision Mechanism | T2 |
| Вимірна матриця / Dimensional Matrix | T3 |
| Зоряний композит / Stellar Alloy | T4 |
| Міфічна схема / Mythic Circuit | T5 |
| Ядро EMC / EMC Core | T6 |
| Контрольований перший цикл реактора / Controlled first fission cycle | T4 |
| Керований термоядерний синтез / Sustained fusion | T5 |
| Ozgum | T3 |
| Rutia | T3 |
| Перша експедиція в Nether / First Nether expedition | T2 |
| Нові горизонти End / First End expedition | T3 |
| Розвиток спорядженої реліквії / Upgrade a worn relic | T2 |
| Готовність до великих експедицій / Expedition readiness | T1 |

## Виробничі hooks і межі перевірки / Production hooks and validation scope

**Історичний результат 0.4.10:** архівний [звіт перевірки 0.4.10](../reports/verification-0.4.10.md) зафіксував успішний native dedicated-server запуск для 43 активних рецептів витрат, персональних компенсацій, відмов без витрати claim, відсутності повторної видачі, апгрейду печі та збереження після reload. Цей результат стосується саме того релізу й не підтверджує поточне робоче дерево. Нижче наведено контракт реалізованих шляхів спостерігача; актуальні серверні результати потрібно отримувати окремим запуском перевірки. Графічний клієнт і повне survival-проходження не входили до історичного серверного звіту.

**Historical 0.4.10 result:** the archived [0.4.10 verification report](../reports/verification-0.4.10.md) recorded a successful native dedicated-server run covering 43 active cost recipes, personal refunds, refusals without consuming claims, duplicate rejection, furnace upgrades and post-reload persistence. That result applies to that release and does not validate the current working tree. The rows below describe the implemented observer contract; current server results require a separate validation run. The historical server report did not include a graphical client or complete survival playthrough.

| Шлях / Path | Очікувана умова запису / Intended recorded event | Приклади / Examples |
| --- | --- | --- |
| Звичайна сітка виготовлення, ручне ME / Player crafting grid, manual ME crafting | Native `ItemCraftedEvent`, справжній гравець, непорожні входи, output ID відсутній серед входів / Real player, nonempty consumed recipe inputs, output ID absent from the inputs | KubeJS-компоненти, схеми Mekanism в authored grid-рецептах, бочки та поліпшення / KubeJS parts, authored grid-made Mekanism circuits, barrels and upgrades |
| AE2 Molecular Assembler | Результат прийнято native-логікою; входи замінено залишками рецепта / Native output accepted and recipe inputs replaced with remainders | Підтримувані закодовані crafting-рецепти / Supported encoded crafting recipes |
| AE2 Inscriber | Прийнятий вихід і фактичне споживання середнього входу, без same-item перетворення / Accepted output and actual middle-input consumption, excluding same-item transformations | Logic, Calculation, Engineering Processor |
| Mekanism Metallurgic Infuser | Native cached-recipe finish після споживання й видачі, однозначний вихід / Native cached-recipe finish after consumption and output, unambiguous result | Basic Control Circuit; Infused, Reinforced, Atomic Alloy |
| Mekanism Infusing Factory | Та сама умова завершення, тільки `FactoryType.INFUSING` / The same completed-operation condition, restricted to `FactoryType.INFUSING` | Ті самі схеми та сплави у фабриці / The same circuits and alloys in a factory |
| Sophisticated crafting serializers | Справжній результат vanilla/ME crafting для зміни рівня та upgrade-рецептів / Actual vanilla/ME craft result for tier and upgrade recipes | Storage tier upgrades, backpack upgrades, next-tier upgrades |
| Sophisticated upgrade на встановленій бочці / In-world storage tier upgrade | Native upgrade успішний, предмет поліпшення реально витрачено, новий block-item ID відрізняється / Native upgrade succeeded, upgrade item was consumed, and the resulting block-item ID changed | Підвищення рівня вже встановленої бочки / Upgrading an already-placed barrel |
| Iron Furnaces upgrade установленої печі / In-world furnace tier upgrade | Native перехід від правильного попереднього tier, точний предмет поліпшення витрачено, фактичний кінцевий блок і його furnace block entity перевірено / Native transition from the required tier, exact upgrade item consumed, resulting block and furnace block entity verified | Обсидіанова → незеритова піч / Obsidian → Netherite Furnace |

Native-джерела: [`QmProgressiveRewards`](../src/compat/progressive-rewards/src/qm/rewards/QmProgressiveRewards.java), [`ProductionEvidence`](../src/compat/progressive-rewards/src/qm/rewards/ProductionEvidence.java), [`ProductionPolicy`](../src/compat/progressive-rewards/src/qm/rewards/ProductionPolicy.java), [mixin-класи](../src/compat/progressive-rewards/src/qm/rewards/mixin/). Докази в каталозі вказують реальні продуктивні recipe ID й способи; native whitelist задає точні дозволені output item ID, а не закриває чесні варіанти деревини чи альтернативні supported-рецепти.

Native sources: [`QmProgressiveRewards`](../src/compat/progressive-rewards/src/qm/rewards/QmProgressiveRewards.java), [`ProductionEvidence`](../src/compat/progressive-rewards/src/qm/rewards/ProductionEvidence.java), [`ProductionPolicy`](../src/compat/progressive-rewards/src/qm/rewards/ProductionPolicy.java), and [mixin classes](../src/compat/progressive-rewards/src/qm/rewards/mixin/). Catalogue evidence identifies real productive recipe IDs and methods; the native whitelist uses exact permitted output item IDs without excluding legitimate wood variants or alternative supported recipes.

Потрібно перевірити справжнє завершення кожного підтримуваного каналу, заблокований вихід і нестачу входів, відсутність фактів від pickup/reward/reset, коректного власника й команди, creative/fake/spectator, вихід власника з гри, збереження/reload, особисті claims двох гравців, старий NIL-claim і збережений старий personal-claim, відсутній/пошкоджений policy та відсутній observer. Окремо слід перевірити українські й англійські команди, назви та читабельність у клієнті.

Validation must cover real completion for every supported channel, blocked output and missing inputs, no facts from pickup/reward/reset, correct owner and team, creative/fake/spectator actors, offline owners, save/reload, two players' personal claims, old NIL claims and preserved old personal claims, missing/invalid policy, and a missing observer. Ukrainian/English commands, labels and client readability require separate client review.

Для компенсацій [`ProgressiveRewardsProbe`](../tests/runtime/ProgressiveRewardsProbe.java) додатково перевіряє 43 активні рецепти витрат для 41 виробу, відмову до витрати claim, невідомий mapping, невірного батька, пошкоджену політику без блокування сторонніх нагород, особисту видачу двом учасникам і точне збереження куба. Повторні custom events, перехід між командами, дискове збереження та отримання після reload не повинні створювати повторних виплат. Повернений компонент не повинен відкривати сам себе у випадковому пулі. Native-джерело цього контракту — [`RecipeRefunds`](../src/compat/progressive-rewards/src/qm/rewards/RecipeRefunds.java).

For refunds, [`ProgressiveRewardsProbe`](../tests/runtime/ProgressiveRewardsProbe.java) additionally checks 43 active cost recipes for 41 outputs, refusal before a claim is spent, unknown mappings, wrong parent ownership, invalid policy without blocking unrelated rewards, two members' personal delivery and exact cube preservation. Repeated custom events, team transitions, disk persistence and post-reload claims must not duplicate payouts. A returned component must not unlock itself in random pools. [`RecipeRefunds`](../src/compat/progressive-rewards/src/qm/rewards/RecipeRefunds.java) implements that native contract.

## Редагування й відтворення / Editing and regeneration

Змінювати склад потрібно в [`metadata/reward_pools.json`](../metadata/reward_pools.json), а підтвердження — у [`metadata/reward_item_evidence.json`](../metadata/reward_item_evidence.json). Зберігайте позитивні ваги, 1–16 одиниці за жеребкування, одноштучні дорогі призи, двомовні назви, точний own-item gate і щонайменше п’ять безпечних стартових результатів у кожній темі. Новий machine-only приз потребує підтримуваного й перевіреного observer. Інвентар, квестова детекція чи JEI не є заміною.

Edit contents in [`metadata/reward_pools.json`](../metadata/reward_pools.json) and evidence in [`metadata/reward_item_evidence.json`](../metadata/reward_item_evidence.json). Preserve positive weights, 1–16 units per draw, single-unit expensive prizes, bilingual names, exact own-item gates and at least five safe starter results per theme. A new machine-only prize requires a supported and validated observer. Inventory, quest detection and JEI are not substitutes.

Компенсації редагуються в [`metadata/recipe_refunds.json`](../metadata/recipe_refunds.json). Для кожного виробу вкажіть один основний квест, усі активні авторські рецепти та їхні SHA256, прямий дорогий інгредієнт і мінімальну витрату серед альтернатив. `partial_ingredient` повертає не більше половини повторюваного інгредієнта; `one_component` допускає один неподільний компонент, якщо щонайменше два інші різні авторські компоненти залишаються витраченими. Новий маршрут виготовлення потребує native observer і перевірки його фактичних витрат. Не додавайте другий refund slot, виробничий запис під час видачі чи перевірку володіння замість виготовлення.

Edit refunds in [`metadata/recipe_refunds.json`](../metadata/recipe_refunds.json). For each output, record one primary quest, every active authored recipe and its SHA256, a direct expensive ingredient and the minimum consumption across alternatives. `partial_ingredient` returns at most half a repeated ingredient; `one_component` permits one indivisible component when at least two other distinct authored components remain consumed. A new manufacture route requires a native observer and verification of its actual costs. Keep one refund slot, and do not record manufacture during payout or substitute possession for manufacture.

`campaign_rewards.apply(chapters, locale)` формує особисті ordinary custom rewards зі збереженими наявними ID та зберігає особисту книгу. Усі milestone-куби лишаються фіксованими: дев’ять видаються через custom reward разом із компенсацією, решта — як ItemReward; для 29 вибраних прямих слотів установлюється точна кількість 2 з `random_bonus: 0`. Фіксований бюджет і вибір задає [`cube_reward_bonus.json`](../metadata/cube_reward_bonus.json). Runtime-політика містить mapping зовнішнього reward ID до quest ID, теми та, за наявності, `refund` і `cube`. Із тих самих даних генерується клієнтський `reward_catalog.json` із гарантованими стеками й усіма можливими позиціями пулів. `campaign_refunds.apply_locale` замінює старі абзаци компенсації в українських та англійських описах, включно з видаленими записами політики. Старі 14 статичних random-таблиць попередньої політики прибираються під час генерації; нова система не використовує їхні незмінні відсотки.

`campaign_rewards.apply(chapters, locale)` generates personal ordinary custom rewards while retaining existing IDs and preserves the personal book. All milestone cubes remain fixed: nine use custom rewards alongside their refund, while the rest remain ItemRewards; the 29 selected direct slots receive an exact count of 2 with `random_bonus: 0`. [`cube_reward_bonus.json`](../metadata/cube_reward_bonus.json) defines the fixed budget and selection. Runtime policy maps each outer reward ID to its quest ID, theme and optional `refund` and `cube`. The same data generates the client `reward_catalog.json`, including guaranteed stacks and every possible pool entry. `campaign_refunds.apply_locale` replaces old refund paragraphs in Ukrainian and English descriptions, including entries removed from the policy. Regeneration removes the previous policy's fourteen static random tables; the new system does not use their fixed percentages.

Відтворення після редагування / Regenerate after editing:

```sh
python3 scripts/campaign_refresh.py
python3 scripts/localize_english.py
python3 scripts/soqm.py lock
```

Цільові офлайн-перевірки політики та відбору / Targeted offline policy and selection checks:

```sh
python3 scripts/campaign_rewards.py --check
python3 -m unittest discover -s tests -p test_rewards.py -v
python3 -m unittest discover -s tests -p test_recipe_refunds.py -v
python3 -m unittest discover -s tests -p test_production_observer.py -v
node tests/progressive-rewards.mjs
```

Повна перевірка пакунка та окремий native-серверний запуск / Full package checks and separate native-server probe:

```sh
./scripts/check
python3 scripts/runtime_probe.py
```

`campaign_rewards.py --write` відтворює runtime-політику та клієнтський каталог із уже згенерованих глав і прибирає відомі застарілі таблиці; самі глави потрібно оновлювати через `campaign_refresh.py`. Python і Node перевірки мають узгоджувати schema, точні шанси, власне виготовлення, fallback, кількість кубів і відповідність preview фактичним гарантованим стекам та пулам. Native-перевірка має підтвердити фактичні класові hooks, видачу, дублікати й непорожню міграцію. `quest-ui/build.py --verify` включає окремий `RewardCatalogueProbe` для згенерованого ресурсу; графічну читабельність це не перевіряє. Команди вище описують процедуру й не є твердженням, що її вже виконано для конкретного commit.

`campaign_rewards.py --write` rebuilds the runtime policy and client catalogue from already-generated chapters and removes the known obsolete tables; use `campaign_refresh.py` to update chapters themselves. Python and Node checks must agree on schema, exact probabilities, manufacture gates, fallbacks, cube quantities and preview equality with actual guaranteed stacks and pools. Native validation must establish real class hooks, delivery, duplicate rejection and nonempty migration. `quest-ui/build.py --verify` includes a separate `RewardCatalogueProbe` for the generated resource; it does not check graphical readability. The commands above describe the procedure rather than asserting it has already run for a particular commit.

## Повний каталог / Full catalogue

Нижче наведено всі **324** позицій. `×` — фіксована кількість за одне жеребкування, `w` — відносна вага **всередині своєї рідкості**, `L` — мінімальний рівень. `Цей ID / Same ID` означає точну умову `requires_craft: [item ID з першого стовпця]`; потрібен реальний виробничий факт команди, а не наявність предмета. `—` означає стартові припаси без умови виробництва. Рідкості: `common` — звичайна, `uncommon` — незвичайна, `rare` — рідкісна, `epic` — епічна. Нульовий `L` не скасовує own-item gate.

All **324** entries appear below. `×` is the fixed quantity per draw, `w` is the relative weight **within its rarity**, and `L` is the minimum level. `Цей ID / Same ID` means the exact condition `requires_craft: [the item ID in the first column]`; it requires the team's real production fact, not possession. `—` denotes starter supplies without a production requirement. A minimum level of 0 does not remove the own-item gate. Use the formula or in-game read-only command for actual percentages.

Для кожного manufactured-предмета точні активні recipe ID, serializer, native/authored джерело й дані рецепта доступні за його item ID у [`reward_item_evidence.json`](../metadata/reward_item_evidence.json). Цей файл також містить native-моделі, джерела звичайної їжі та SHA-256 закріплених архівів. / Every manufactured item's exact active recipe IDs, serializers, native/authored sources and recipe data are indexed by item ID in [`reward_item_evidence.json`](../metadata/reward_item_evidence.json), alongside native models, ordinary-food sources and archive SHA-256 values.

### survival

Припаси для старту / Starting Supplies — **19** позицій / entries.

| Item ID | Українська назва / English name | × | Rarity | w | L | Виготовлення / Manufacture |
| --- | --- | ---: | --- | ---: | ---: | --- |
| `minecraft:bread` | Хліб / Bread | 8 | common | 10 | 0 | — |
| `minecraft:torch` | Смолоскип / Torch | 16 | common | 10 | 0 | — |
| `minecraft:oak_planks` | Дубові дошки / Oak Planks | 16 | common | 10 | 0 | — |
| `minecraft:leather` | Шкіра / Leather | 4 | common | 10 | 0 | — |
| `minecraft:paper` | Папір / Paper | 16 | common | 10 | 0 | — |
| `ecologics:crab_meat` | Крабове м’ясо / Crab Meat | 8 | common | 10 | 0 | — |
| `ecologics:coconut_slice` | Скибка кокоса / Coconut Slice | 8 | common | 10 | 0 | — |
| `ecologics:walnut` | Волоський горіх / Walnut | 8 | common | 10 | 0 | — |
| `twilightforest:cooked_venison` | Стейк з оленини / Venison Steak | 8 | common | 10 | 0 | — |
| `aether:blue_berry` | Синя ягода / Blue Berry | 8 | common | 10 | 0 | — |
| `ecologics:coconut_planks` | Кокосові дошки / Coconut Planks | 16 | common | 10 | 0 | Цей ID / Same ID |
| `ecologics:walnut_planks` | Горіхові дошки / Walnut Planks | 16 | common | 10 | 0 | Цей ID / Same ID |
| `exdeorum:crook` | Палиця з гаком / Crook | 1 | uncommon | 2 | 0 | Цей ID / Same ID |
| `exdeorum:wooden_hammer` | Дерев’яний молот / Wooden Hammer | 1 | uncommon | 2 | 0 | Цей ID / Same ID |
| `exdeorum:string_mesh` | Нитяна сітка / String Mesh | 1 | uncommon | 10 | 0 | Цей ID / Same ID |
| `immersiveengineering:hemp_fabric` | Міцна тканина / Tough Fabric | 1 | uncommon | 10 | 0 | Цей ID / Same ID |
| `sophisticatedbackpacks:backpack` | Наплічник / Backpack | 1 | rare | 8 | 0 | Цей ID / Same ID |
| `aether:cold_parachute` | Холодний парашут / Cold Parachute | 1 | rare | 8 | 0 | Цей ID / Same ID |
| `botania:travel_belt` | Пояс мандрівника / Sojourner's Sash | 1 | epic | 4 | 1 | Цей ID / Same ID |

### workshop

Запас майстерні / Workshop Supplies — **24** позицій / entries.

| Item ID | Українська назва / English name | × | Rarity | w | L | Виготовлення / Manufacture |
| --- | --- | ---: | --- | ---: | ---: | --- |
| `minecraft:redstone` | Редстоун / Redstone Dust | 16 | common | 10 | 0 | — |
| `minecraft:coal` | Вугілля / Coal | 8 | common | 10 | 0 | — |
| `minecraft:copper_ingot` | Мідний зливок / Copper Ingot | 8 | common | 10 | 0 | — |
| `minecraft:iron_ingot` | Залізний злиток / Iron Ingot | 6 | common | 10 | 0 | — |
| `minecraft:gold_ingot` | Золотий зливок / Gold Ingot | 4 | common | 10 | 0 | — |
| `immersiveengineering:plate_iron` | Залізна пластина / Iron Plate | 4 | uncommon | 14 | 0 | Цей ID / Same ID |
| `immersiveengineering:plate_copper` | Мідна пластина / Copper Plate | 4 | uncommon | 14 | 0 | Цей ID / Same ID |
| `immersiveengineering:plate_aluminum` | Алюмінієва пластина / Aluminium Plate | 4 | uncommon | 14 | 0 | Цей ID / Same ID |
| `immersiveengineering:plate_steel` | Сталева пластина / Steel Plate | 4 | uncommon | 14 | 0 | Цей ID / Same ID |
| `immersiveengineering:stick_steel` | Сталевий стрижень / Steel Rod | 4 | uncommon | 14 | 0 | Цей ID / Same ID |
| `immersiveengineering:wirecoil_copper` | Котушка дроту низької напруги / LV Wire Coil | 8 | uncommon | 14 | 0 | Цей ID / Same ID |
| `immersiveengineering:hemp_fabric` | Міцна тканина / Tough Fabric | 8 | common | 10 | 0 | Цей ID / Same ID |
| `immersiveengineering:treated_wood_horizontal` | Просочені дошки / Treated Wood Planks | 16 | common | 10 | 0 | Цей ID / Same ID |
| `immersiveengineering:component_iron` | Залізний механічний компонент / Iron Mechanical Component | 1 | rare | 12 | 0 | Цей ID / Same ID |
| `immersiveengineering:component_steel` | Сталевий механічний компонент / Steel Mechanical Component | 1 | rare | 8 | 1 | Цей ID / Same ID |
| `immersiveengineering:hammer` | Молот інженера / Engineer's Hammer | 1 | uncommon | 2 | 0 | Цей ID / Same ID |
| `immersiveengineering:screwdriver` | Викрутка інженера / Engineer's Screwdriver | 1 | uncommon | 2 | 0 | Цей ID / Same ID |
| `kubejs:industrial_chassis` | Промислове шасі / Industrial Chassis | 1 | rare | 12 | 1 | Цей ID / Same ID |
| `kubejs:electromechanical_drive` | Електромеханічний привід / Electromechanical Drive | 1 | rare | 12 | 1 | Цей ID / Same ID |
| `kubejs:sealed_process_chamber` | Герметична камера / Sealed Process Chamber | 1 | epic | 6 | 2 | Цей ID / Same ID |
| `mekanism:steel_casing` | Сталевий корпус / Steel Casing | 1 | rare | 8 | 1 | Цей ID / Same ID |
| `enderio:ensouled_chassis` | Одухотворене шасі / Ensouled Chassis | 1 | rare | 6 | 2 | Цей ID / Same ID |
| `ironfurnaces:augment_fuel` | Модуль ощадного палива / Augment: Fuel Efficiency | 1 | rare | 8 | 2 | Цей ID / Same ID |
| `ironfurnaces:augment_speed` | Модуль швидкості печі / Augment: Speed | 1 | epic | 8 | 2 | Цей ID / Same ID |

### engineering

Запас для виробництва / Factory Supplies — **24** позицій / entries.

| Item ID | Українська назва / English name | × | Rarity | w | L | Виготовлення / Manufacture |
| --- | --- | ---: | --- | ---: | ---: | --- |
| `minecraft:redstone` | Редстоун / Redstone Dust | 16 | common | 10 | 0 | — |
| `minecraft:copper_ingot` | Мідний зливок / Copper Ingot | 8 | common | 10 | 0 | — |
| `minecraft:iron_ingot` | Залізний злиток / Iron Ingot | 6 | common | 10 | 0 | — |
| `minecraft:coal` | Вугілля / Coal | 8 | common | 10 | 0 | — |
| `minecraft:glass` | Скло / Glass | 16 | common | 10 | 0 | — |
| `mekanism:basic_control_circuit` | Базова керувальна схема / Basic Control Circuit | 4 | uncommon | 16 | 0 | Цей ID / Same ID |
| `mekanism:advanced_control_circuit` | Поліпшена керувальна схема / Advanced Control Circuit | 1 | rare | 14 | 2 | Цей ID / Same ID |
| `mekanism:elite_control_circuit` | Елітна керувальна схема / Elite Control Circuit | 1 | rare | 8 | 3 | Цей ID / Same ID |
| `mekanism:ultimate_control_circuit` | Найвища керувальна схема / Ultimate Control Circuit | 1 | epic | 8 | 4 | Цей ID / Same ID |
| `mekanism:alloy_infused` | Збагачений сплав / Infused Alloy | 4 | uncommon | 10 | 0 | Цей ID / Same ID |
| `mekanism:alloy_reinforced` | Зміцнений сплав / Reinforced Alloy | 1 | rare | 10 | 2 | Цей ID / Same ID |
| `mekanism:alloy_atomic` | Атомний сплав / Atomic Alloy | 1 | epic | 10 | 3 | Цей ID / Same ID |
| `ae2:logic_processor` | Логічний процесор / Logic Processor | 1 | rare | 14 | 1 | Цей ID / Same ID |
| `ae2:calculation_processor` | Обчислювальний процесор / Calculation Processor | 1 | rare | 14 | 1 | Цей ID / Same ID |
| `ae2:engineering_processor` | Інженерний процесор / Engineering Processor | 1 | rare | 14 | 1 | Цей ID / Same ID |
| `enderio:basic_capacitor` | Базовий конденсатор / Basic Capacitor | 4 | uncommon | 10 | 1 | Цей ID / Same ID |
| `enderio:double_layer_capacitor` | Двошаровий конденсатор / Double-Layer Capacitor | 1 | rare | 10 | 2 | Цей ID / Same ID |
| `enderio:octadic_capacitor` | Восьмишаровий конденсатор / Octadic Capacitor | 1 | epic | 10 | 3 | Цей ID / Same ID |
| `buildcraftcore:gears/gear_iron` | Залізна шестерня / Iron Gear | 1 | uncommon | 12 | 0 | Цей ID / Same ID |
| `buildcraftcore:gears/gear_gold` | Золота шестерня / Gold Gear | 1 | rare | 8 | 1 | Цей ID / Same ID |
| `kubejs:industrial_chassis` | Промислове шасі / Industrial Chassis | 1 | rare | 10 | 1 | Цей ID / Same ID |
| `kubejs:electromechanical_drive` | Електромеханічний привід / Electromechanical Drive | 1 | rare | 12 | 1 | Цей ID / Same ID |
| `kubejs:precision_mechanism` | Точний механізм / Precision Mechanism | 1 | epic | 8 | 2 | Цей ID / Same ID |
| `kubejs:control_assembly` | Блок керування / Control Assembly | 1 | epic | 10 | 2 | Цей ID / Same ID |

### power

Запас енергетика / Power Workshop Supplies — **24** позицій / entries.

| Item ID | Українська назва / English name | × | Rarity | w | L | Виготовлення / Manufacture |
| --- | --- | ---: | --- | ---: | ---: | --- |
| `minecraft:coal` | Вугілля / Coal | 8 | common | 10 | 0 | — |
| `minecraft:redstone` | Редстоун / Redstone Dust | 16 | common | 10 | 0 | — |
| `minecraft:copper_ingot` | Мідний зливок / Copper Ingot | 8 | common | 10 | 0 | — |
| `minecraft:iron_ingot` | Залізний злиток / Iron Ingot | 6 | common | 10 | 0 | — |
| `minecraft:glass` | Скло / Glass | 16 | common | 10 | 0 | — |
| `immersiveengineering:wirecoil_copper` | Котушка дроту низької напруги / LV Wire Coil | 8 | uncommon | 14 | 0 | Цей ID / Same ID |
| `immersiveengineering:wirecoil_electrum` | Котушка дроту середньої напруги / MV Wire Coil | 8 | uncommon | 14 | 1 | Цей ID / Same ID |
| `immersiveengineering:wirecoil_steel` | Котушка дроту високої напруги / HV Wire Coil | 8 | uncommon | 14 | 2 | Цей ID / Same ID |
| `immersiveengineering:connector_lv` | Роз’єм низької напруги / LV Wire Connector | 4 | uncommon | 12 | 0 | Цей ID / Same ID |
| `immersiveengineering:capacitor_lv` | Накопичувач низької напруги / LV Accumulator | 1 | rare | 8 | 1 | Цей ID / Same ID |
| `immersiveengineering:connector_mv` | Роз’єм середньої напруги / MV Wire Connector | 4 | uncommon | 12 | 1 | Цей ID / Same ID |
| `immersiveengineering:capacitor_mv` | Накопичувач середньої напруги / MV Accumulator | 1 | rare | 8 | 1 | Цей ID / Same ID |
| `immersiveengineering:connector_hv` | Роз’єм високої напруги / HV Wire Connector | 4 | uncommon | 12 | 2 | Цей ID / Same ID |
| `immersiveengineering:capacitor_hv` | Накопичувач високої напруги / HV Accumulator | 1 | epic | 8 | 2 | Цей ID / Same ID |
| `mekanism:energy_tablet` | Енергетична пластина / Energy Tablet | 1 | uncommon | 14 | 1 | Цей ID / Same ID |
| `mekanism:basic_energy_cube` | Базовий енергетичний куб / Basic Energy Cube | 1 | rare | 6 | 1 | Цей ID / Same ID |
| `mekanism:advanced_energy_cube` | Поліпшений енергетичний куб / Advanced Energy Cube | 1 | rare | 6 | 2 | Цей ID / Same ID |
| `mekanism:elite_energy_cube` | Елітний енергетичний куб / Elite Energy Cube | 1 | epic | 6 | 3 | Цей ID / Same ID |
| `mekanism:ultimate_energy_cube` | Найвищий енергетичний куб / Ultimate Energy Cube | 1 | epic | 6 | 4 | Цей ID / Same ID |
| `mekanism:basic_universal_cable` | Базовий універсальний кабель / Basic Universal Cable | 8 | uncommon | 12 | 0 | Цей ID / Same ID |
| `mekanism:advanced_universal_cable` | Поліпшений універсальний кабель / Advanced Universal Cable | 8 | uncommon | 12 | 2 | Цей ID / Same ID |
| `mekanism:elite_universal_cable` | Елітний універсальний кабель / Elite Universal Cable | 8 | uncommon | 12 | 3 | Цей ID / Same ID |
| `enderio:double_layer_capacitor` | Двошаровий конденсатор / Double-Layer Capacitor | 1 | rare | 10 | 2 | Цей ID / Same ID |
| `enderio:octadic_capacitor` | Восьмишаровий конденсатор / Octadic Capacitor | 1 | epic | 8 | 3 | Цей ID / Same ID |

### logistics

Припаси для складу / Storage Supplies — **24** позицій / entries.

| Item ID | Українська назва / English name | × | Rarity | w | L | Виготовлення / Manufacture |
| --- | --- | ---: | --- | ---: | ---: | --- |
| `minecraft:paper` | Папір / Paper | 16 | common | 10 | 0 | — |
| `minecraft:glass` | Скло / Glass | 16 | common | 10 | 0 | — |
| `minecraft:string` | Нитка / String | 8 | common | 10 | 0 | — |
| `minecraft:iron_ingot` | Залізний злиток / Iron Ingot | 6 | common | 10 | 0 | — |
| `minecraft:redstone` | Редстоун / Redstone Dust | 16 | common | 10 | 0 | — |
| `sophisticatedstorage:barrel` | Дерев’яна бочка / Barrel | 1 | uncommon | 14 | 0 | Цей ID / Same ID |
| `sophisticatedstorage:copper_barrel` | Мідна бочка / Copper Barrel | 1 | uncommon | 10 | 0 | Цей ID / Same ID |
| `sophisticatedstorage:iron_barrel` | Залізна бочка / Iron Barrel | 1 | uncommon | 8 | 0 | Цей ID / Same ID |
| `sophisticatedstorage:gold_barrel` | Золота бочка / Gold Barrel | 1 | rare | 10 | 2 | Цей ID / Same ID |
| `sophisticatedstorage:diamond_barrel` | Діамантова бочка / Diamond Barrel | 1 | rare | 6 | 3 | Цей ID / Same ID |
| `sophisticatedstorage:netherite_barrel` | Незеритова бочка / Netherite Barrel | 1 | epic | 4 | 4 | Цей ID / Same ID |
| `ae2:logic_processor` | Логічний процесор / Logic Processor | 1 | rare | 12 | 1 | Цей ID / Same ID |
| `ae2:calculation_processor` | Обчислювальний процесор / Calculation Processor | 1 | rare | 12 | 1 | Цей ID / Same ID |
| `ae2:engineering_processor` | Інженерний процесор / Engineering Processor | 1 | rare | 12 | 1 | Цей ID / Same ID |
| `ae2:fluix_glass_cable` | Скляний флюїксовий ME-кабель / Fluix ME Glass Cable | 8 | uncommon | 14 | 0 | Цей ID / Same ID |
| `ae2:quartz_fiber` | Кварцове волокно / Quartz Fiber | 8 | uncommon | 14 | 0 | Цей ID / Same ID |
| `ae2:cell_component_1k` | Компонент ME-комірки на 1k / 1k ME Storage Component | 1 | rare | 10 | 1 | Цей ID / Same ID |
| `sophisticatedstorage:filter_upgrade` | Поліпшення фільтра / Filter Upgrade | 1 | uncommon | 10 | 0 | Цей ID / Same ID |
| `sophisticatedstorage:hopper_upgrade` | Поліпшення лійки / Hopper Upgrade | 1 | rare | 10 | 1 | Цей ID / Same ID |
| `sophisticatedstorage:stack_upgrade_tier_1` | Поліпшення стосів, рівень 1 / Stack Upgrade Tier 1 | 1 | uncommon | 8 | 0 | Цей ID / Same ID |
| `sophisticatedstorage:stack_upgrade_tier_2` | Поліпшення стосів, рівень 2 / Stack Upgrade Tier 2 | 1 | rare | 10 | 1 | Цей ID / Same ID |
| `sophisticatedstorage:stack_upgrade_tier_3` | Поліпшення стосів, рівень 3 / Stack Upgrade Tier 3 | 1 | rare | 8 | 2 | Цей ID / Same ID |
| `sophisticatedstorage:stack_upgrade_tier_4` | Поліпшення стосів, рівень 4 / Stack Upgrade Tier 4 | 1 | epic | 8 | 3 | Цей ID / Same ID |
| `sophisticatedstorage:stack_upgrade_tier_5` | Поліпшення стосів, рівень 5 / Stack Upgrade Tier 5 | 1 | epic | 4 | 4 | Цей ID / Same ID |

### scholar

Матеріали дослідника / Research Supplies — **20** позицій / entries.

| Item ID | Українська назва / English name | × | Rarity | w | L | Виготовлення / Manufacture |
| --- | --- | ---: | --- | ---: | ---: | --- |
| `minecraft:paper` | Папір / Paper | 16 | common | 10 | 0 | — |
| `minecraft:lapis_lazuli` | Лазурит / Lapis Lazuli | 12 | common | 10 | 0 | — |
| `minecraft:glass` | Скло / Glass | 16 | common | 10 | 0 | — |
| `minecraft:amethyst_shard` | Уламок аметисту / Amethyst Shard | 8 | common | 10 | 0 | — |
| `minecraft:sugar` | Цукор / Sugar | 8 | common | 10 | 0 | — |
| `ae2:certus_quartz_cutting_knife` | Ніж із сертус-кварцу / Certus Quartz Cutting Knife | 1 | uncommon | 2 | 0 | Цей ID / Same ID |
| `ae2:certus_quartz_wrench` | Ключ із сертус-кварцу / Certus Quartz Wrench | 1 | uncommon | 2 | 0 | Цей ID / Same ID |
| `ae2:memory_card` | Картка пам’яті / Memory Card | 1 | uncommon | 8 | 0 | Цей ID / Same ID |
| `ae2:basic_card` | Базова картка / Basic Card | 1 | uncommon | 14 | 1 | Цей ID / Same ID |
| `ae2:advanced_card` | Поліпшена картка / Advanced Card | 1 | rare | 12 | 1 | Цей ID / Same ID |
| `ae2:capacity_card` | Картка місткості / Capacity Card | 1 | uncommon | 12 | 1 | Цей ID / Same ID |
| `ae2:crafting_card` | Картка виготовлення / Crafting Card | 1 | rare | 12 | 1 | Цей ID / Same ID |
| `ae2:wireless_booster` | Підсилювач бездротового зв’язку / Wireless Booster | 1 | uncommon | 10 | 1 | Цей ID / Same ID |
| `ae2:calculation_processor` | Обчислювальний процесор / Calculation Processor | 1 | rare | 14 | 1 | Цей ID / Same ID |
| `apothic_enchanting:basic_bookshelf` | Базова книжкова полиця / Basic Bookshelf | 1 | uncommon | 12 | 0 | Цей ID / Same ID |
| `apothic_enchanting:geode_shelf` | Книжкова полиця стабільності з геодою / Geode-Encased Bookshelf of Stability | 1 | rare | 8 | 1 | Цей ID / Same ID |
| `apothic_enchanting:hellshelf` | Пекельна книжкова полиця / Hellshelf | 1 | rare | 8 | 1 | Цей ID / Same ID |
| `apotheosis:gem_case` | Футляр для самоцвітів / Gem Case | 1 | rare | 6 | 0 | Цей ID / Same ID |
| `rftoolsbase:information_screen` | Інформаційний екран / Information Screen | 1 | uncommon | 8 | 1 | Цей ID / Same ID |
| `kubejs:quantum_processor` | Квантовий процесор / Quantum Processor | 1 | epic | 6 | 3 | Цей ID / Same ID |

### arcane

Запас для магічної майстерні / Arcane Workshop Supplies — **24** позицій / entries.

| Item ID | Українська назва / English name | × | Rarity | w | L | Виготовлення / Manufacture |
| --- | --- | ---: | --- | ---: | ---: | --- |
| `minecraft:lapis_lazuli` | Лазурит / Lapis Lazuli | 12 | common | 10 | 0 | — |
| `minecraft:amethyst_shard` | Уламок аметисту / Amethyst Shard | 8 | common | 10 | 0 | — |
| `minecraft:glass` | Скло / Glass | 16 | common | 10 | 0 | — |
| `minecraft:paper` | Папір / Paper | 16 | common | 10 | 0 | — |
| `minecraft:experience_bottle` | Пляшечка досвіду / Bottle o’ Enchanting | 6 | common | 10 | 0 | — |
| `botania:white_petal` | Біла містична пелюстка / Mystical White Petal | 8 | common | 10 | 0 | Цей ID / Same ID |
| `botania:purple_petal` | Фіолетова містична пелюстка / Mystical Purple Petal | 8 | common | 10 | 0 | Цей ID / Same ID |
| `botania:light_blue_petal` | Блакитна містична пелюстка / Mystical Light Blue Petal | 8 | common | 10 | 0 | Цей ID / Same ID |
| `botania:pink_petal` | Рожева містична пелюстка / Mystical Pink Petal | 8 | common | 10 | 0 | Цей ID / Same ID |
| `botania:vine_ball` | Куля з ліан / Vine Ball | 1 | uncommon | 14 | 0 | Цей ID / Same ID |
| `botania:lexicon` | Лексика Botania / Lexica Botania | 1 | uncommon | 2 | 0 | Цей ID / Same ID |
| `botania:incense_plate` | Підставка для пахощів / Incense Plate | 1 | uncommon | 6 | 0 | Цей ID / Same ID |
| `botania:incense_stick` | Паличка пахощів / Incense Stick | 1 | uncommon | 12 | 1 | Цей ID / Same ID |
| `botania:lens_normal` | Лінза мани / Mana Lens | 1 | uncommon | 14 | 1 | Цей ID / Same ID |
| `botania:lens_magnet` | Магнітна лінза / Magnetizing Lens | 1 | rare | 10 | 2 | Цей ID / Same ID |
| `botania:mana_tablet` | Планшет мани / Mana Tablet | 1 | rare | 10 | 1 | Цей ID / Same ID |
| `botania:monocle` | Монокль провидця мани / Manaseer Monocle | 1 | uncommon | 6 | 1 | Цей ID / Same ID |
| `evilcraft:blood_orb_empty` | Порожня кров’яна сфера / Empty Orb | 1 | uncommon | 12 | 0 | Цей ID / Same ID |
| `evilcraft:gem_stone_torch` | Самоцвітний смолоскип / Gemstone Torch | 1 | uncommon | 12 | 0 | Цей ID / Same ID |
| `evilcraft:bowl_of_promises_empty` | Чаша порожніх обіцянок / Bowl of Empty Promises | 1 | uncommon | 8 | 0 | Цей ID / Same ID |
| `evilcraft:blood_infusion_core` | Ядро насичення кров’ю / Blood Infusion Core | 1 | rare | 8 | 2 | Цей ID / Same ID |
| `evilcraft:blood_pearl_of_teleportation` | Кров’яна перлина телепортації / Blood Pearl of Teleportation | 1 | rare | 6 | 2 | Цей ID / Same ID |
| `kubejs:arcane_conduit` | Арканний провідник / Arcane Conduit | 1 | rare | 12 | 2 | Цей ID / Same ID |
| `kubejs:dimensional_matrix` | Вимірна матриця / Dimensional Matrix | 1 | epic | 6 | 3 | Цей ID / Same ID |

### expedition

Припаси для експедиції / Expedition Supplies — **22** позицій / entries.

| Item ID | Українська назва / English name | × | Rarity | w | L | Виготовлення / Manufacture |
| --- | --- | ---: | --- | ---: | ---: | --- |
| `minecraft:bread` | Хліб / Bread | 8 | common | 10 | 0 | — |
| `minecraft:torch` | Смолоскип / Torch | 16 | common | 10 | 0 | — |
| `minecraft:arrow` | Стріла / Arrow | 16 | common | 10 | 0 | — |
| `minecraft:leather` | Шкіра / Leather | 4 | common | 10 | 0 | — |
| `minecraft:white_wool` | Біла вовна / White Wool | 8 | common | 10 | 0 | — |
| `ecologics:crab_meat` | Крабове м’ясо / Crab Meat | 8 | common | 10 | 0 | — |
| `twilightforest:cooked_venison` | Стейк з оленини / Venison Steak | 8 | common | 10 | 0 | — |
| `twilightforest:cooked_meef` | Стейк із мінотавра / Meef Steak | 8 | common | 10 | 0 | — |
| `aether:blue_berry` | Синя ягода / Blue Berry | 8 | common | 10 | 0 | — |
| `ecologics:coconut_slice` | Скибка кокоса / Coconut Slice | 8 | common | 10 | 0 | — |
| `waystones:blank_scroll` | Порожній сувій / Blank Scroll | 1 | uncommon | 14 | 0 | Цей ID / Same ID |
| `waystones:warp_dust` | Пил переміщення / Warp Dust | 1 | uncommon | 14 | 0 | Цей ID / Same ID |
| `waystones:return_scroll` | Сувій повернення / Return Scroll | 1 | uncommon | 12 | 0 | Цей ID / Same ID |
| `waystones:warp_scroll` | Сувій переміщення / Warp Scroll | 1 | rare | 10 | 1 | Цей ID / Same ID |
| `waystones:portal_scroll` | Портальний сувій / Portal Scroll | 1 | rare | 8 | 2 | Цей ID / Same ID |
| `aether:ambrosium_torch` | Амброзієвий смолоскип / Ambrosium Torch | 16 | common | 10 | 0 | Цей ID / Same ID |
| `aether:cold_parachute` | Холодний парашут / Cold Parachute | 1 | rare | 8 | 0 | Цей ID / Same ID |
| `aether:golden_parachute` | Золотий парашут / Golden Parachute | 1 | rare | 6 | 1 | Цей ID / Same ID |
| `sophisticatedbackpacks:backpack` | Наплічник / Backpack | 1 | rare | 8 | 0 | Цей ID / Same ID |
| `sophisticatedbackpacks:feeding_upgrade` | Поліпшення годування / Feeding Upgrade | 1 | rare | 10 | 1 | Цей ID / Same ID |
| `botania:travel_belt` | Пояс мандрівника / Sojourner's Sash | 1 | epic | 6 | 1 | Цей ID / Same ID |
| `ecologics:coconut_boat` | Кокосовий човен / Coconut Boat | 1 | uncommon | 6 | 0 | Цей ID / Same ID |

### settlement

Посилка для будівельника / Settlement Supplies — **23** позицій / entries.

| Item ID | Українська назва / English name | × | Rarity | w | L | Виготовлення / Manufacture |
| --- | --- | ---: | --- | ---: | ---: | --- |
| `minecraft:brick` | Цеглина / Brick | 16 | common | 10 | 0 | — |
| `minecraft:oak_planks` | Дубові дошки / Oak Planks | 16 | common | 10 | 0 | — |
| `minecraft:white_wool` | Біла вовна / White Wool | 8 | common | 10 | 0 | — |
| `minecraft:paper` | Папір / Paper | 16 | common | 10 | 0 | — |
| `minecraft:bread` | Хліб / Bread | 8 | common | 10 | 0 | — |
| `ecologics:coconut_planks` | Кокосові дошки / Coconut Planks | 16 | common | 10 | 0 | Цей ID / Same ID |
| `ecologics:walnut_planks` | Горіхові дошки / Walnut Planks | 16 | common | 10 | 0 | Цей ID / Same ID |
| `ecologics:azalea_planks` | Азалієві дошки / Azalea Planks | 16 | common | 10 | 0 | Цей ID / Same ID |
| `millenaire:byzantine_stone_tiles` | Візантійська кам’яна плитка / Byzantine Stone Tiles | 16 | common | 10 | 0 | Цей ID / Same ID |
| `millenaire:byzantine_sandstone_tiles` | Візантійська пісковикова плитка / Byzantine Sandstone Tiles | 16 | common | 10 | 0 | Цей ID / Same ID |
| `millenaire:ice_brick` | Крижана цегла / Ice Brick | 16 | common | 10 | 0 | Цей ID / Same ID |
| `millenaire:cider` | Сидр / Cider | 1 | uncommon | 12 | 0 | Цей ID / Same ID |
| `immersiveengineering:treated_wood_horizontal` | Просочені дошки / Treated Wood Planks | 16 | common | 10 | 0 | Цей ID / Same ID |
| `immersiveengineering:cushion` | Подушка для приземлення / Jump Cushion | 1 | uncommon | 8 | 0 | Цей ID / Same ID |
| `immersiveengineering:lantern` | Інженерський ліхтар / Lantern | 1 | uncommon | 12 | 0 | Цей ID / Same ID |
| `immersiveengineering:crate` | Дерев’яний ящик / Wooden Storage Crate | 1 | uncommon | 10 | 0 | Цей ID / Same ID |
| `immersiveengineering:reinforced_crate` | Зміцнений ящик / Reinforced Storage Crate | 1 | rare | 8 | 1 | Цей ID / Same ID |
| `sophisticatedstorage:barrel` | Дерев’яна бочка / Barrel | 1 | uncommon | 8 | 0 | Цей ID / Same ID |
| `sophisticatedstorage:copper_barrel` | Мідна бочка / Copper Barrel | 1 | uncommon | 8 | 0 | Цей ID / Same ID |
| `sophisticatedstorage:iron_barrel` | Залізна бочка / Iron Barrel | 1 | rare | 8 | 0 | Цей ID / Same ID |
| `waystones:blank_scroll` | Порожній сувій / Blank Scroll | 1 | uncommon | 10 | 0 | Цей ID / Same ID |
| `rftoolsbase:information_screen` | Інформаційний екран / Information Screen | 1 | rare | 8 | 1 | Цей ID / Same ID |
| `sophisticatedstorage:hopper_upgrade` | Поліпшення лійки / Hopper Upgrade | 1 | epic | 6 | 1 | Цей ID / Same ID |

### space

Запас космічної майстерні / Space Workshop Supplies — **24** позицій / entries.

| Item ID | Українська назва / English name | × | Rarity | w | L | Виготовлення / Manufacture |
| --- | --- | ---: | --- | ---: | ---: | --- |
| `minecraft:glass` | Скло / Glass | 16 | common | 10 | 0 | — |
| `minecraft:redstone` | Редстоун / Redstone Dust | 16 | common | 10 | 0 | — |
| `minecraft:iron_ingot` | Залізний злиток / Iron Ingot | 6 | common | 10 | 0 | — |
| `minecraft:golden_carrot` | Золота морква / Golden Carrot | 4 | common | 10 | 0 | — |
| `minecraft:experience_bottle` | Пляшечка досвіду / Bottle o’ Enchanting | 6 | common | 10 | 0 | — |
| `ad_astra:iron_rod` | Залізний стрижень / Iron Rod | 4 | uncommon | 14 | 0 | Цей ID / Same ID |
| `ad_astra:steel_rod` | Сталевий стрижень / Steel Rod | 4 | uncommon | 14 | 1 | Цей ID / Same ID |
| `immersiveengineering:plate_iron` | Залізна пластина / Iron Plate | 4 | uncommon | 14 | 0 | Цей ID / Same ID |
| `immersiveengineering:plate_steel` | Сталева пластина / Steel Plate | 4 | uncommon | 14 | 1 | Цей ID / Same ID |
| `ad_astra:fan` | Вентилятор двигуна / Fan | 1 | uncommon | 12 | 1 | Цей ID / Same ID |
| `ad_astra:engine_frame` | Рама двигуна / Engine Frame | 1 | rare | 12 | 2 | Цей ID / Same ID |
| `ad_astra:oxygen_gear` | Кисневий комплект / Oxygen Gear | 1 | rare | 8 | 2 | Цей ID / Same ID |
| `ad_astra:oxygen_sensor` | Кисневий датчик / Oxygen Sensor | 1 | uncommon | 8 | 2 | Цей ID / Same ID |
| `ad_astra:steel_cable` | Сталевий кабель / Steel Cable | 4 | uncommon | 14 | 1 | Цей ID / Same ID |
| `ad_astra:desh_cable` | Кабель із дешу / Desh Cable | 4 | uncommon | 10 | 3 | Цей ID / Same ID |
| `ad_astra:desh_plate` | Пластина з дешу / Desh Plate | 1 | rare | 12 | 3 | Цей ID / Same ID |
| `ad_astra:ostrum_plate` | Острумова пластина / Ostrum Plate | 1 | rare | 12 | 4 | Цей ID / Same ID |
| `ad_astra:calorite_plate` | Калоритова пластина / Calorite Plate | 1 | rare | 12 | 4 | Цей ID / Same ID |
| `kubejs:propulsion_turbine` | Реактивна турбіна / Propulsion Turbine | 1 | rare | 10 | 2 | Цей ID / Same ID |
| `kubejs:stellar_alloy` | Зоряний композит / Stellar Alloy | 1 | epic | 8 | 4 | Цей ID / Same ID |
| `kubejs:radiation_shield` | Радіаційний захист / Radiation Shield | 1 | rare | 8 | 4 | Цей ID / Same ID |
| `kubejs:reinforced_netherite_plate` | Посилена незеритова пластина / Reinforced Netherite Plate | 1 | epic | 6 | 4 | Цей ID / Same ID |
| `ad_astra:coal_generator` | Вугільний генератор / Coal Generator | 1 | rare | 6 | 1 | Цей ID / Same ID |
| `ad_astra:cheese` | Сир / Cheese | 8 | common | 10 | 0 | — |

### processing

Сировина для переробки / Processing Supplies — **24** позицій / entries.

| Item ID | Українська назва / English name | × | Rarity | w | L | Виготовлення / Manufacture |
| --- | --- | ---: | --- | ---: | ---: | --- |
| `minecraft:sand` | Пісок / Sand | 16 | common | 10 | 0 | — |
| `minecraft:gravel` | Гравій / Gravel | 16 | common | 10 | 0 | — |
| `minecraft:cobblestone` | Бруківка / Cobblestone | 16 | common | 10 | 0 | — |
| `minecraft:clay_ball` | Грудка глини / Clay Ball | 16 | common | 10 | 0 | — |
| `minecraft:coal` | Вугілля / Coal | 8 | common | 10 | 0 | — |
| `exdeorum:crook` | Палиця з гаком / Crook | 1 | uncommon | 2 | 0 | Цей ID / Same ID |
| `exdeorum:bone_crook` | Кістяна палиця з гаком / Bone Crook | 1 | uncommon | 2 | 0 | Цей ID / Same ID |
| `exdeorum:wooden_hammer` | Дерев’яний молот / Wooden Hammer | 1 | uncommon | 2 | 0 | Цей ID / Same ID |
| `exdeorum:iron_hammer` | Залізний молот / Iron Hammer | 1 | uncommon | 2 | 0 | Цей ID / Same ID |
| `exdeorum:diamond_hammer` | Діамантовий молот / Diamond Hammer | 1 | rare | 2 | 1 | Цей ID / Same ID |
| `exdeorum:string_mesh` | Нитяна сітка / String Mesh | 1 | uncommon | 12 | 0 | Цей ID / Same ID |
| `exdeorum:flint_mesh` | Кременева сітка / Flint Mesh | 1 | uncommon | 12 | 0 | Цей ID / Same ID |
| `exdeorum:iron_mesh` | Залізна сітка / Iron Mesh | 1 | uncommon | 10 | 0 | Цей ID / Same ID |
| `exdeorum:golden_mesh` | Золота сітка / Golden Mesh | 1 | rare | 10 | 1 | Цей ID / Same ID |
| `exdeorum:diamond_mesh` | Діамантова сітка / Diamond Mesh | 1 | rare | 8 | 2 | Цей ID / Same ID |
| `exmachinis:comparator_upgrade` | Поліпшення компаратора / Comparator Upgrade | 1 | uncommon | 10 | 0 | Цей ID / Same ID |
| `exmachinis:gold_upgrade` | Золоте поліпшення / Gold Upgrade | 1 | uncommon | 10 | 0 | Цей ID / Same ID |
| `exmachinis:diamond_upgrade` | Діамантове поліпшення / Diamond Upgrade | 1 | rare | 8 | 2 | Цей ID / Same ID |
| `exmachinis:netherite_upgrade` | Незеритове поліпшення / Netherite Upgrade | 1 | epic | 6 | 4 | Цей ID / Same ID |
| `exmachinis:item_buffer` | Буфер предметів / Item Buffer | 1 | uncommon | 10 | 1 | Цей ID / Same ID |
| `exmachinis:flux_hammer` | Енергетичний молот / Flux Hammer | 1 | rare | 2 | 2 | Цей ID / Same ID |
| `exmachinis:flux_sieve` | Енергетичне сито / Flux Sieve | 1 | epic | 6 | 2 | Цей ID / Same ID |
| `ironfurnaces:augment_blasting` | Модуль плавлення руд / Augment: Blasting | 1 | uncommon | 10 | 0 | Цей ID / Same ID |
| `ironfurnaces:augment_smoking` | Модуль копчення / Augment: Smoking | 1 | uncommon | 10 | 0 | Цей ID / Same ID |

### hunting

Припаси мисливця / Hunting Supplies — **24** позицій / entries.

| Item ID | Українська назва / English name | × | Rarity | w | L | Виготовлення / Manufacture |
| --- | --- | ---: | --- | ---: | ---: | --- |
| `minecraft:arrow` | Стріла / Arrow | 16 | common | 10 | 0 | — |
| `minecraft:string` | Нитка / String | 8 | common | 10 | 0 | — |
| `minecraft:bone` | Кістка / Bone | 8 | common | 10 | 0 | — |
| `minecraft:cooked_beef` | Стейк / Steak | 8 | common | 10 | 0 | — |
| `minecraft:leather` | Шкіра / Leather | 4 | common | 10 | 0 | — |
| `ecologics:crab_meat` | Крабове м’ясо / Crab Meat | 8 | common | 10 | 0 | — |
| `twilightforest:cooked_venison` | Стейк з оленини / Venison Steak | 8 | common | 10 | 0 | — |
| `twilightforest:cooked_meef` | Стейк із мінотавра / Meef Steak | 8 | common | 10 | 0 | — |
| `mob_grinding_utils:mob_swab` | Мазок моба / Mob Swab | 1 | uncommon | 12 | 0 | Цей ID / Same ID |
| `mob_grinding_utils:absorption_upgrade` | Поліпшення поглинання / Absorption Radius Upgrade | 1 | uncommon | 12 | 0 | Цей ID / Same ID |
| `mob_grinding_utils:fan_upgrade_speed` | Поліпшення швидкості вентилятора / Mob Fan Distance Upgrade | 1 | uncommon | 12 | 0 | Цей ID / Same ID |
| `mob_grinding_utils:fan_upgrade_width` | Поліпшення ширини вентилятора / Mob Fan Width Upgrade | 1 | uncommon | 12 | 0 | Цей ID / Same ID |
| `mob_grinding_utils:fan_upgrade_height` | Поліпшення висоти вентилятора / Mob Fan Height Upgrade | 1 | uncommon | 12 | 0 | Цей ID / Same ID |
| `mob_grinding_utils:xp_tap` | Кран досвіду / XP Tap | 1 | uncommon | 8 | 0 | Цей ID / Same ID |
| `mob_grinding_utils:spikes` | Шипи / Iron Spikes | 1 | uncommon | 10 | 0 | Цей ID / Same ID |
| `mob_grinding_utils:tinted_glass` | Тоноване скло ферми мобів / Tinted Glass | 1 | uncommon | 12 | 0 | Цей ID / Same ID |
| `mob_grinding_utils:saw_upgrade_sharpness` | Поліпшення гостроти дробарки / Mob Masher Upgrade Sharpness | 1 | uncommon | 12 | 1 | Цей ID / Same ID |
| `mob_grinding_utils:saw_upgrade_beheading` | Поліпшення обезголовлення / Mob Masher Upgrade Beheading | 1 | rare | 10 | 1 | Цей ID / Same ID |
| `mob_grinding_utils:saw_upgrade_looting` | Поліпшення здобичі / Mob Masher Upgrade Looting | 1 | rare | 10 | 1 | Цей ID / Same ID |
| `mob_grinding_utils:fan` | Вентилятор ферми мобів / Mob Fan | 1 | rare | 8 | 1 | Цей ID / Same ID |
| `mob_grinding_utils:absorption_hopper` | Поглинальна лійка / Absorption Hopper | 1 | rare | 8 | 1 | Цей ID / Same ID |
| `mob_grinding_utils:xpsolidifier` | Затверджувач досвіду / XP Solidifier | 1 | rare | 6 | 2 | Цей ID / Same ID |
| `sophisticatedstorage:pickup_upgrade` | Поліпшення підбирання / Pickup Upgrade | 1 | uncommon | 10 | 0 | Цей ID / Same ID |
| `sophisticatedstorage:advanced_pickup_upgrade` | Поліпшене підбирання / Advanced Pickup Upgrade | 1 | epic | 6 | 2 | Цей ID / Same ID |

### equipment

Запас для спорядження / Equipment Supplies — **24** позицій / entries.

| Item ID | Українська назва / English name | × | Rarity | w | L | Виготовлення / Manufacture |
| --- | --- | ---: | --- | ---: | ---: | --- |
| `minecraft:arrow` | Стріла / Arrow | 16 | common | 10 | 0 | — |
| `minecraft:leather` | Шкіра / Leather | 4 | common | 10 | 0 | — |
| `minecraft:string` | Нитка / String | 8 | common | 10 | 0 | — |
| `minecraft:lapis_lazuli` | Лазурит / Lapis Lazuli | 12 | common | 10 | 0 | — |
| `minecraft:iron_ingot` | Залізний злиток / Iron Ingot | 6 | common | 10 | 0 | — |
| `sophisticatedbackpacks:backpack` | Наплічник / Backpack | 1 | uncommon | 10 | 0 | Цей ID / Same ID |
| `sophisticatedbackpacks:copper_backpack` | Мідний наплічник / Copper Backpack | 1 | uncommon | 8 | 0 | Цей ID / Same ID |
| `sophisticatedbackpacks:iron_backpack` | Залізний наплічник / Iron Backpack | 1 | rare | 10 | 0 | Цей ID / Same ID |
| `sophisticatedbackpacks:gold_backpack` | Золотий наплічник / Gold Backpack | 1 | rare | 8 | 2 | Цей ID / Same ID |
| `sophisticatedbackpacks:diamond_backpack` | Діамантовий наплічник / Diamond Backpack | 1 | epic | 6 | 3 | Цей ID / Same ID |
| `sophisticatedbackpacks:pickup_upgrade` | Поліпшення підбирання / Pickup Upgrade | 1 | uncommon | 12 | 0 | Цей ID / Same ID |
| `sophisticatedbackpacks:filter_upgrade` | Поліпшення фільтра / Filter Upgrade | 1 | uncommon | 12 | 0 | Цей ID / Same ID |
| `sophisticatedbackpacks:feeding_upgrade` | Поліпшення годування / Feeding Upgrade | 1 | uncommon | 12 | 1 | Цей ID / Same ID |
| `sophisticatedbackpacks:magnet_upgrade` | Поліпшення магніту / Magnet Upgrade | 1 | rare | 10 | 1 | Цей ID / Same ID |
| `sophisticatedbackpacks:advanced_magnet_upgrade` | Поліпшений магніт / Advanced Magnet Upgrade | 1 | epic | 8 | 2 | Цей ID / Same ID |
| `sophisticatedbackpacks:advanced_pickup_upgrade` | Поліпшене підбирання / Advanced Pickup Upgrade | 1 | rare | 8 | 2 | Цей ID / Same ID |
| `mekanismtools:steel_pickaxe` | Сталева кирка / Steel Pickaxe | 1 | uncommon | 8 | 1 | Цей ID / Same ID |
| `mekanismtools:steel_shield` | Сталевий щит / Steel Shield | 1 | rare | 8 | 1 | Цей ID / Same ID |
| `botania:travel_belt` | Пояс мандрівника / Sojourner's Sash | 1 | rare | 8 | 1 | Цей ID / Same ID |
| `botania:magnet_ring` | Перстень намагнічування / Ring of Magnetization | 1 | rare | 8 | 2 | Цей ID / Same ID |
| `botania:super_travel_belt` | Пояс навколосвітнього мандрівника / Globetrotter's Sash | 1 | epic | 6 | 3 | Цей ID / Same ID |
| `apotheosis:gem_case` | Футляр для самоцвітів / Gem Case | 1 | uncommon | 6 | 0 | Цей ID / Same ID |
| `apotheosis:sigil_of_enhancement` | Сигіл посилення / Sigil of Enhancement | 1 | rare | 8 | 1 | Цей ID / Same ID |
| `apotheosis:sigil_of_socketing` | Сигіл гнізда / Sigil of Socketing | 1 | epic | 6 | 2 | Цей ID / Same ID |

### masterwork

Запас майстра / Masterwork Supplies — **24** позицій / entries.

| Item ID | Українська назва / English name | × | Rarity | w | L | Виготовлення / Manufacture |
| --- | --- | ---: | --- | ---: | ---: | --- |
| `minecraft:emerald` | Смарагд / Emerald | 3 | common | 10 | 0 | — |
| `minecraft:gold_ingot` | Золотий зливок / Gold Ingot | 4 | common | 10 | 0 | — |
| `minecraft:golden_carrot` | Золота морква / Golden Carrot | 4 | common | 10 | 0 | — |
| `minecraft:experience_bottle` | Пляшечка досвіду / Bottle o’ Enchanting | 6 | common | 10 | 0 | — |
| `minecraft:name_tag` | Бирка / Name Tag | 1 | common | 10 | 0 | — |
| `kubejs:industrial_chassis` | Промислове шасі / Industrial Chassis | 1 | uncommon | 6 | 1 | Цей ID / Same ID |
| `kubejs:precision_mechanism` | Точний механізм / Precision Mechanism | 1 | uncommon | 8 | 2 | Цей ID / Same ID |
| `kubejs:dimensional_matrix` | Вимірна матриця / Dimensional Matrix | 1 | rare | 10 | 3 | Цей ID / Same ID |
| `kubejs:stellar_alloy` | Зоряний композит / Stellar Alloy | 1 | rare | 10 | 4 | Цей ID / Same ID |
| `kubejs:mythic_circuit` | Міфічна схема / Mythic Circuit | 1 | epic | 8 | 5 | Цей ID / Same ID |
| `kubejs:emc_core` | Ядро EMC / EMC Core | 1 | epic | 4 | 6 | Цей ID / Same ID |
| `kubejs:quantum_processor` | Квантовий процесор / Quantum Processor | 1 | rare | 12 | 3 | Цей ID / Same ID |
| `kubejs:radiation_shield` | Радіаційний захист / Radiation Shield | 1 | rare | 10 | 4 | Цей ID / Same ID |
| `kubejs:reinforced_netherite_plate` | Посилена незеритова пластина / Reinforced Netherite Plate | 1 | epic | 6 | 4 | Цей ID / Same ID |
| `projecte:alchemical_coal` | Алхімічне вугілля / Alchemical Coal | 1 | uncommon | 14 | 6 | Цей ID / Same ID |
| `projecte:mobius_fuel` | Паливо Мебіуса / Mobius Fuel | 1 | uncommon | 14 | 6 | Цей ID / Same ID |
| `projecte:aeternalis_fuel` | Етернальне паливо / Aeternalis Fuel | 1 | uncommon | 12 | 6 | Цей ID / Same ID |
| `projecte:dark_matter` | Темна матерія / Dark Matter | 1 | rare | 10 | 6 | Цей ID / Same ID |
| `projecte:red_matter` | Червона матерія / Red Matter | 1 | epic | 6 | 6 | Цей ID / Same ID |
| `projecte:klein_star_ein` | Зірка Кляйна I / Klein Star Ein | 1 | rare | 6 | 6 | Цей ID / Same ID |
| `projecte:klein_star_zwei` | Зірка Кляйна II / Klein Star Zwei | 1 | epic | 4 | 6 | Цей ID / Same ID |
| `avaritia:diamond_lattice` | Діамантова решітка / Diamond Lattice | 1 | rare | 8 | 5 | Цей ID / Same ID |
| `avaritia:crystal_matrix_ingot` | Злиток кристалічної матриці / Crystal Matrix Ingot | 1 | epic | 6 | 5 | Цей ID / Same ID |
| `avaritia:neutron_nugget` | Нейтронієвий самородок / Neutronium Nugget | 1 | rare | 8 | 5 | Цей ID / Same ID |

## Порівняння з іншими збірками / Pack references

Переглянуто прямі конфігурації ATM10 та Craftoria. ATM10 розділяє нагороди за темами та дає партії схем, сплавів, кабелів і матеріалів. У Craftoria ресурсна таблиця містить по 12 металевих злитків, по 16 вугілля/редстоуну/лазуриту; тематичний Powah-пул поєднує витратні матеріали й окреме обладнання. Звідси запозичено принцип корисної партії та відповідності гілці. Особиста видача, точні виробничі відкриття та гарантоване перше uncommon-or-better жеребкування — правила SOQM, а не твердження про ці збірки.

Direct ATM10 and Craftoria configurations informed the useful-batch sizes and themed contents. ATM10 separates themed component pools; Craftoria's resource table includes 12 metal ingots and 16 coal/redstone/lapis per selected prize, while its Powah pool mixes crafting supplies with equipment. SOQM's personal claims, exact production gates and reserved uncommon-or-better first draw remain SOQM-specific rules.

- [ATM10 Mekanism rewards](https://github.com/AllTheMods/ATM-10/blob/main/config/ftbquests/quests/reward_tables/mekanism.snbt)
- [ATM10 advanced Mekanism rewards](https://github.com/AllTheMods/ATM-10/blob/main/config/ftbquests/quests/reward_tables/amekanismr_dadvanced_loot_boxr.snbt)
- [Craftoria resource rewards](https://github.com/TeamAOF/Craftoria/blob/main/config/ftbquests/quests/reward_tables/59C15B9D0F7ED5E1.snbt)
- [Craftoria Powah rewards](https://github.com/TeamAOF/Craftoria/blob/main/config/ftbquests/quests/reward_tables/0414572B7C36F04F.snbt)

These are balance references, not evidence of a completed SOQM survival playthrough. In-game pacing remains a playtest question.
