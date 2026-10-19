# Локалізація / Localization

Кампанія SOQM **0.4.9** з доповненням Cardboard Box має український текст та англійський довідник цілей для alpha: **37 глав, 1626 квестів, 1773 задачі**. Книга має 251 тематичну секцію зі змістовними назвами обома мовами. Підписи на карті прив’язані до ID конкретного квесту: переміщення вузла не може запозичити назву сусіда.

У 0.4.7 прибрано 1453 повторювані вступи, скорочено 1525 назв і вилучено 2287 застарілих абзаців із поверненнями нагород, списками підготовки та патчнотами рецептів. Корисні спеціальні пояснення, зокрема сплави Foundry, збережено. Єдина пам’ятка керування міститься на початку книги.

У поточному доповненні **269 квестів виробничих гілок** мають авторські двомовні описи в
`metadata/quest_branches/*.json`. Разом із попередніми уроками це **286
різних квестів з авторськими уроками**: нові описи перекривають 50 із
попередніх 67 уроків. Первинні джерела попереднього оформлення —
`metadata/campaign_tutorials.json` і `metadata/campaign_route.json` —
залишаються для інших глав і матеріальних рубежів.

Гілки пояснюють ручне й автоматичне просіювання, виходи Flux-машин,
ME Chest, 1k-автокрафт, зовнішню обробку, канали й віддалений доступ,
машинні рами, паливні цикли та ферми. Умови 16 спеціальних автоматичних
і трьох ручних інженерних задач також перекладено вручну. Наявне
досягнення ME перевіряє фактичний крафт через активний термінал із
непорожнім сховищем; предметні уроки прямо описують межі інвентарної перевірки.

Решта англійських цілей виводиться з фактичних задач. Назви предметів і досягнень надходять із закріплених ресурсів модів, наявних авторських ресурсів або читабельного registry ID. Словник `metadata/quest_english_names.json` містить лише використані назви та SHA-256 джерел. Повний художній переклад залишено поза заявленим обсягом англійського довідника.

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
canvas caption. All **251 section names** and **286 distinct authored
tutorials** have editorial translations. The 269 production-branch lessons
override 50 of the previous 67 core lessons and teach actual machine setup,
inputs, outputs and trial runs. Source fragments live in
`metadata/quest_branches/*.json`; both languages share the same quest IDs.
The 14 reward themes, six production milestones, all 326 prize entries and
read-only commands are bilingual. Current per-draw odds and locked requirements
are shown by the reward commands, while the native FTB tooltip names the pack.
The 16 custom automatic checks, three manual engineering checks and native
ME-craft advancement retain explicit English conditions. Other objectives
derive their quantities and conditions from actual tasks.

The single introductory controls guide explains inventory detection and JEI. Repeated patch notes and per-quest R/U reminders have been removed. Registry names are retained in the offline name lexicon; unknown display names fall back to readable identifiers. This is an alpha objective guide with authored core lessons; a full literary translation and graphical client review remain separate work.

The commands above regenerate and validate the campaign, locale and payload lock. Existing quest, task, reward and image IDs stay stable. See [campaign update](CAMPAIGN_UPDATE.md) for migration and [native validation](RUNTIME_VALIDATION.md) for executable completion checks.
