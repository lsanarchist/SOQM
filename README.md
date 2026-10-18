<p align="center"><img src="assets/banner.svg" alt="Shout Out to Questing Mayhem" width="100%"></p>

<p align="center"><b>SOQM 0.4.6 · Minecraft 1.21.1 · NeoForge 21.1.262 · Java 21 · Expert · Alpha</b></p>

**Shout Out to Questing Mayhem (SOQM)** — Expert-збірка про виробничі ланцюги,
магію та дослідження. **1625 квестів у 37 главах** ведуть від першої майстерні
до великих автоматизованих систем. Поточна основа — Questing Mayhem Expert
**0.4.6**; SOQM зберігає власне оформлення, політики та відтворюваний процес збірки.

У 0.4.6 зменшено випадкові запаси у скринях, прибрано пластик/HDPE з випадкового
луту, оновлено 280 квестових іконок і поліпшено українські пояснення поступу.
Рецепти цього оновлення та стабільні ID квестів, задач і нагород збережено.
[Зміни](CHANGELOG.md) · [Стан перевірок](reports/README.md)

**Статус — alpha.** Свіжа серверна перевірка 0.4.6 пройшла: старт,
reload **20 077 → 20 077 рецептів**, нуль помилок KubeJS і штатне завершення.
Повне проходження, графічний клієнт і довгостроковий баланс
потребують окремої перевірки. Українська містить авторську кампанію; англійська —
довідник цілей, а не завершений художній переклад.

## Грати

Поточний інсталяційний пакет створюється **локально**:

```sh
./scripts/check
python3 scripts/soqm.py verify-downloads
python3 scripts/soqm.py build
```

Результат: `dist/SOQM-0.4.6-curseforge.zip` та контрольна сума в `dist/`.
Імпортуй цей ZIP як новий профіль у сумісний із CurseForge manifest лаунчер.
Для MultiMC дивись окремі застереження щодо ручного встановлення в інструкції.
Потрібні придбана Minecraft Java Edition та Java 21. GitHub `Source code`
не є інсталяційним пакетом; ZIP 0.4.6 ще не опубліковано як окремий asset.

[Встановлення українською](docs/INSTALL.uk.md) · [Install in English](docs/INSTALL.en.md) ·
[Поточні та історичні завантаження](downloads/README.md)

- **Expert:** складні рецепти пов’язують промисловість, магію та матеріали вимірів.
- **Техніка:** Mekanism, Immersive Engineering, AE2, Ender IO, Industrial Foregoing.
- **Світи:** DivineRPG, Aether, Twilight Forest, Ad Astra та додаткові експедиції.
- **Mayhem:** тематичні куби, власні компоненти та одноразові квестові нагороди.
- **Мови:** українська кампанія та англійський довідник; [межі локалізації](docs/LOCALIZATION.md).

## Розробка

Потрібні Python 3.11+ та Node.js 18+. `./scripts/check` виконує локальні перевірки
й упаковку; `verify-downloads` окремо перевіряє доступність і хеші **122**
закріплених сторонніх модів та зберігає їх у локальному кеші. Початкові 111
залежностей залишено, додано 11. Пакет також використовує **10** авторських
модулів сумісності: початкові вісім і модулі мобів та компактного луту скринь.

`overrides/` містить ігрові налаштування, квести та скрипти; `src/compat/` —
джерела модулів. `manifest.json`, `soqm.json` і `metadata/` фіксують версії та
походження. Генеровані архіви, сторонні JAR, кеші й JSON-звіти залишаються
локальними. GitHub використовується для контролю версій; поточна підготовка
не означає публікацію нового пакета.

[Порядок релізу](docs/RELEASE.uk.md) · [План ігрових перевірок](docs/PLAYTEST.uk.md)

## English

SOQM **0.4.6** is an Expert Minecraft campaign with **1,625 quests in 37 chapters**,
**122 pinned third-party mods** and **10 authored compatibility modules**.
This update brings leaner chest loot, removes random plastic/HDPE, replaces
280 quest icons and improves Ukrainian progression guidance. Existing quest,
task and reward IDs remain stable. English provides an alpha objective guide;
an editorial translation of the narrative and humor remains pending.

Build `dist/SOQM-0.4.6-curseforge.zip` locally using the commands above and follow
the [installation guide](docs/INSTALL.en.md). The 0.4.6 artifact has not been
published separately. Fresh 0.4.6 dedicated-server startup, reload
(**20,077 → 20,077 recipes**, zero KubeJS errors) and clean shutdown passed.
Full graphical playthrough, long-term balance and client performance remain
unverified. The historical 0.1.0-alpha.1 download is a separate version.

## Авторство

Автор SOQM: **lsanarchist**. Craftoria — орієнтир якості оформлення та зручності.
Questing Mayhem — натхнення для назви. Проєкт не пов’язаний із цими командами.

[Моди й автори](MODLIST.md) · [Ліцензії та походження](THIRD_PARTY_NOTICES.md) ·
[Ліцензія проєкту](LICENSE)

Not an official Minecraft product. Not approved by or associated with Mojang or Microsoft.
