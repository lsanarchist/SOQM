# Локалізація / Localization

Кампанія SOQM **0.4.9** з доповненням Cardboard Box має український текст та англійський довідник цілей для alpha: **37 глав, 1626 квестів, 1773 задачі**. Книга має 251 тематичну секцію зі змістовними назвами обома мовами. Підписи на карті прив’язані до ID конкретного квесту: переміщення вузла не може запозичити назву сусіда.

У 0.4.7 прибрано 1453 повторювані вступи, скорочено 1525 назв і вилучено 2287 застарілих абзаців із поверненнями нагород, списками підготовки та патчнотами рецептів. Корисні спеціальні пояснення, зокрема сплави Foundry, збережено. Єдина пам’ятка керування міститься на початку книги.

Оновлення виробничих гілок дало **269 авторських двомовних описів** у
`metadata/quest_branches/*.json`: разом із попередніми уроками — 286 різних
квестів, оскільки 50 описів перекрили попередні 67 уроків. Поточне редагування
додає ще **286 двомовних уроків**, зокрема повний англійський переклад 35
наявних українських уроків Modern Foundry. Тепер **572 різні квести** мають
авторські описи обома мовами. Первинні джерела попереднього оформлення —
`metadata/campaign_tutorials.json` і `metadata/campaign_route.json` —
залишаються для інших глав і матеріальних рубежів.

Нові джерела в `metadata/tutorial_expansions/*.json` містять лише описи та
посилання на перевірені рецепти/довідники. `scripts/campaign_lessons.py`
накладає їх після базових генераторів, перевіряє двомовність, унікальність
власника й наявність ID квесту. Цей формат не дозволяє змінювати задачі,
залежності чи нагороди. Докладний обсяг і перевірки — в
[оновленні уроків](QUEST_LESSONS_AND_OBSERVER.md).

Гілки пояснюють ручне й автоматичне просіювання, виходи Flux-машин,
ME Chest, 1k-автокрафт, зовнішню обробку, канали й віддалений доступ,
машинні рами, паливні цикли та ферми. Умови 16 спеціальних автоматичних
і трьох ручних інженерних задач також перекладено вручну. Наявне
досягнення ME перевіряє фактичний крафт через активний термінал із
непорожнім сховищем; предметні уроки прямо описують межі інвентарної перевірки.

Решта англійських цілей виводиться з фактичних задач. Назви предметів і досягнень надходять із закріплених ресурсів модів, наявних авторських ресурсів або читабельного registry ID. Словник `metadata/quest_english_names.json` містить лише використані назви та SHA-256 джерел. Повний художній переклад залишено поза заявленим обсягом англійського довідника.

## Файли перекладів за главами

У Git переклади зберігаються невеликими JSON-файлами в
`metadata/quest_locales/uk_ua/` та `metadata/quest_locales/en_us/`.
Назва файла відповідає файлу глави; він містить тексти самої глави та її
квестів, задач, нагород і підписів. `_global.json` містить решту записів,
зокрема загальні назви книги й груп глав. Власник визначається за стабільним
ID об'єкта. Об'єднання відхиляє дублікати ключів; тексти й списки абзаців
зберігаються повністю, без скорочення.

