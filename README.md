<p align="center"><img src="assets/banner.svg" alt="Shout Out to Questing Mayhem" width="100%"></p>

<p align="center"><b>SOQM 0.4.8 · Minecraft 1.21.1 · NeoForge 21.1.262 · Java 21 · Expert · Alpha</b></p>

**Shout Out to Questing Mayhem (SOQM)** — Expert-кампанія про виробничі ланцюги,
магію та дослідження. **1625 квестів у 37 главах** ведуть від першої майстерні
до великих автоматизованих систем. Основа — Questing Mayhem Expert **0.4.6**;
SOQM розвиває власні рецепти, квести, баланс та відтворюваний процес збірки.

**У 0.4.8 нагороди розвиваються разом із виробництвом команди:** 14 тематичних
пулів містять **326 варіантів — 240 різних предметів, серед них 210 модових**.
Кожен учасник забирає власний пакунок із **2–4 випадковими призами** раз за квест.
Шасі, схеми, процесори, бочки й дорогі поліпшення починають випадати після
справжнього виготовлення командою саме цього предмета. Пластик і HDPE виключені.
Поточний рівень показує `/soqm rewards`, склад і шанси — `/soqm rewards pool <тема>`.

Збережено оформлення 0.4.7: головний маршрут стоїть першим, матеріальні рубежі
мають виразні вузли, а додаткові гілки — окремі позначки.
Перша компактна AE2-мережа й 1k-автокрафт доступні після точного механізму;
космічне спорядження вивчається до першого польоту. Книга містить **67 основних
уроків українською й англійською** та 251 іменовану секцію.

[Зміни кампанії](docs/CAMPAIGN_UPDATE.md) · [Пули нагород](docs/REWARDS.md) ·
[Changelog](CHANGELOG.md) · [Стан перевірок](reports/README.md)

## Грати

У pull request або вкладці **Actions → Validate and package** відкрий успішний
запуск для потрібного коміту. Артефакт `SOQM-candidate-<commit>` містить
інсталяційний ZIP і `SHA256SUMS`. Розпакуй зовнішній архів GitHub, потім імпортуй
`SOQM-0.4.8-curseforge.zip` у сумісний із CurseForge manifest лаунчер.
Артефакти CI зберігаються 14 днів; їх можна відтворити з того самого коміту.

Локальна підготовка:

```sh
./scripts/check
python3 scripts/soqm.py verify-downloads
```

`./scripts/check` створює `dist/SOQM-0.4.8-curseforge.zip` та контрольну суму.
Друга команда окремо звіряє всі 122 сторонні залежності з їхніми SHA-256.
Потрібні Minecraft Java Edition та Java 21. GitHub **Source code** містить
репозиторій; імпортувати в лаунчер слід зібраний інсталяційний ZIP.

[Встановлення українською](docs/INSTALL.uk.md) · [Install in English](docs/INSTALL.en.md) ·
[Поточні й історичні завантаження](downloads/README.md)

### Оновлення світу

Збережено всі наявні ID квестів, задач, нагород, глав та підписів і виконані задачі.
**Колишні спільні нагороди тепер дають кожному одну нову особисту спробу за вже
виконані квести.** Старі персональні отримання, зокрема вступна книга, зберігаються.
Надалі перехід між командами не відновлює використану особисту спробу.

