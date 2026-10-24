# AE2 recipe transfer in JEI / Перенесення рецептів AE2 через JEI

SOQM **0.4.20** · Minecraft **1.21.1** · NeoForge **21.1.262**

## Українською

### Причина та виправлення

У попередньому клієнтському manifest були AE2 19.2.18 та JEI 19.64.0.455,
але не було **AE2 JEI Integration**. У цій гілці AE2 обробник JEI винесений
в окремий додаток. Без нього JEI не має зареєстрованого способу заповнити
сітку термінала створення ME, тому кнопка **«Перемістити предмети» (+)**
відсутня. Автори описують призначення додатка на
[офіційній сторінці](https://www.curseforge.com/minecraft/mc-mods/ae2-jei-integration);
джерела конкретних версій наведені нижче.

SOQM 0.4.20 додає **AE2 JEI Integration 1.2.1** як клієнтську залежність.
У manifest тепер 130 сторонніх модів; попередні 129 версій збережені.
Додаток реєструє перенесення до звичайного й бездротового терміналів
створення AE2, а також інтеграцію термінала кодування шаблонів.

### Встановлення та використання

Новий профіль із повного інсталяційного ZIP 0.4.20 завантажує потрібний JAR
через manifest. Для наявного профілю повністю закрий Minecraft, завантаж
[`ae2jeiintegration-1.2.1.jar`](https://www.curseforge.com/minecraft/mc-mods/ae2-jei-integration/files/7727898)
до папки `mods/` **клієнта** та перезапусти гру. Кожному гравцю потрібен
додаток у його клієнтському профілі. Для цього виправлення сервер оновлювати
не потрібно: перенесення виконує вже наявний серверний код AE2.

1. Відкрий **термінал створення ME**, підключений до ME-мережі з живленням.
2. Поклади потрібні матеріали у сховище ME.
3. Наведи на бажаний предмет у JEI й натисни **R**.
4. Вибери потрібний рецепт категорії **«Створення» / Crafting**.
5. Натисни **+ / «Перемістити предмети»**, після чого забери результат
   із термінала.

**Кнопка + міститься у вікні рецепта JEI, відкритому поверх термінала.**
Це не постійна кнопка поруч із власною сіткою термінала. Рецепт має бути
придатним для сітки 3x3. Якщо матеріалів бракує, інтеграція показує нестачу.
Для розкладання вже наявних матеріалів не потрібні процесор автокрафту,
співпроцесори, постачальники шаблонів чи закодовані шаблони. Автоматичне
виробництво відсутніх компонентів залишається окремою можливістю AE2.

### Межі перевірки

Перевірено точний архів, його залежності та шлях реєстрації обробника у
джерелах. JAR не містить `data/`-ресурсів, рецептів, тегів або власних
мережевих пакетів; він використовує наявний пакет AE2. Це не є перевіркою
кнопки у запущеному графічному клієнті. Для неї залишено окремі
[контрольні дії](PLAYTEST.uk.md#перенесення-рецептів-ae2-через-jei):
інгредієнти лише у ME, рецепти із заданою формою та без неї, варіанти за
тегами, відсутні матеріали й підключення до сервера без клієнтського додатка.

## English

### Cause and fix

The preceding client manifest contained AE2 19.2.18 and JEI 19.64.0.455 but
omitted **AE2 JEI Integration**. This AE2 branch supplies its JEI handler
through that separate add-on. Without it, JEI has no registered transfer
handler for the ME Crafting Terminal, so **Move Items (+)** is absent. The
[official project description](https://www.curseforge.com/minecraft/mc-mods/ae2-jei-integration)
explains the add-on's purpose; exact-version sources are linked below.

SOQM 0.4.20 adds **AE2 JEI Integration 1.2.1** as a client dependency. The
manifest now contains 130 third-party mods, retaining the previous 129 version
pins. The add-on registers transfer for AE2's normal and wireless crafting
terminals and integration for the pattern encoding terminal.

### Installation and use

A fresh profile imported from the complete 0.4.20 installation ZIP downloads
the JAR through the manifest. For an existing profile, fully close Minecraft,
add [`ae2jeiintegration-1.2.1.jar`](https://www.curseforge.com/minecraft/mc-mods/ae2-jei-integration/files/7727898)
to the **client** profile's `mods/` directory, and restart the game. Every
player needs the add-on in their client profile. No server update is required
for this fix: existing AE2 server code performs the transfer.

1. Open the **ME Crafting Terminal** connected to a powered ME network.
2. Put the required materials in ME storage.
3. Hover the desired item in JEI and press **R**.
4. Select the desired recipe in the **Crafting** category.
5. Click **+ / Move Items**, then take the result from the terminal.

**The + button is inside the JEI recipe view opened over the terminal.** It
is not a permanent button next to the terminal's native crafting grid. The
recipe must fit a 3x3 grid. The integration reports missing materials. Moving
already available materials needs no crafting CPU, co-processors, pattern
providers or encoded patterns. Automatic production of missing components
remains a separate AE2 feature.

### Verification limits

The exact archive, its dependencies and its source registration path were
inspected. The JAR has no `data/` resources, recipes, tags or custom network
packets; it uses an existing AE2 packet. This does not establish the button's
behavior in a running graphical client. The separate
[acceptance checklist](PLAYTEST.en.md#ae2-recipe-transfer-in-jei) covers
materials stored only in ME, shaped and shapeless recipes, tag alternatives,
missing materials and connection to a server without the client add-on.

## Exact dependency / Точна залежність

| Field / Поле | Value / Значення |
| --- | --- |
| Official file / Офіційний файл | [CurseForge 1074338 / 7727898](https://www.curseforge.com/minecraft/mc-mods/ae2-jei-integration/files/7727898) |
| Filename / Назва файла | `ae2jeiintegration-1.2.1.jar` |
| Mod ID / Version | `ae2jeiintegration` / `1.2.1` |
| Runtime / Середовище | Minecraft 1.21.1 / NeoForge |
| Byte count / Байтів | 91710 |
| SHA-256 | `2989c5d762d3f14a58b4db6519daaff81e719dff3445a2b39328948ac23e548b` |
| License / Ліцензія | LGPL-3.0 |
| Required AE2 / Потрібна AE2 | `[19.0.22-beta,)`; SOQM: `19.2.18` |
| Required JEI / Потрібна JEI | `[19.19.3,)`; SOQM: `19.64.0.455` |
| SOQM installation side / Сторона встановлення | Client / Клієнт |

## Source evidence / Джерела

The add-on source revision declaring version 1.2.1 is
[`ce16a255305e95e0ad670ca2755cdf8202d0395d`](https://github.com/Tamaized/AE2-JEI-Integration/tree/ce16a255305e95e0ad670ca2755cdf8202d0395d).
AE2 tag `neoforge/v19.2.18` resolves to
[`db17504a86128fdf3dae31f5fb7a112a646e0b93`](https://github.com/AppliedEnergistics/Applied-Energistics-2/tree/db17504a86128fdf3dae31f5fb7a112a646e0b93).

| Evidence / Підтвердження | Source / Джерело |
| --- | --- |
| Required version ranges / Межі версій | [Add-on properties](https://github.com/Tamaized/AE2-JEI-Integration/blob/ce16a255305e95e0ad670ca2755cdf8202d0395d/gradle.properties) and [mod descriptor](https://github.com/Tamaized/AE2-JEI-Integration/blob/ce16a255305e95e0ad670ca2755cdf8202d0395d/src/main/resources/META-INF/neoforge.mods.toml) |
| Crafting-terminal registration / Реєстрація термінала | [JEIPlugin.registerRecipeTransferHandlers](https://github.com/Tamaized/AE2-JEI-Integration/blob/ce16a255305e95e0ad670ca2755cdf8202d0395d/src/main/java/tamaized/ae2jeiintegration/integration/modules/jei/JEIPlugin.java#L92) |
| Transfer delegates to AE2 / Передавання керування AE2 | [UseCraftingRecipeTransfer](https://github.com/Tamaized/AE2-JEI-Integration/blob/ce16a255305e95e0ad670ca2755cdf8202d0395d/src/main/java/tamaized/ae2jeiintegration/integration/modules/jei/transfer/UseCraftingRecipeTransfer.java#L98) |
| Existing native packet / Наявний пакет AE2 | [CraftingHelper](https://github.com/AppliedEnergistics/Applied-Energistics-2/blob/db17504a86128fdf3dae31f5fb7a112a646e0b93/src/main/java/appeng/integration/modules/itemlists/CraftingHelper.java#L43) and [InitNetwork](https://github.com/AppliedEnergistics/Applied-Energistics-2/blob/db17504a86128fdf3dae31f5fb7a112a646e0b93/src/main/java/appeng/core/network/InitNetwork.java#L73) |

The add-on can also be present on a server, but its descriptor then requires
JEI there. SOQM excludes it from dedicated-server staging because its client
transfer handler uses AE2's existing server packet. Its empty
[`@Mod` entrypoint](https://github.com/Tamaized/AE2-JEI-Integration/blob/ce16a255305e95e0ad670ca2755cdf8202d0395d/src/main/java/tamaized/ae2jeiintegration/AE2JEIIntegration.java)
is not annotated as client-only; the client classification is SOQM's installation
policy, not a claim about an upstream distribution annotation.

Додаток може бути й на сервері, але тоді його опис залежностей вимагає там
JEI. SOQM виключає його з підготовки виділеного сервера, бо клієнтський
обробник використовує вже наявний серверний пакет AE2. Порожня точка входу
`@Mod` не має позначки лише клієнта; клієнтська сторона — це правило
встановлення SOQM, а не твердження про анотацію автора.
