# SOQM 0.4.8: особисті прогресивні нагороди / Progressive personal rewards

**Кожен учасник отримує власний пакунок за завершений звичайний квест. Пакунок містить 2–4 незалежні тематичні призи; дорогий предмет стає доступним лише після фактичного виготовлення цього самого предмета командою.** Кількість призів і ваги рідкостей зростають із виробничими відкриттями. Отримання ручне, одноразове й особисте.

**Every member can claim a personal pack for a completed ordinary quest. A pack contains 2–4 independently drawn themed prizes; an expensive item becomes eligible only after the team has actually manufactured that exact item.** Production discoveries increase the number of draws and the weights of higher rarities. Claims are manual, personal and one-time.

Каталог і правила задає [`metadata/reward_pools.json`](../metadata/reward_pools.json). Підтвердження предметів і продуктивних рецептів із закріплених версій модів містить [`metadata/reward_item_evidence.json`](../metadata/reward_item_evidence.json). Компілятор створює [`kubejs/config/soqm_rewards.json`](../overrides/kubejs/config/soqm_rewards.json); обробник нагород і спостерігач виробництва читають цей спільний runtime-файл.

The catalogue and rules live in [`metadata/reward_pools.json`](../metadata/reward_pools.json). Exact pinned item and productive recipe evidence is recorded in [`metadata/reward_item_evidence.json`](../metadata/reward_item_evidence.json). The compiler produces [`kubejs/config/soqm_rewards.json`](../overrides/kubejs/config/soqm_rewards.json), the shared runtime policy read by the reward handler and production observer.

| Частина системи / System part | Кількість / Count |
| --- | ---: |
| Наявні зовнішні ID нагород, збережені / Preserved outer reward IDs | 1626 |
| Звичайні квести з особистим прогресивним пакунком / Ordinary quests with a personal progressive pack | 1611 |
| Рубежі з особистим фіксованим Mayhem Cube / Milestones with a personal fixed Mayhem Cube | 14 |
| Особиста вступна книга / Personal introductory book | 1 |
| Тематичні пули / Themes | 14 |
| Позиції повного каталогу / Catalogue entries | 326 |
| Унікальні предмети, з них модові / Distinct items, including modded items | 240 / 210 |
| Модові позиції з повтореннями між темами / Modded entries across themes | 256 |
| Позиції з умовою власного виготовлення / Entries requiring exact manufacture | 240 |
| Унікальні відстежувані виробничі результати / Distinct tracked production outputs | 202 |

У кожного звичайного квесту один пакунок. Вступний квест додатково зберігає особисту книгу. Чотирнадцять фіксованих рубежів перелічено нижче; вони видають один куб замість пакунка. Числа походять із каталогу та застосування [`campaign_rewards.apply`](../scripts/campaign_rewards.py) до наявної книги.

Each ordinary quest has one pack. The introductory quest also retains its personal book. The fourteen fixed milestones listed below award one cube in place of a pack. These counts come from the catalogue and [`campaign_rewards.apply`](../scripts/campaign_rewards.py) applied to the existing book.

## Як це працює

1. **Тему задає глава.** Майстерня, логістика, магія, експедиції та інші гілки мають власні набори. Колишній статичний перехід на інший пул через тег `stage_N` прибрано: тема залишається темою глави, а доступність і якість залежать від поточного виробництва команди.
2. **Рівень визначається виробництвом.** Система бере найвищий рівень із шести рубежів, для якого записано справжнє виготовлення відповідного KubeJS-компонента. До першого такого запису діє рівень 0. Це окрема шкала нагород; завершення предметного завдання в квестбуці саме собою не створює виробничий запис.
3. **Кожен виріб має власне відкриття.** Для позиції з `requires_craft` потрібні всі зазначені item ID у виробничих фактах команди та рівень не нижчий за `min_level`. Виготовлена залізна бочка відкриває саме залізну бочку; золоту й незеритову потрібно реально виготовити окремо. Рівень 6 також не відкриває ще не виготовлені процесори, шасі чи поліпшення.
4. **Пакунок розігрується під час отримання.** Враховується стан команди в момент натискання «отримати», а не стан під час завершення квесту. Для кожного з 2–4 призів спочатку обирається доступна рідкість, потім предмет усередині неї. Повтори дозволені; однакові результати складаються. Видача предметів не створює виробничих відкриттів.

Доказом служить завершена підтримувана операція виробництва з фактичним результатом. Підбирання, передавання, відкривання скрині, видача нагороди, наявність предмета в інвентарі, JEI, ME-склад і завершена заявка CPU без підтвердженої виробничої операції не є таким доказом. Перейменування, скидання чи зміна оформлення з тим самим item ID серед входів і виходу відсікаються. Для нового дорогого типу виробництва потрібен окремий надійний observer; замінювати його перевіркою інвентарю не можна.

Спостерігач зберігає особисті виробничі факти гравця та окреме об’єднання фактів його партії FTB Teams. Власні відкриття можна принести в партію; читання відкриттів партії не копіює чужі факти назад у власний запис. Виробництво машини приписується її справжньому власнику/зареєстрованому виробнику та команді, записаній при прив’язці машини, а не найближчому гравцеві. Creative, spectator і fake-player дії не створюють виробничих фактів. Джерело контракту: [`ProductionEvidence`](../src/compat/progressive-rewards/src/qm/rewards/ProductionEvidence.java).

**Після вступу в партію або виходу з неї власнику потрібно забрати й повторно встановити раніше прив’язану підтримувану машину в поточній команді, у survival/adventure.** Це прив’язує наступні справжні завершені цикли до нової команди. До повторного встановлення стара командна прив’язка не записує нові відкриття; уже записані особисті виробничі факти зберігаються.

**Старі інвентарі автоматично не індексуються.** Після оновлення старий запас, пройдені квести й уже готові машини не вважаються доказом минулого виробництва. Потрібно виконати нову підтримувану операцію. Раніше встановлена машина з придатним native-власником може записати наступний справжній завершений результат; це не зарахування старого вмісту. Без придатного власника або виробничого observer дорогі позиції залишаються закритими.

Кожна тема має 5–11 доступних стартових припасів, тож за відсутності відкриттів пакунок усе одно має вміст. Невелика їжа, деревина, папір та звичайні матеріали служать базою; виготовлені деталі й дорогі готові речі відкриваються поступово. Порожній або несправний пул переходить до `survival`, а обробник має окремий аварійний запас базових vanilla-припасів. Це захист від некоректної runtime-політики, а не колишня система зміни теми за етапом.

## How it works

1. **The chapter determines the theme.** Workshop, logistics, magic, expedition and the other branches have their own contents. The old static `stage_N` fallback is removed: the chapter keeps its theme while the team's current production facts determine eligibility and quality.
2. **Manufacture determines the level.** The system uses the highest of the six milestone levels whose exact KubeJS component has a recorded real manufacture. Before the first such fact, the level is 0. This is a separate reward scale; completing an item-detection quest does not create a production fact.
3. **Every manufactured prize has its own unlock.** An entry requires every exact ID in `requires_craft` and a level at least as high as `min_level`. Producing an iron barrel unlocks the iron barrel itself; gold and netherite barrels must each be manufactured separately. Level 6 alone does not unlock an unmade processor, chassis or upgrade.
4. **The pack is rolled when claimed.** Eligibility uses team state at the moment of claiming, not at quest completion. Each of the 2–4 draws first chooses an eligible rarity and then an item within that rarity. Draws are with replacement, so duplicate results can occur and their quantities are combined. Reward delivery never creates production facts.