Історії виробництва у старих світах не було: після оновлення виготов один новий
зразок потрібного предмета. Отримання з квесту, підбирання й наявність у сховищі
не відкривають виробничий приз. Команда ділиться відкриттями; після виходу
гравець зберігає власні виготовлені предмети, а колишня команда — свій доробок.
Перед оновленням збережи копію світу й профілю та заміни файли збірки повністю.
Деталі — у [поясненні міграції](docs/CAMPAIGN_UPDATE.md#оновлення-наявного-світу).

## Що перевірено

Статус збірки — **alpha**. Автоматичні перевірки охоплюють незмінність
збережених ID, граф залежностей, матеріальні обмеження, пули, локалізацію,
геометрію карти та інсталяційний архів. Окрема перевірка на справжньому
NeoForge/FTB Quests/AE2/Mekanism перевіряє особисту видачу нагород, міграцію
старого прогресу, справжнє виготовлення, командну належність, збереження та reload.

Підсумки й точні межі перевірки записано в [reports](reports/README.md) та
[інструкції відтворення](docs/RUNTIME_VALIDATION.md). Повне проходження,
графічний клієнт, тривала економіка та вимірювання продуктивності потребують
ігрової перевірки. Збережено актуальне виправлення Cannibal 0.4.1.

## Розробка

Потрібні Python 3.11+ та Node.js 18+. Звичайна перевірка не завантажує
сторонні JAR. Після редагування уроків, карти або політики нагород:

```sh
python3 scripts/campaign_refresh.py
python3 scripts/localize_english.py
python3 scripts/render_campaign.py
python3 scripts/soqm.py lock
./scripts/check
```

`overrides/` містить ігровий вміст, `src/compat/` — джерела 12 авторських
модулів, `metadata/` — версії, хеші й редаговані правила кампанії. Прев’ю карт
генеруються з фактичних координат; вони показують структуру й не замінюють
огляд інтерфейсу в грі. Сторонні JAR, світи, тестовий мод та кеші залишаються
поза інсталяційним архівом.

[Локалізація](docs/LOCALIZATION.md) · [Порядок релізу](docs/RELEASE.uk.md) ·
[План ігрових перевірок](docs/PLAYTEST.uk.md) ·
[Оптимізація генерації та фонової роботи](docs/PERFORMANCE_OPTIMIZATION.md)

## English

SOQM **0.4.8** is an Expert Minecraft campaign with **1,625 quests in 37 chapters**,
**122 pinned third-party mods** and **12 authored compatibility modules**.
Its **14 original themed reward pools** contain **326 entries covering 240
distinct items, including 210 modded items**. Every member claims a personal
pack once per quest, receiving **2–4 independent draws**. Six manufactured
campaign milestones increase draw counts and rare-prize weights. Expensive
chassis, circuits, processors, barrels and upgrades require actual production
of that exact item by the team; inventory possession and reward delivery do
not unlock them. Plastic and HDPE remain excluded. Inspect the current level
with `/soqm rewards` and live per-draw odds with `/soqm rewards pool <theme>`.

The Main Route remains the first chapter. Distinct map shapes identify finale
prerequisites, mod development and optional branches. Fourteen selected
milestones retain one Mayhem Cube for each member.

Early AE2 preparation precedes processors and Precision Mechanism; a compact
1k autocrafting setup is available at stage 2, while larger components retain
the matrix gate. Space preparation precedes the first Moon landing. English
includes **67 authored core tutorials**, all 251 section names and generated
objectives for the rest of the campaign.

Download the installation candidate and checksum from a successful **Validate
and package** workflow for the desired commit, or build locally with the commands
above. See the [installation guide](docs/INSTALL.en.md),
[native validation](docs/RUNTIME_VALIDATION.md) and [current evidence](reports/README.md).
Existing quest progress and IDs are preserved. **Previously shared rewards
intentionally become one new personal entitlement per member**, while old
personal claims remain claimed. Leaving or joining teams does not reset a used
personal claim. Existing worlds need a new manufactured sample to establish
production evidence. Full graphical playthrough, long-term multiplayer balance
and performance measurements remain alpha validation work.

## Авторство

Автор SOQM: **lsanarchist**. Craftoria — орієнтир якості оформлення та зручності;
пули SOQM мають власний склад і баланс. Questing Mayhem — натхнення для назви.
Проєкт не пов’язаний із цими командами.

[Моди й автори](MODLIST.md) · [Ліцензії та походження](THIRD_PARTY_NOTICES.md) ·
[Ліцензія проєкту](LICENSE)

Not an official Minecraft product. Not approved by or associated with Mojang or Microsoft.
