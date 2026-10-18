<p align="center"><img src="assets/banner.svg" alt="Shout Out to Questing Mayhem" width="100%"></p>

<p align="center"><b>Minecraft 1.21.1 · NeoForge 21.1.262 · Java 21 · Expert · Alpha</b></p>

**Shout Out to Questing Mayhem (SOQM)** — Expert-збірка про виробничі ланцюги,
магію та дослідження. 1625 квестів у 37 главах ведуть від першої майстерні до
великих автоматизованих систем. Основа — Questing Mayhem Expert 0.4.4;
SOQM оформлює її як окремий відтворюваний проєкт із власними правилами та релізами.

**Версія 0.1.0-alpha.1.** Це перша публічна альфа. Повне проходження, клієнтська
продуктивність і довгостроковий баланс ще потребують ігрового тестування.
Свіжа серверна перевірка пройшла: старт, reload 20 074 → 20 074 рецепти,
0 помилок KubeJS та штатне завершення. [Обсяг і результати перевірки](reports/README.md).

## Грати

Завантаж `SOQM-0.1.0-alpha.1-curseforge.zip` із папки
[`downloads/`](downloads/) та імпортуй його як новий профіль у сумісний лаунчер.
Архів **Source code** від GitHub для встановлення не підходить.
Потрібні придбана Minecraft Java Edition та Java 21.

[Встановлення українською](docs/INSTALL.uk.md) · [Install in English](docs/INSTALL.en.md)

- **Прогресія:** 3905 зв’язків між квестами; складні рецепти пов’язують модові системи.
- **Техніка:** Mekanism, Immersive Engineering, AE2, Ender IO, Industrial Foregoing.
- **Магія та світи:** Botania Revived, EvilCraft, DivineRPG, Aether, Twilight Forest, Ad Astra.
- **Mayhem:** тематичні куби, здобич, власні компоненти та квестові нагороди.
- **Мови:** українська кампанія та англійський довідник цілей. Повний художній
  переклад англійською ще в роботі; [точний обсяг локалізації](docs/LOCALIZATION.md).

## Розробка

Потрібні Python 3.11+ та Node.js 18+. Локальні перевірки й збірка:

```sh
./scripts/check
python3 scripts/soqm.py verify-downloads
```

Перший запуск працює без мережі й створює інсталяційний ZIP у `dist/`.
Друга команда окремо перевіряє доступність і хеші 111 зафіксованих модів,
зберігаючи їх лише в локальному кеші. Дивись [порядок релізу](docs/RELEASE.uk.md)
та [план ігрового тестування](docs/PLAYTEST.uk.md).

`overrides/` містить ігрові налаштування, квести та скрипти; `src/compat/` —
джерела восьми власних модулів. `manifest.json` і `metadata/` фіксують залежності
та контрольні суми. Світи, акаунти, карти й сторонні модові JAR не зберігаються
в репозиторії. Свіжі результати перевірок — у [`reports/`](reports/).

## English

SOQM is an Expert Minecraft campaign built around interconnected production,
magic and exploration: **1,625 quests across 37 chapters**, with 111 pinned
third-party mods and eight small compatibility modules. Import the installation
ZIP from `downloads/` into a new launcher profile. This is an **alpha**;
full playthrough, client performance and long-term balance are not yet verified.
The Ukrainian narrative is complete; English provides an objective guide.

## Авторство

Автор SOQM: **lsanarchist**. Craftoria — орієнтир якості оформлення та зручності.
Questing Mayhem — натхнення для назви. Проєкт не пов’язаний із цими командами.

[Моди й автори](MODLIST.md) · [Ліцензії та походження](THIRD_PARTY_NOTICES.md) ·
[Зміни](CHANGELOG.md) · [Ліцензія проєкту](LICENSE)

Not an official Minecraft product. Not approved by or associated with Mojang or Microsoft.