Evidence comes from a completed supported production operation with a real output. Pickup, transfers, chest loot, reward delivery, inventory possession, JEI, ME storage and CPU job completion without an observed production operation are insufficient. Rename, reset or cosmetic operations with the same item ID in an input and the output are rejected. Adding a new expensive production family requires a trustworthy observer rather than an inventory check.

The observer keeps the player's own production facts and a separate union for the current FTB Teams party. A player can bring their own discoveries into a party; reading a party's discoveries does not copy other players' facts into their personal record. Machine production is attributed to its real owner/recorded producer and captured team, not a nearby player. Creative, spectator and fake-player actions do not create production facts. The contract is implemented in [`ProductionEvidence`](../src/compat/progressive-rewards/src/qm/rewards/ProductionEvidence.java).

**After joining or leaving a party, the owner must pick up and re-place a previously bound supported machine in the current team, in survival/adventure.** This binds its next real completed operations to the new team. Until re-placement, the old team binding does not record new discoveries; previously recorded personal production facts remain available.

**Existing inventories are not automatically backfilled.** Old stock, completed quests and already-built machines do not prove earlier production after upgrading. A new supported operation is required. An existing machine with a valid native owner can record its next real completed output; this does not credit its old contents. Missing valid ownership or an unsupported production observer leaves manufactured prizes locked.

Every theme contains 5–11 eligible starter supplies, so a pack has contents even without discoveries. Small food, wood, paper and ordinary materials provide the base; manufactured parts and expensive finished items unlock progressively. An empty or invalid pool falls back to `survival`, and the handler also retains emergency basic vanilla supplies. This protects against invalid runtime policy rather than changing the chapter theme by its static stage.

## Рівні та кількість призів / Levels and draw counts

Рівень — найвищий реально виготовлений рубіж у таблиці. Ваги рідкостей у чотирьох останніх стовпцях дорівнюють їхнім відсоткам **лише коли всі чотири рідкості мають доступні предмети**. За відсутності доступних предметів певної рідкості решта ваг нормалізуються; формула наведена нижче.

The level is the highest actually manufactured milestone in this table. The last four columns become rarity percentages **only when all four rarities have eligible items**. Empty rarity groups are omitted and the remaining weights are normalized, as described below.

| Рівень / Level | Рубіж / Milestone | Призів / Draws | Common | Uncommon | Rare | Epic |
| ---: | --- | ---: | ---: | ---: | ---: | ---: |
| 0 | До першого виробничого рубежу / Before the first production milestone | 2 | 70 | 25 | 4 | 1 |
| 1 | `kubejs:industrial_chassis` — Промислове шасі / Industrial Chassis | 2 | 65 | 28 | 6 | 1 |
| 2 | `kubejs:precision_mechanism` — Точний механізм / Precision Mechanism | 3 | 58 | 31 | 9 | 2 |
| 3 | `kubejs:dimensional_matrix` — Вимірна матриця / Dimensional Matrix | 3 | 50 | 35 | 12 | 3 |
| 4 | `kubejs:stellar_alloy` — Зоряний композит / Stellar Alloy | 3 | 45 | 36 | 15 | 4 |
| 5 | `kubejs:mythic_circuit` — Міфічна схема / Mythic Circuit | 4 | 38 | 39 | 18 | 5 |
| 6 | `kubejs:emc_core` — Ядро EMC / EMC Core | 4 | 32 | 41 | 21 | 6 |

## Як подивитися поточні шанси / Viewing current chances

Команди доступні гравцеві й лише читають стан. Вони не отримують нагород, не виконують жеребкування та не змінюють відкриття чи позначки отримання. Назва нагороди в квестбуці показує тему; фактичний персональний перегляд доступності та відсотків забезпечують ці команди:

These player commands are read-only. They do not claim rewards, draw prizes, or change production facts or claim records. The quest reward title identifies its theme; these commands provide the current availability and percentage view:

```text
/soqm rewards status
/soqm rewards pool logistics
/soqm rewards pool engineering
/soqm rewards pool arcane
```

`/soqm rewards status` показує рівень, кількість жеребкувань, шість рубежів та список тем із доступними й закритими позиціями. `/soqm rewards pool <key>` показує кількість, рідкість і поточний нормалізований шанс **за одне жеребкування**; закриті предмети перелічують потрібний рівень і відсутні виробничі факти. Ключі всіх тем є у наступному розділі. Інтерфейс використовує українську для української мови клієнта, англійську — для інших мов.

`/soqm rewards status` shows the level, draw count, six milestones and theme availability counts. `/soqm rewards pool <key>` shows quantities, rarities and the current normalized chance **per draw**, followed by locked items and their missing level/production requirements. All theme keys appear below. The interface uses Ukrainian for Ukrainian client language and English for other languages.

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

Повторні жеребкування незалежні й допускають той самий результат. Середня кількість одиниць предмета `i` та шанс отримати його хоча б раз у пакунку:

Draws are independent and with replacement. Expected units of item `i` and the chance of receiving it at least once in a pack are:

$$
E[N_i]=d\,c_i\,P(i),\qquad
P(N_i>0)=1-(1-P(i))^d
$$

Внутрішня вага в каталозі **не є готовим відсотком**. Відкриття ще одного предмета тієї самої рідкості ділить вагу цієї рідкості між більшою кількістю результатів. Саме тому інтерфейс показує актуальний розрахунок, а каталог зберігає ваги та умови.

An entry's catalogue weight **is not its final percentage**. Unlocking another entry in the same rarity distributes that rarity's weight across more eligible results. The interface therefore shows the current calculation while the catalogue records weights and conditions.

| Приклад / Example | Шанс за одне жеребкування / Per-draw chance | Хоча б один за пакунок / At least one per pack |
| --- | ---: | ---: |
| Logistics, рівень 2: серед його manufactured-позицій виготовлено лише золоту бочку; активні common і rare / Level 2: only the gold barrel among this pool's manufactured entries is produced; only common and rare are available | Gold barrel: `9 / (58 + 9)` = 13.4328% | 35.1277% за 3 / over 3 draws |
| Logistics, рівень 6, виготовлено всі позиції пулу / Level 6, every entry in the pool is produced | Netherite barrel: `6/100 × 4/16` = 1.5% | 5.8663% за 4 / over 4 draws |
| Рівень 6, усі чотири рідкості доступні / Level 6, all four rarities are available | Any rare or epic: 27% | 71.6018% за 4 / over 4 draws |

Кількість за одне жеребкування — 1–4 одиниці. Усі `rare`/`epic`, усі KubeJS-компоненти, бочки, наплічники та їхні поліпшення видаються по одному. Невеликі винятки для дешевих виготовлених припасів — дошки, декоративні блоки, тканина, пелюстки та смолоскипи — явно мають кількість 2–4 у каталозі. Повтори дорогого результату можуть дати кілька одиниць за пакунок, але кожне окреме жеребкування дає одну. Загальна жорстка межа звичайного пакунка — 16 предметів із чотирьох жеребкувань по 4; це не гарантована кількість і не оцінка цінності.

A draw contains 1–4 units. All rare/epic entries, KubeJS components, barrels, backpacks and their upgrades award one unit. Cheap manufactured-supply exceptions such as planks, decorative blocks, fabric, petals and torches explicitly use 2–4 units in the catalogue. Repeated expensive results can give multiple units in one pack, but each individual draw awards one. The absolute ordinary-pack bound is 16 items from four draws of four; this is neither a guaranteed quantity nor a value estimate.

