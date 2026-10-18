# Локалізація / Localization

Український текст оновлено до кампанії 0.4.6 зі збереженням ID квестів. `en_us.snbt` — **англійський довідник цілей для alpha**, а не завершений художній переклад. Він охоплює 37 глав, 1625 квестів, 1772 задачі й усі ключі оформлення книги. Назви 37 глав і 7 груп перекладено вручну; підписи цілей і вимоги згенеровано з фактичних задач. Довгі підписи на полотні скорочуються; частина секцій поки має функціональні назви `Section N`.

Для 16 спеціальних автоматичних і 3 ручних інженерних задач англійські умови написано вручну за вихідним описом. Збережено різницю між наявністю предмета, крафтом, досягненням, дією у світі й ручним підтвердженням. У 272 квестах із приміткою до змінених рецептів додано вказівку перевірити рецепт SOQM у JEI через R/U. Числа й механіка беруться з задач; україномовні жарти не копіюються в англійський текст.

Назви предметів і досягнень беруться з англійських ресурсів закріплених модів, наявних авторських ресурсів або читабельного перетворення registry ID. У `metadata/quest_english_names.json` записано лише потрібні назви та SHA-256 джерел. Цей словник зберігається в репозиторії, тому звичайна повторна генерація працює без JAR і мережі.

```bash
python3 scripts/localize_english.py
python3 scripts/localize_english.py --check
```

Оновити словник з уже завантажених модів можна окремо:

```bash
python3 scripts/localize_english.py --mod-dir .cache/mods
```

Приймаються лише файли з SHA-256, що відповідає `metadata/mods.lock.json`. Відсутні локальні JAR пропускаються; для них залишається читабельний registry ID або наявне авторське ім’я. Після зміни словника переглянь diff і знову виконай `--check`. Скрипт не змінює `uk_ua.snbt`, ID, залежності, задачі, рецепти чи нагороди.

Потрібні подальша редактура назв, переклад авторського сюжету й гумору та графічна перевірка обох мов. Після зміни цілей, глав або спеціальних перевірок онови генератор; він навмисно завершується помилкою для невідомого типу задачі чи непокритої глави. Редагуй генератор і словник, а не згенерований файл.

## English scope

Ukrainian retains the full authored campaign. English currently provides an **alpha objective guide**, covering every chapter, quest, task and canvas-label key. It derives counts and conditions from actual task data, with hand-written explanations for all 16 custom automatic checks and 3 manual engineering checks. All 272 source recipe notes include a JEI R/U reminder.

This is not a complete editorial translation of the narrative and humor. Some canvas sections use `Section N`, long captions are shortened, and names without an available English resource fall back to readable registry identifiers. Native advancement goals refer to the actual advancement screen or mod guide. A graphical review of both locales remains required.

The generator preserves stable IDs, dependencies, recipes and rewards. Use the commands above to regenerate, verify freshness or refresh relevant display names from SHA-256-verified mod JARs. The committed name lexicon makes normal regeneration deterministic and offline.