Закріплена FTB Quests **2101.1.36** читає один файл `lang/<мова>.snbt` на
мову. Її [TranslationManager](https://github.com/FTBTeam/FTB-Quests/blob/v2101.1.36/common/src/main/java/dev/ftb/mods/ftbquests/quest/translation/TranslationManager.java)
переглядає лише верхній рівень `lang/`, а всю назву файла до `.snbt`
вважає кодом мови. Тому частини зберігаються в метаданих репозиторію.
Команди `soqm.py lock` і пакування об'єднують їх у пам'яті; інсталяційний
ZIP та підготовлений інстанс отримують звичайні `lang/uk_ua.snbt` і
`lang/en_us.snbt`. Ці два великі файли не відстежуються Git і не потрібні
на диску у вихідній копії для перевірки чи збирання.

Генератори оновлюють частини за главами. Для запуску гри використовуй
зібраний інсталяційний ZIP або сценарій підготовки інстансу, який об'єднує
переклади; просте копіювання `overrides/` із вихідної копії їх не створює.

Порядок оновлення:

```bash
python3 scripts/campaign_refresh.py
python3 scripts/localize_english.py
python3 scripts/render_campaign.py
python3 scripts/soqm.py lock
./scripts/check
```

Обидва генератори мають `--check`: він перевіряє свіжість і нічого не змінює. Редагуй метадані уроків, схему карти або політику нагород, потім запускай генератори. Вони перевіряють стабільні ID; нові задачі й переклади невідомих типів потребують явного розширення схеми.

Для оновлення словника з уже завантажених JAR:

```bash
python3 scripts/localize_english.py --mod-dir .cache/mods
```

Приймаються лише SHA-256-узгоджені файли з `metadata/mods.lock.json`. Звичайна генерація працює без мережі й без JAR. Назви 14 пулів, шести рівнів і всіх 326 позицій нагород перекладено вручну в `metadata/reward_pools.json`. У FTB Quests показано назву й іконку особистого пакунка; актуальний склад, шанси та умови відкриття показують `/soqm rewards` і `/soqm rewards pool <тема>` мовою гравця. Початок книги пояснює особисте отримання, виробничі умови й міграцію старого світу обома мовами. Розмір і розташування підписів перевіряються автоматично; остаточну читабельність обох мов треба оглянути у графічному клієнті.

## Apotheosis: порожні підказки світових рівнів

У закріпленій Apotheosis **8.9.0** українські описи світових рівнів, навчання та
вимоги розблокування вже є. Порожня рамка підказки на екрані «Притулок» пов'язана
з обрізанням тексту: підказки піктограм малюються перед кнопкою активації, а
довгий напис цієї кнопки вмикає область відсікання, коли текст підказки ще
перебуває в буфері відображення.

Кнопка має ширину 60 px і внутрішні відступи по 2 px — для тексту залишається
**56 px**. Ресурс `overrides/kubejs/assets/apotheosis/lang/uk_ua.json` замінює
лише два підписи. Ширини обчислені за стандартним растровим шрифтом Minecraft
1.21.1:

| Ключ | Початковий підпис | Ширина | Новий підпис | Ширина |
| --- | --- | --- | --- | --- |
| `button.apotheosis.activate` | Активувати | 59 px | Увімкнути | 49 px |
| `button.apotheosis.activated` | Активовано | 59 px | Активно | 41 px |

Повні описи та вимоги продовжують надходити з ресурсів Apotheosis. Це обхід
проблеми через локалізацію для стандартного шрифту; ресурсні пакети з іншими
шрифтами можуть змінити ширини. Порядок відображення та розмір кнопки звірено
з класами закріпленого JAR, але графічний клієнт із цією правкою ще не перевірено.

Для графічної перевірки вибери українську мову і стандартний шрифт без
примусового Unicode. На екрані світових рівнів наведи курсор на відкриті й
заблоковані піктограми, коли кнопка показує «Активно», а потім — «Увімкнути».
Текст підказок має бути видимим в обох станах; перевір також кнопку «Деталі».

Джерела: [SimpleTexButton](https://github.com/Shadows-of-Fire/Apotheosis/blob/1.21/src/main/java/dev/shadowsoffire/apotheosis/client/SimpleTexButton.java),
[WorldTierSelectScreen](https://github.com/Shadows-of-Fire/Apotheosis/blob/1.21/src/main/java/dev/shadowsoffire/apotheosis/client/WorldTierSelectScreen.java),
[споріднений upstream-звіт про кирилицю](https://github.com/Shadows-of-Fire/Apotheosis/issues/1756).

## English scope

English covers every chapter, quest, task, personal reward-pack title and
canvas caption. All **251 section names** and **572 distinct authored
tutorials** have editorial translations. The 269 production-branch lessons
override 50 of the previous 67 core lessons and teach actual machine setup,
inputs, outputs and trial runs. Source fragments live in
`metadata/quest_branches/*.json`. Another **286 bilingual lessons** live in
`metadata/tutorial_expansions/*.json`, including full English translations of
all 35 existing Ukrainian Modern Foundry lessons. These description-only
overlays run last in both generators; both languages share the same quest IDs.
Generic single-paragraph inventory descriptions decreased from 1,022 to 763.
The 14 reward themes, six production milestones, all 326 prize entries and
read-only commands are bilingual. Current per-draw odds and locked requirements
are shown by the reward commands, while the native FTB tooltip names the pack.
The 16 custom automatic checks, three manual engineering checks and native
ME-craft advancement retain explicit English conditions. Other objectives
derive their quantities and conditions from actual tasks.

The single introductory controls guide explains inventory detection and JEI. Repeated patch notes and per-quest R/U reminders have been removed. Registry names are retained in the offline name lexicon; unknown display names fall back to readable identifiers. This is an alpha objective guide with authored core lessons; a full literary translation and graphical client review remain separate work.

The commands above regenerate and validate the campaign, locale and payload lock. Existing quest, task, reward and image IDs stay stable. See [campaign update](CAMPAIGN_UPDATE.md) for migration and [native validation](RUNTIME_VALIDATION.md) for executable completion checks.

## Locale files by chapter

Git stores the translations as small JSON files under
`metadata/quest_locales/uk_ua/` and `metadata/quest_locales/en_us/`.
Each chapter file owns the text for that chapter and its quests, tasks,
rewards and captions, using their stable object IDs. `_global.json` holds
the remaining entries, including book and chapter-group names. Merging
rejects duplicate keys and preserves every string and paragraph list.

The pinned FTB Quests **2101.1.36**
[TranslationManager](https://github.com/FTBTeam/FTB-Quests/blob/v2101.1.36/common/src/main/java/dev/ftb/mods/ftbquests/quest/translation/TranslationManager.java)
scans only the top level of `lang/` and treats each basename as a locale.
The repository fragments therefore live outside the game payload. The
existing generators update them; `soqm.py lock`, packaging and instance
staging merge them into the normal `lang/uk_ua.snbt` and `lang/en_us.snbt`
runtime files. These two large generated files are not tracked by Git and
do not need to exist on disk for source validation or packaging.

Use the built installation ZIP or instance-staging tooling to run the pack.
Copying only the repository's `overrides/` directory omits the generated
quest translations. The regeneration commands above remain unchanged.