| Пул / Pool | Середня кількість за пакунок на рівні 6, усе відкрито / Expected units per level-6 pack, everything unlocked |
| --- | ---: |
| engineering | 5.536 |
| logistics | 6.304 |
| arcane | 5.280 |
| space | 5.067 |
| masterwork | 4.000 |

Ці значення — умовні математичні сподівання, не результати ігрового проходження. Фіксованої суми ресурсів «за всю кампанію» більше немає: її визначають момент отримання, відкриті вироби та кількість гравців. Особистий режим множить загальну командну видачу на кількість учасників, які отримають власні нагороди. Більша нагорода не стає повторюваним квестом; потрібний для першого виготовлення дорогий предмет не може випасти до відповідного виробничого факту.

These are conditional mathematical expectations, not playthrough results. There is no single fixed campaign-wide material budget: claim timing, discovered outputs and player count determine it. Personal claims increase total team delivery with the number of members claiming their own rewards. Larger packs do not make quests repeatable; a required first expensive item cannot drop before its exact production fact exists.

**Пластик і HDPE виключені з призів, зокрема після відкриття.** Готовий радіаційний захист може бути призом після його фактичного виготовлення: HDPE у рецепті захисту не перетворюється на окрему нагороду. Контейнери, енергокуби, планшети мани й інші предмети створюються свіжими, із порожнім/default станом. Копіювати зі зразка вміст, storage UUID, енергію, ману, поліпшення чи афікси заборонено.

**Plastic and HDPE are excluded from prizes, including after unlock.** A finished radiation shield can be rewarded after its actual manufacture; HDPE used in its recipe is not itself a prize. Containers, energy cubes, mana tablets and other items are created fresh with empty/default state. Their contents, storage UUIDs, energy, mana, upgrades and affixes are never copied from the manufactured specimen.

## Особисті отримання та оновлення старого світу / Personal claims and existing-world upgrades

FTB Quests зберігає позначку отримання за **зовнішнім ID нагороди та областю власника**. У 0.4.8 для наявних нагород установлено `team_reward: false`, `auto: "disabled"`; звичайний пакунок має `type: "custom"`. Об’єкти квестів, задач і нагород зберігають свої ID. Зміна типу не змінює ID, але навмисна зміна командної області на особисту створює інше право отримання.

FTB Quests keys a claim by the **outer reward ID and owner scope**. Version 0.4.8 sets existing rewards to `team_reward: false`, `auto: "disabled"`; an ordinary pack uses `type: "custom"`. Quest, task and reward IDs remain unchanged. The type change preserves identity, while the deliberate change from team scope to personal scope creates a different claim entitlement.

| Старий стан / Previous state | Поведінка 0.4.8 / Version 0.4.8 behavior |
| --- | --- |
| Командний приз уже отримано під ключем `(NIL_UUID, reward ID)` / A shared reward was already claimed under `(NIL_UUID, reward ID)` | **Кожен гравець отримує одне нове особисте право** під `(player UUID, reward ID)`. Старий NIL-запис не зараховується як його особистий claim. / **Each player gets one new personal entitlement** under `(player UUID, reward ID)`; the old NIL record is not their personal claim. |
| Особистий приз уже отримано під `(player UUID, reward ID)` / A personal reward was already claimed under `(player UUID, reward ID)` | Залишається отриманим; повторної видачі немає. / Remains claimed, with no second delivery. |
| Квест завершено, особисту нагороду ще не отримано / Quest completed, personal reward still unclaimed | Дає новий вміст за поточними відкриттями при ручному отриманні. / Gives the new contents using current discoveries when manually claimed. |
| Особиста вступна книга вже отримана / Personal introductory book already claimed | Її старе особисте отримання зберігається. / Its existing personal claim remains recorded. |
| Старі припаси, машини чи компоненти вже є в інвентарі / Old supplies, machines or components already exist in inventory | Залишаються у світі, але автоматичних виробничих відкриттів із них немає. / Remain in the world, without automatic production-fact backfill. |

Це навмисна міграція від спільного подарунка до особистого. Вона стосується і 14 фіксованих кубів. Власні уже отримані нагороди не скидаються; старі спільні отримання не копіюються у всі UUID. Повторне натискання після нового особистого claim не дає ще один пакунок. Очікувана поведінка при вході/виході з партії, збереженні та перезапуску входить до native-перевірки, описаної нижче.

This is an intentional migration from one shared gift to a personal gift. It also applies to the fourteen fixed cubes. Existing personal claims are not cleared, and previous shared claims are not copied to every UUID. Clicking again after the new personal claim cannot award another pack. Expected party join/leave, save and restart behavior belongs to the native validation coverage described below.

Стан виробництва й стан отримання — окремі записи. Видача використовує native `ItemReward` для нових порожніх/default стеків, а FTB Quests зберігає справжній зовнішній claim ID. Тимчасові об’єкти видачі не створюють нових постійних нагород. Джерела: [`campaign_rewards.py`](../scripts/campaign_rewards.py), [`soqm_progressive_rewards.js`](../overrides/kubejs/server_scripts/soqm_progressive_rewards.js) та закріплені FTB Quests/Teams у [`mods.lock.json`](../metadata/mods.lock.json).

Production facts and claim state are separate records. Delivery uses native `ItemReward` with new empty/default stacks, while FTB Quests owns the real outer claim ID. Transient delivery objects do not introduce persistent reward IDs. Sources: [`campaign_rewards.py`](../scripts/campaign_rewards.py), [`soqm_progressive_rewards.js`](../overrides/kubejs/server_scripts/soqm_progressive_rewards.js), and pinned FTB Quests/Teams in [`mods.lock.json`](../metadata/mods.lock.json).

## Фіксовані рубежі / Fixed milestones

Кожен наведений квест дає особисто один Mayhem Cube відповідного рівня. Він не виконує 2–4 жеребкування звичайного пакунка. Власні події кубів не включено в наведені очікування припасів. / Each listed quest gives one personal Mayhem Cube of the indicated tier. It does not perform the ordinary pack's 2–4 draws. Cube events are outside the supply expectations above.

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

**Нижче наведено очікуване покриття нових hooks 0.4.8. Цей документ не заявляє успішне проходження native-перевірки.** Фактичний статус потрібно звіряти зі звітами поточної версії у [`reports/`](../reports/); результати попередньої 0.4.7 не доводять нові шляхи виробництва й особистої міграції. Native серверна перевірка також не є графічною перевіркою клієнта чи повним survival-проходженням.

**The rows below describe intended coverage of the new 0.4.8 hooks. This document does not assert that native validation has passed.** Check the current-version results in [`reports/`](../reports/) for actual evidence; previous 0.4.7 results do not prove the new production or personal-migration paths. A native dedicated-server check also does not constitute graphical client validation or a complete survival playthrough.

| Шлях / Path | Очікувана умова запису / Intended recorded event | Приклади / Examples |
| --- | --- | --- |
| Звичайна сітка виготовлення, ручне ME / Player crafting grid, manual ME crafting | Native `ItemCraftedEvent`, справжній гравець, непорожні входи, output ID відсутній серед входів / Real player, nonempty consumed recipe inputs, output ID absent from the inputs | KubeJS-компоненти, схеми Mekanism в authored grid-рецептах, бочки та поліпшення / KubeJS parts, authored grid-made Mekanism circuits, barrels and upgrades |
| AE2 Molecular Assembler | Результат прийнято native-логікою; входи замінено залишками рецепта / Native output accepted and recipe inputs replaced with remainders | Підтримувані закодовані crafting-рецепти / Supported encoded crafting recipes |
| AE2 Inscriber | Прийнятий вихід і фактичне споживання середнього входу, без same-item перетворення / Accepted output and actual middle-input consumption, excluding same-item transformations | Logic, Calculation, Engineering Processor |
| Mekanism Metallurgic Infuser | Native cached-recipe finish після споживання й видачі, однозначний вихід / Native cached-recipe finish after consumption and output, unambiguous result | Basic Control Circuit; Infused, Reinforced, Atomic Alloy |
| Mekanism Infusing Factory | Та сама умова завершення, тільки `FactoryType.INFUSING` / The same completed-operation condition, restricted to `FactoryType.INFUSING` | Ті самі схеми та сплави у фабриці / The same circuits and alloys in a factory |
| Sophisticated crafting serializers | Справжній результат vanilla/ME crafting для зміни рівня та upgrade-рецептів / Actual vanilla/ME craft result for tier and upgrade recipes | Storage tier upgrades, backpack upgrades, next-tier upgrades |
| Sophisticated upgrade на встановленій бочці / In-world storage tier upgrade | Native upgrade успішний, предмет поліпшення реально витрачено, новий block-item ID відрізняється / Native upgrade succeeded, upgrade item was consumed, and the resulting block-item ID changed | Підвищення рівня вже встановленої бочки / Upgrading an already-placed barrel |

Native-джерела: [`QmProgressiveRewards`](../src/compat/progressive-rewards/src/qm/rewards/QmProgressiveRewards.java), [`ProductionEvidence`](../src/compat/progressive-rewards/src/qm/rewards/ProductionEvidence.java), [`ProductionPolicy`](../src/compat/progressive-rewards/src/qm/rewards/ProductionPolicy.java), [mixin-класи](../src/compat/progressive-rewards/src/qm/rewards/mixin/). Докази в каталозі вказують реальні продуктивні recipe ID й способи; native whitelist задає точні дозволені output item ID, а не закриває чесні варіанти деревини чи альтернативні supported-рецепти.

Native sources: [`QmProgressiveRewards`](../src/compat/progressive-rewards/src/qm/rewards/QmProgressiveRewards.java), [`ProductionEvidence`](../src/compat/progressive-rewards/src/qm/rewards/ProductionEvidence.java), [`ProductionPolicy`](../src/compat/progressive-rewards/src/qm/rewards/ProductionPolicy.java), and [mixin classes](../src/compat/progressive-rewards/src/qm/rewards/mixin/). Catalogue evidence identifies real productive recipe IDs and methods; the native whitelist uses exact permitted output item IDs without excluding legitimate wood variants or alternative supported recipes.

Потрібно перевірити справжнє завершення кожного підтримуваного каналу, заблокований вихід і нестачу входів, відсутність фактів від pickup/reward/reset, коректного власника й команди, creative/fake/spectator, вихід власника з гри, збереження/reload, особисті claims двох гравців, старий NIL-claim і збережений старий personal-claim, відсутній/пошкоджений policy та відсутній observer. Окремо слід перевірити українські й англійські команди, назви та читабельність у клієнті.

Validation must cover real completion for every supported channel, blocked output and missing inputs, no facts from pickup/reward/reset, correct owner and team, creative/fake/spectator actors, offline owners, save/reload, two players' personal claims, old NIL claims and preserved old personal claims, missing/invalid policy, and a missing observer. Ukrainian/English commands, labels and client readability require separate client review.

## Редагування й відтворення / Editing and regeneration

Змінювати склад потрібно в [`metadata/reward_pools.json`](../metadata/reward_pools.json), а підтвердження — у [`metadata/reward_item_evidence.json`](../metadata/reward_item_evidence.json). Зберігайте позитивні ваги, 1–4 одиниці за жеребкування, одноштучні дорогі призи, двомовні назви, точний own-item gate і щонайменше п’ять безпечних стартових результатів у кожній темі. Новий machine-only приз потребує підтримуваного й перевіреного observer. Інвентар, квестова детекція чи JEI не є заміною.

Edit contents in [`metadata/reward_pools.json`](../metadata/reward_pools.json) and evidence in [`metadata/reward_item_evidence.json`](../metadata/reward_item_evidence.json). Preserve positive weights, 1–4 units per draw, single-unit expensive prizes, bilingual names, exact own-item gates and at least five safe starter results per theme. A new machine-only prize requires a supported and validated observer. Inventory, quest detection and JEI are not substitutes.

`campaign_rewards.apply(chapters, locale)` перетворює наявні ordinary reward objects на особисті custom rewards зі збереженими ID, лишає milestone-куби фіксованими й зберігає особисту книгу. Runtime-політика містить mapping зовнішнього reward ID до quest ID і теми. Старі 14 статичних random-таблиць попередньої політики прибираються під час генерації; нова система не використовує їхні незмінні відсотки.

`campaign_rewards.apply(chapters, locale)` converts existing ordinary reward objects into personal custom rewards while retaining IDs, keeps milestone cubes fixed and preserves the personal book. Runtime policy maps the outer reward ID to its quest ID and theme. Regeneration removes the previous policy's fourteen static random tables; the new system does not use their fixed percentages.

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
node tests/progressive-rewards.mjs
```

Повна перевірка пакунка та окремий native-серверний запуск / Full package checks and separate native-server probe:

```sh
./scripts/check
python3 scripts/runtime_probe.py
```

`campaign_rewards.py --write` відтворює runtime-політику з уже згенерованих глав і прибирає відомі застарілі таблиці; самі глави потрібно оновлювати через `campaign_refresh.py`. Python і Node перевірки мають узгоджувати schema, точні шанси, власне виготовлення, fallback і межі кількості. Native-перевірка має підтвердити фактичні класові hooks, видачу, дублікати й непорожню міграцію. Команди вище описують процедуру й не є твердженням, що її вже виконано для конкретного commit.

`campaign_rewards.py --write` rebuilds runtime policy from already-generated chapters and removes the known obsolete tables; use `campaign_refresh.py` to update chapters themselves. Python and Node checks must agree on schema, exact probabilities, manufacture gates, fallbacks and quantity bounds. Native validation must establish real class hooks, delivery, duplicate rejection and nonempty migration. The commands above describe the procedure rather than asserting it has already run for a particular commit.

## Повний каталог / Full catalogue

Нижче наведено всі **326** позицій. `×` — фіксована кількість за одне жеребкування, `w` — відносна вага **всередині своєї рідкості**, `L` — мінімальний рівень. `Цей ID / Same ID` означає точну умову `requires_craft: [item ID з першого стовпця]`; потрібен реальний виробничий факт команди, а не наявність предмета. `—` означає стартові припаси без умови виробництва. Рідкості: `common` — звичайна, `uncommon` — незвичайна, `rare` — рідкісна, `epic` — епічна. Нульовий `L` не скасовує own-item gate.

All **326** entries appear below. `×` is the fixed quantity per draw, `w` is the relative weight **within its rarity**, and `L` is the minimum level. `Цей ID / Same ID` means the exact condition `requires_craft: [the item ID in the first column]`; it requires the team's real production fact, not possession. `—` denotes starter supplies without a production requirement. A minimum level of 0 does not remove the own-item gate. Use the formula or in-game read-only command for actual percentages.

Для кожного manufactured-предмета точні активні recipe ID, serializer, native/authored джерело й дані рецепта доступні за його item ID у [`reward_item_evidence.json`](../metadata/reward_item_evidence.json). Цей файл також містить native-моделі, джерела звичайної їжі та SHA-256 закріплених архівів. / Every manufactured item's exact active recipe IDs, serializers, native/authored sources and recipe data are indexed by item ID in [`reward_item_evidence.json`](../metadata/reward_item_evidence.json), alongside native models, ordinary-food sources and archive SHA-256 values.

### survival

Припаси для старту / Starting Supplies — **20** позицій / entries.

| Item ID | Українська назва / English name | × | Rarity | w | L | Виготовлення / Manufacture |
| --- | --- | ---: | --- | ---: | ---: | --- |
| `minecraft:bread` | Хліб / Bread | 2 | common | 10 | 0 | — |
| `minecraft:torch` | Смолоскип / Torch | 4 | common | 10 | 0 | — |
| `minecraft:oak_planks` | Дубові дошки / Oak Planks | 4 | common | 10 | 0 | — |
| `minecraft:leather` | Шкіра / Leather | 1 | common | 10 | 0 | — |
| `minecraft:paper` | Папір / Paper | 2 | common | 10 | 0 | — |
| `ecologics:crab_meat` | Крабове м’ясо / Crab Meat | 2 | common | 10 | 0 | — |
| `ecologics:coconut_slice` | Скибка кокоса / Coconut Slice | 3 | common | 10 | 0 | — |
| `ecologics:walnut` | Волоський горіх / Walnut | 3 | common | 10 | 0 | — |
| `twilightforest:cooked_venison` | Стейк з оленини / Venison Steak | 2 | common | 10 | 0 | — |
| `aether:blue_berry` | Синя ягода / Blue Berry | 3 | common | 10 | 0 | — |
| `ecologics:coconut_planks` | Кокосові дошки / Coconut Planks | 4 | common | 10 | 0 | Цей ID / Same ID |
| `ecologics:walnut_planks` | Горіхові дошки / Walnut Planks | 4 | common | 10 | 0 | Цей ID / Same ID |
| `exdeorum:crook` | Палиця з гаком / Crook | 1 | uncommon | 10 | 0 | Цей ID / Same ID |
| `exdeorum:wooden_hammer` | Дерев’яний молот / Wooden Hammer | 1 | uncommon | 10 | 0 | Цей ID / Same ID |
| `exdeorum:string_mesh` | Нитяна сітка / String Mesh | 1 | uncommon | 10 | 0 | Цей ID / Same ID |
| `immersiveengineering:hemp_fabric` | Міцна тканина / Tough Fabric | 1 | uncommon | 10 | 0 | Цей ID / Same ID |
| `sophisticatedbackpacks:backpack` | Наплічник / Backpack | 1 | rare | 8 | 0 | Цей ID / Same ID |
| `millenaire:masa` | Маса з кукурудзи / Masa | 2 | common | 10 | 0 | — |
| `aether:cold_parachute` | Холодний парашут / Cold Parachute | 1 | rare | 8 | 0 | Цей ID / Same ID |
| `botania:travel_belt` | Пояс мандрівника / Sojourner's Sash | 1 | epic | 4 | 1 | Цей ID / Same ID |

### workshop

Запас майстерні / Workshop Supplies — **24** позицій / entries.

| Item ID | Українська назва / English name | × | Rarity | w | L | Виготовлення / Manufacture |
| --- | --- | ---: | --- | ---: | ---: | --- |
| `minecraft:iron_nugget` | Шматочок заліза / Iron Nugget | 4 | common | 10 | 0 | — |
| `minecraft:coal` | Вугілля / Coal | 2 | common | 10 | 0 | — |
| `minecraft:copper_ingot` | Мідний зливок / Copper Ingot | 2 | common | 10 | 0 | — |
| `minecraft:iron_ingot` | Залізний злиток / Iron Ingot | 1 | common | 10 | 0 | — |
| `minecraft:gold_nugget` | Шматочок золота / Gold Nugget | 4 | common | 10 | 0 | — |
| `immersiveengineering:plate_iron` | Залізна пластина / Iron Plate | 1 | uncommon | 14 | 0 | Цей ID / Same ID |
| `immersiveengineering:plate_copper` | Мідна пластина / Copper Plate | 1 | uncommon | 14 | 0 | Цей ID / Same ID |
| `immersiveengineering:plate_aluminum` | Алюмінієва пластина / Aluminium Plate | 1 | uncommon | 14 | 0 | Цей ID / Same ID |
| `immersiveengineering:plate_steel` | Сталева пластина / Steel Plate | 1 | uncommon | 14 | 0 | Цей ID / Same ID |
| `immersiveengineering:stick_steel` | Сталевий стрижень / Steel Rod | 1 | uncommon | 14 | 0 | Цей ID / Same ID |
| `immersiveengineering:wirecoil_copper` | Котушка дроту низької напруги / LV Wire Coil | 1 | uncommon | 14 | 0 | Цей ID / Same ID |
| `immersiveengineering:hemp_fabric` | Міцна тканина / Tough Fabric | 2 | common | 10 | 0 | Цей ID / Same ID |
| `immersiveengineering:treated_wood_horizontal` | Просочені дошки / Treated Wood Planks | 4 | common | 10 | 0 | Цей ID / Same ID |
| `immersiveengineering:component_iron` | Залізний механічний компонент / Iron Mechanical Component | 1 | rare | 12 | 0 | Цей ID / Same ID |
| `immersiveengineering:component_steel` | Сталевий механічний компонент / Steel Mechanical Component | 1 | rare | 8 | 1 | Цей ID / Same ID |
| `immersiveengineering:hammer` | Молот інженера / Engineer's Hammer | 1 | uncommon | 5 | 0 | Цей ID / Same ID |
| `immersiveengineering:screwdriver` | Викрутка інженера / Engineer's Screwdriver | 1 | uncommon | 5 | 0 | Цей ID / Same ID |
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
| `minecraft:redstone` | Редстоун / Redstone Dust | 3 | common | 10 | 0 | — |
| `minecraft:copper_ingot` | Мідний зливок / Copper Ingot | 2 | common | 10 | 0 | — |
| `minecraft:iron_ingot` | Залізний злиток / Iron Ingot | 1 | common | 10 | 0 | — |
| `minecraft:coal` | Вугілля / Coal | 2 | common | 10 | 0 | — |
| `minecraft:glass` | Скло / Glass | 3 | common | 10 | 0 | — |
| `mekanism:basic_control_circuit` | Базова керувальна схема / Basic Control Circuit | 1 | uncommon | 16 | 0 | Цей ID / Same ID |
| `mekanism:advanced_control_circuit` | Поліпшена керувальна схема / Advanced Control Circuit | 1 | rare | 14 | 2 | Цей ID / Same ID |
| `mekanism:elite_control_circuit` | Елітна керувальна схема / Elite Control Circuit | 1 | rare | 8 | 3 | Цей ID / Same ID |
| `mekanism:ultimate_control_circuit` | Найвища керувальна схема / Ultimate Control Circuit | 1 | epic | 8 | 4 | Цей ID / Same ID |
| `mekanism:alloy_infused` | Збагачений сплав / Infused Alloy | 1 | uncommon | 10 | 0 | Цей ID / Same ID |
| `mekanism:alloy_reinforced` | Зміцнений сплав / Reinforced Alloy | 1 | rare | 10 | 2 | Цей ID / Same ID |
| `mekanism:alloy_atomic` | Атомний сплав / Atomic Alloy | 1 | epic | 10 | 3 | Цей ID / Same ID |
| `ae2:logic_processor` | Логічний процесор / Logic Processor | 1 | rare | 14 | 1 | Цей ID / Same ID |
| `ae2:calculation_processor` | Обчислювальний процесор / Calculation Processor | 1 | rare | 14 | 1 | Цей ID / Same ID |
| `ae2:engineering_processor` | Інженерний процесор / Engineering Processor | 1 | rare | 14 | 1 | Цей ID / Same ID |
| `enderio:basic_capacitor` | Базовий конденсатор / Basic Capacitor | 1 | uncommon | 10 | 1 | Цей ID / Same ID |
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
| `minecraft:coal` | Вугілля / Coal | 2 | common | 10 | 0 | — |
| `minecraft:redstone` | Редстоун / Redstone Dust | 3 | common | 10 | 0 | — |
| `minecraft:copper_ingot` | Мідний зливок / Copper Ingot | 2 | common | 10 | 0 | — |
| `minecraft:iron_ingot` | Залізний злиток / Iron Ingot | 1 | common | 10 | 0 | — |
| `minecraft:glass` | Скло / Glass | 3 | common | 10 | 0 | — |
| `immersiveengineering:wirecoil_copper` | Котушка дроту низької напруги / LV Wire Coil | 1 | uncommon | 14 | 0 | Цей ID / Same ID |
| `immersiveengineering:wirecoil_electrum` | Котушка дроту середньої напруги / MV Wire Coil | 1 | uncommon | 14 | 1 | Цей ID / Same ID |
| `immersiveengineering:wirecoil_steel` | Котушка дроту високої напруги / HV Wire Coil | 1 | uncommon | 14 | 2 | Цей ID / Same ID |
| `immersiveengineering:connector_lv` | Роз’єм низької напруги / LV Wire Connector | 1 | uncommon | 12 | 0 | Цей ID / Same ID |
| `immersiveengineering:capacitor_lv` | Накопичувач низької напруги / LV Accumulator | 1 | rare | 8 | 1 | Цей ID / Same ID |
| `immersiveengineering:connector_mv` | Роз’єм середньої напруги / MV Wire Connector | 1 | uncommon | 12 | 1 | Цей ID / Same ID |
| `immersiveengineering:capacitor_mv` | Накопичувач середньої напруги / MV Accumulator | 1 | rare | 8 | 1 | Цей ID / Same ID |
| `immersiveengineering:connector_hv` | Роз’єм високої напруги / HV Wire Connector | 1 | uncommon | 12 | 2 | Цей ID / Same ID |
| `immersiveengineering:capacitor_hv` | Накопичувач високої напруги / HV Accumulator | 1 | epic | 8 | 2 | Цей ID / Same ID |
| `mekanism:energy_tablet` | Енергетична пластина / Energy Tablet | 1 | uncommon | 14 | 1 | Цей ID / Same ID |
| `mekanism:basic_energy_cube` | Базовий енергетичний куб / Basic Energy Cube | 1 | rare | 6 | 1 | Цей ID / Same ID |
| `mekanism:advanced_energy_cube` | Поліпшений енергетичний куб / Advanced Energy Cube | 1 | rare | 6 | 2 | Цей ID / Same ID |
| `mekanism:elite_energy_cube` | Елітний енергетичний куб / Elite Energy Cube | 1 | epic | 6 | 3 | Цей ID / Same ID |
| `mekanism:ultimate_energy_cube` | Найвищий енергетичний куб / Ultimate Energy Cube | 1 | epic | 6 | 4 | Цей ID / Same ID |
| `mekanism:basic_universal_cable` | Базовий універсальний кабель / Basic Universal Cable | 1 | uncommon | 12 | 0 | Цей ID / Same ID |
| `mekanism:advanced_universal_cable` | Поліпшений універсальний кабель / Advanced Universal Cable | 1 | uncommon | 12 | 2 | Цей ID / Same ID |
| `mekanism:elite_universal_cable` | Елітний універсальний кабель / Elite Universal Cable | 1 | uncommon | 12 | 3 | Цей ID / Same ID |
| `enderio:double_layer_capacitor` | Двошаровий конденсатор / Double-Layer Capacitor | 1 | rare | 10 | 2 | Цей ID / Same ID |
| `enderio:octadic_capacitor` | Восьмишаровий конденсатор / Octadic Capacitor | 1 | epic | 8 | 3 | Цей ID / Same ID |

### logistics

Припаси для складу / Storage Supplies — **24** позицій / entries.

| Item ID | Українська назва / English name | × | Rarity | w | L | Виготовлення / Manufacture |
| --- | --- | ---: | --- | ---: | ---: | --- |
| `minecraft:paper` | Папір / Paper | 4 | common | 10 | 0 | — |
| `minecraft:glass` | Скло / Glass | 2 | common | 10 | 0 | — |
| `minecraft:string` | Нитка / String | 2 | common | 10 | 0 | — |
| `minecraft:iron_nugget` | Шматочок заліза / Iron Nugget | 4 | common | 10 | 0 | — |
| `minecraft:redstone` | Редстоун / Redstone Dust | 2 | common | 10 | 0 | — |
| `sophisticatedstorage:barrel` | Дерев’яна бочка / Barrel | 1 | uncommon | 14 | 0 | Цей ID / Same ID |
| `sophisticatedstorage:copper_barrel` | Мідна бочка / Copper Barrel | 1 | uncommon | 10 | 0 | Цей ID / Same ID |
| `sophisticatedstorage:iron_barrel` | Залізна бочка / Iron Barrel | 1 | uncommon | 8 | 0 | Цей ID / Same ID |
| `sophisticatedstorage:gold_barrel` | Золота бочка / Gold Barrel | 1 | rare | 10 | 2 | Цей ID / Same ID |
| `sophisticatedstorage:diamond_barrel` | Діамантова бочка / Diamond Barrel | 1 | rare | 6 | 3 | Цей ID / Same ID |
| `sophisticatedstorage:netherite_barrel` | Незеритова бочка / Netherite Barrel | 1 | epic | 4 | 4 | Цей ID / Same ID |
| `ae2:logic_processor` | Логічний процесор / Logic Processor | 1 | rare | 12 | 1 | Цей ID / Same ID |
| `ae2:calculation_processor` | Обчислювальний процесор / Calculation Processor | 1 | rare | 12 | 1 | Цей ID / Same ID |
| `ae2:engineering_processor` | Інженерний процесор / Engineering Processor | 1 | rare | 12 | 1 | Цей ID / Same ID |
| `ae2:fluix_glass_cable` | Скляний флюїксовий ME-кабель / Fluix ME Glass Cable | 1 | uncommon | 14 | 0 | Цей ID / Same ID |
| `ae2:quartz_fiber` | Кварцове волокно / Quartz Fiber | 1 | uncommon | 14 | 0 | Цей ID / Same ID |
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
| `minecraft:paper` | Папір / Paper | 4 | common | 10 | 0 | — |
| `minecraft:lapis_lazuli` | Лазурит / Lapis Lazuli | 2 | common | 10 | 0 | — |
| `minecraft:glass` | Скло / Glass | 2 | common | 10 | 0 | — |
| `minecraft:amethyst_shard` | Уламок аметисту / Amethyst Shard | 1 | common | 10 | 0 | — |
| `minecraft:sugar` | Цукор / Sugar | 3 | common | 10 | 0 | — |
| `ae2:certus_quartz_cutting_knife` | Ніж із сертус-кварцу / Certus Quartz Cutting Knife | 1 | uncommon | 8 | 0 | Цей ID / Same ID |
| `ae2:certus_quartz_wrench` | Ключ із сертус-кварцу / Certus Quartz Wrench | 1 | uncommon | 8 | 0 | Цей ID / Same ID |
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
| `minecraft:lapis_lazuli` | Лазурит / Lapis Lazuli | 2 | common | 10 | 0 | — |
| `minecraft:amethyst_shard` | Уламок аметисту / Amethyst Shard | 2 | common | 10 | 0 | — |
| `minecraft:glass` | Скло / Glass | 2 | common | 10 | 0 | — |
| `minecraft:paper` | Папір / Paper | 3 | common | 10 | 0 | — |
| `minecraft:experience_bottle` | Пляшечка досвіду / Bottle o’ Enchanting | 1 | common | 10 | 0 | — |
| `botania:white_petal` | Біла містична пелюстка / Mystical White Petal | 2 | common | 10 | 0 | Цей ID / Same ID |
| `botania:purple_petal` | Фіолетова містична пелюстка / Mystical Purple Petal | 2 | common | 10 | 0 | Цей ID / Same ID |
| `botania:light_blue_petal` | Блакитна містична пелюстка / Mystical Light Blue Petal | 2 | common | 10 | 0 | Цей ID / Same ID |
| `botania:pink_petal` | Рожева містична пелюстка / Mystical Pink Petal | 2 | common | 10 | 0 | Цей ID / Same ID |
| `botania:vine_ball` | Куля з ліан / Vine Ball | 1 | uncommon | 14 | 0 | Цей ID / Same ID |
| `botania:lexicon` | Лексика Botania / Lexica Botania | 1 | uncommon | 3 | 0 | Цей ID / Same ID |
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
| `minecraft:bread` | Хліб / Bread | 2 | common | 10 | 0 | — |
| `minecraft:torch` | Смолоскип / Torch | 4 | common | 10 | 0 | — |
| `minecraft:arrow` | Стріла / Arrow | 4 | common | 10 | 0 | — |
| `minecraft:leather` | Шкіра / Leather | 1 | common | 10 | 0 | — |
| `minecraft:white_wool` | Біла вовна / White Wool | 2 | common | 10 | 0 | — |
| `ecologics:crab_meat` | Крабове м’ясо / Crab Meat | 2 | common | 10 | 0 | — |
| `twilightforest:cooked_venison` | Стейк з оленини / Venison Steak | 2 | common | 10 | 0 | — |
| `twilightforest:cooked_meef` | Стейк із мінотавра / Meef Steak | 2 | common | 10 | 0 | — |
| `aether:blue_berry` | Синя ягода / Blue Berry | 3 | common | 10 | 0 | — |
| `ecologics:coconut_slice` | Скибка кокоса / Coconut Slice | 3 | common | 10 | 0 | — |
| `waystones:blank_scroll` | Порожній сувій / Blank Scroll | 1 | uncommon | 14 | 0 | Цей ID / Same ID |
| `waystones:warp_dust` | Пил переміщення / Warp Dust | 1 | uncommon | 14 | 0 | Цей ID / Same ID |
| `waystones:return_scroll` | Сувій повернення / Return Scroll | 1 | uncommon | 12 | 0 | Цей ID / Same ID |
| `waystones:warp_scroll` | Сувій переміщення / Warp Scroll | 1 | rare | 10 | 1 | Цей ID / Same ID |
| `waystones:portal_scroll` | Портальний сувій / Portal Scroll | 1 | rare | 8 | 2 | Цей ID / Same ID |
| `aether:ambrosium_torch` | Амброзієвий смолоскип / Ambrosium Torch | 2 | common | 10 | 0 | Цей ID / Same ID |
| `aether:cold_parachute` | Холодний парашут / Cold Parachute | 1 | rare | 8 | 0 | Цей ID / Same ID |
| `aether:golden_parachute` | Золотий парашут / Golden Parachute | 1 | rare | 6 | 1 | Цей ID / Same ID |
| `sophisticatedbackpacks:backpack` | Наплічник / Backpack | 1 | rare | 8 | 0 | Цей ID / Same ID |
| `sophisticatedbackpacks:feeding_upgrade` | Поліпшення годування / Feeding Upgrade | 1 | rare | 10 | 1 | Цей ID / Same ID |
| `botania:travel_belt` | Пояс мандрівника / Sojourner's Sash | 1 | epic | 6 | 1 | Цей ID / Same ID |
| `ecologics:coconut_boat` | Кокосовий човен / Coconut Boat | 1 | uncommon | 6 | 0 | Цей ID / Same ID |

### settlement

Посилка для будівельника / Settlement Supplies — **24** позицій / entries.

| Item ID | Українська назва / English name | × | Rarity | w | L | Виготовлення / Manufacture |
| --- | --- | ---: | --- | ---: | ---: | --- |
| `minecraft:brick` | Цеглина / Brick | 4 | common | 10 | 0 | — |
| `minecraft:oak_planks` | Дубові дошки / Oak Planks | 4 | common | 10 | 0 | — |
| `minecraft:white_wool` | Біла вовна / White Wool | 2 | common | 10 | 0 | — |
| `minecraft:paper` | Папір / Paper | 3 | common | 10 | 0 | — |
| `minecraft:bread` | Хліб / Bread | 2 | common | 10 | 0 | — |
| `ecologics:coconut_planks` | Кокосові дошки / Coconut Planks | 4 | common | 10 | 0 | Цей ID / Same ID |
| `ecologics:walnut_planks` | Горіхові дошки / Walnut Planks | 4 | common | 10 | 0 | Цей ID / Same ID |
| `ecologics:azalea_planks` | Азалієві дошки / Azalea Planks | 4 | common | 10 | 0 | Цей ID / Same ID |
| `millenaire:byzantine_stone_tiles` | Візантійська кам’яна плитка / Byzantine Stone Tiles | 4 | common | 10 | 0 | Цей ID / Same ID |
| `millenaire:byzantine_sandstone_tiles` | Візантійська пісковикова плитка / Byzantine Sandstone Tiles | 4 | common | 10 | 0 | Цей ID / Same ID |
| `millenaire:ice_brick` | Крижана цегла / Ice Brick | 4 | common | 10 | 0 | Цей ID / Same ID |
| `millenaire:masa` | Маса з кукурудзи / Masa | 2 | common | 10 | 0 | — |
| `millenaire:cider` | Сидр / Cider | 1 | uncommon | 12 | 0 | Цей ID / Same ID |
| `immersiveengineering:treated_wood_horizontal` | Просочені дошки / Treated Wood Planks | 4 | common | 10 | 0 | Цей ID / Same ID |
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
| `minecraft:glass` | Скло / Glass | 3 | common | 10 | 0 | — |
| `minecraft:redstone` | Редстоун / Redstone Dust | 3 | common | 10 | 0 | — |
| `minecraft:iron_ingot` | Залізний злиток / Iron Ingot | 1 | common | 10 | 0 | — |
| `minecraft:golden_carrot` | Золота морква / Golden Carrot | 1 | common | 10 | 0 | — |
| `minecraft:experience_bottle` | Пляшечка досвіду / Bottle o’ Enchanting | 1 | common | 10 | 0 | — |
| `ad_astra:iron_rod` | Залізний стрижень / Iron Rod | 1 | uncommon | 14 | 0 | Цей ID / Same ID |
| `ad_astra:steel_rod` | Сталевий стрижень / Steel Rod | 1 | uncommon | 14 | 1 | Цей ID / Same ID |
| `immersiveengineering:plate_iron` | Залізна пластина / Iron Plate | 1 | uncommon | 14 | 0 | Цей ID / Same ID |
| `immersiveengineering:plate_steel` | Сталева пластина / Steel Plate | 1 | uncommon | 14 | 1 | Цей ID / Same ID |
| `ad_astra:fan` | Вентилятор двигуна / Fan | 1 | uncommon | 12 | 1 | Цей ID / Same ID |
| `ad_astra:engine_frame` | Рама двигуна / Engine Frame | 1 | rare | 12 | 2 | Цей ID / Same ID |
| `ad_astra:oxygen_gear` | Кисневий комплект / Oxygen Gear | 1 | rare | 8 | 2 | Цей ID / Same ID |
| `ad_astra:oxygen_sensor` | Кисневий датчик / Oxygen Sensor | 1 | uncommon | 8 | 2 | Цей ID / Same ID |
| `ad_astra:steel_cable` | Сталевий кабель / Steel Cable | 1 | uncommon | 14 | 1 | Цей ID / Same ID |
| `ad_astra:desh_cable` | Кабель із дешу / Desh Cable | 1 | uncommon | 10 | 3 | Цей ID / Same ID |
| `ad_astra:desh_plate` | Пластина з дешу / Desh Plate | 1 | rare | 12 | 3 | Цей ID / Same ID |
| `ad_astra:ostrum_plate` | Острумова пластина / Ostrum Plate | 1 | rare | 12 | 4 | Цей ID / Same ID |
| `ad_astra:calorite_plate` | Калоритова пластина / Calorite Plate | 1 | rare | 12 | 4 | Цей ID / Same ID |
| `kubejs:propulsion_turbine` | Реактивна турбіна / Propulsion Turbine | 1 | rare | 10 | 2 | Цей ID / Same ID |
| `kubejs:stellar_alloy` | Зоряний композит / Stellar Alloy | 1 | epic | 8 | 4 | Цей ID / Same ID |
| `kubejs:radiation_shield` | Радіаційний захист / Radiation Shield | 1 | rare | 8 | 4 | Цей ID / Same ID |
| `kubejs:reinforced_netherite_plate` | Посилена незеритова пластина / Reinforced Netherite Plate | 1 | epic | 6 | 4 | Цей ID / Same ID |
| `ad_astra:coal_generator` | Вугільний генератор / Coal Generator | 1 | rare | 6 | 1 | Цей ID / Same ID |
| `ad_astra:cheese` | Сир / Cheese | 2 | common | 10 | 0 | — |

### processing

Сировина для переробки / Processing Supplies — **24** позицій / entries.

| Item ID | Українська назва / English name | × | Rarity | w | L | Виготовлення / Manufacture |
| --- | --- | ---: | --- | ---: | ---: | --- |
| `minecraft:sand` | Пісок / Sand | 4 | common | 10 | 0 | — |
| `minecraft:gravel` | Гравій / Gravel | 4 | common | 10 | 0 | — |
| `minecraft:cobblestone` | Бруківка / Cobblestone | 4 | common | 10 | 0 | — |
| `minecraft:clay_ball` | Грудка глини / Clay Ball | 4 | common | 10 | 0 | — |
| `minecraft:coal` | Вугілля / Coal | 2 | common | 10 | 0 | — |
| `exdeorum:crook` | Палиця з гаком / Crook | 1 | uncommon | 6 | 0 | Цей ID / Same ID |
| `exdeorum:bone_crook` | Кістяна палиця з гаком / Bone Crook | 1 | uncommon | 6 | 0 | Цей ID / Same ID |
| `exdeorum:wooden_hammer` | Дерев’яний молот / Wooden Hammer | 1 | uncommon | 8 | 0 | Цей ID / Same ID |
| `exdeorum:iron_hammer` | Залізний молот / Iron Hammer | 1 | uncommon | 10 | 0 | Цей ID / Same ID |
| `exdeorum:diamond_hammer` | Діамантовий молот / Diamond Hammer | 1 | rare | 8 | 1 | Цей ID / Same ID |
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
| `exmachinis:flux_hammer` | Енергетичний молот / Flux Hammer | 1 | rare | 8 | 2 | Цей ID / Same ID |
| `exmachinis:flux_sieve` | Енергетичне сито / Flux Sieve | 1 | epic | 6 | 2 | Цей ID / Same ID |
| `ironfurnaces:augment_blasting` | Модуль плавлення руд / Augment: Blasting | 1 | uncommon | 10 | 0 | Цей ID / Same ID |
| `ironfurnaces:augment_smoking` | Модуль копчення / Augment: Smoking | 1 | uncommon | 10 | 0 | Цей ID / Same ID |

### hunting

Припаси мисливця / Hunting Supplies — **24** позицій / entries.

| Item ID | Українська назва / English name | × | Rarity | w | L | Виготовлення / Manufacture |
| --- | --- | ---: | --- | ---: | ---: | --- |
| `minecraft:arrow` | Стріла / Arrow | 4 | common | 10 | 0 | — |
| `minecraft:string` | Нитка / String | 3 | common | 10 | 0 | — |
| `minecraft:bone` | Кістка / Bone | 2 | common | 10 | 0 | — |
| `minecraft:cooked_beef` | Стейк / Steak | 2 | common | 10 | 0 | — |
| `minecraft:leather` | Шкіра / Leather | 1 | common | 10 | 0 | — |
| `ecologics:crab_meat` | Крабове м’ясо / Crab Meat | 2 | common | 10 | 0 | — |
| `twilightforest:cooked_venison` | Стейк з оленини / Venison Steak | 2 | common | 10 | 0 | — |
| `twilightforest:cooked_meef` | Стейк із мінотавра / Meef Steak | 2 | common | 10 | 0 | — |
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
| `minecraft:arrow` | Стріла / Arrow | 4 | common | 10 | 0 | — |
| `minecraft:leather` | Шкіра / Leather | 1 | common | 10 | 0 | — |
| `minecraft:string` | Нитка / String | 2 | common | 10 | 0 | — |
| `minecraft:lapis_lazuli` | Лазурит / Lapis Lazuli | 2 | common | 10 | 0 | — |
| `minecraft:iron_ingot` | Залізний злиток / Iron Ingot | 1 | common | 10 | 0 | — |
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
| `minecraft:emerald` | Смарагд / Emerald | 1 | common | 10 | 0 | — |
| `minecraft:gold_ingot` | Золотий зливок / Gold Ingot | 1 | common | 10 | 0 | — |
| `minecraft:golden_carrot` | Золота морква / Golden Carrot | 1 | common | 10 | 0 | — |
| `minecraft:experience_bottle` | Пляшечка досвіду / Bottle o’ Enchanting | 1 | common | 10 | 0 | — |
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
