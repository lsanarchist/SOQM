# Перехід на Sophisticated Storage у SOQM 0.4.10

Iron Chests вилучено з нового manifest. Залишаються **124 сторонні моди**;
Sophisticated Storage 1.6.7.2184 і Sophisticated Backpacks зберігаються. Серед
закріплених решти модів немає обов’язкової залежності від Iron Chests.

## Нове проходження

Глава 11 навчає однієї системи скринь: дерев’яна скриня з глави 37 → залізна →
золота → діамантова → незеритова. Мідь — додатковий ранній варіант. Stack Upgrade
має окрему ранню задачу, оскільки розмір стака й кількість комірок — різні
покращення. Глава 37 продовжує навчання комплектами Tier Upgrade, Controller,
функціональними модулями та рюкзаками.

Шість колишніх задач мають ті самі quest/task/reward ID. Уже отримані особисті
нагороди не видаються повторно, завершені квести не скидаються. Незавершені
предметні задачі тепер приймають відповідні предмети Sophisticated Storage.

| Старе завдання | Новий предмет |
|---|---|
| Copper Chest | Sophisticated Storage Copper Chest |
| Iron Chest | Sophisticated Storage Iron Chest |
| Gold Chest | Sophisticated Storage Gold Chest |
| Diamond Chest | Sophisticated Storage Diamond Chest |
| Crystal Chest | Sophisticated Storage Stack Upgrade Tier 1 |
| Obsidian Chest | Sophisticated Storage Netherite Chest |

ME Drive використовує `sophisticatedstorage:iron_to_gold_tier_upgrade`, а Matter
Condenser — `sophisticatedstorage:gold_to_diamond_tier_upgrade`. Це комплекти без
власного інвентарю: звичайний рецепт машини не може забрати заповнену скриню.
Рідні serializers для покращення самих скринь і подвійних скринь залишено.
Дванадцять рецептів і старі EMC-записи Iron Chests вилучено.

## Наявний світ: виконати на старій версії

**Автоматичної конвертації блоків та їхнього вмісту немає. Не відкривай єдиний
примірник старого світу після вилучення Iron Chests.**

1. Закрий гру або сервер, зроби копію всього старого інстансу разом зі світом.
2. Відкрий робочу копію старою версією SOQM, у якій Iron Chests ще встановлено.
3. Перенеси вміст скринь у Sophisticated Storage або інші контейнери, які
   залишаються. Перевір кількість предметів, фільтри та підключені труби.
4. Прибери старі скрині й предмети Iron Chests, включно з комплектами покращення,
   trapped-варіантами, переносними сховищами, іншими вимірами й інвентарями
   всіх гравців. Перевір також блоки, заховані в коробках або інших переносниках.
5. Збережи світ, знову закрий гру й створи окрему копію для перевірки. За
   наявності Python 3.11+ запусти з репозиторію:

   ```bash
   python3 scripts/audit_storage_upgrade.py --world /path/to/stopped-world-backup
   ```

   У ZIP кандидата той самий скрипт є в `tools/audit_storage_upgrade.py`;
   для нього не потрібно встановлювати моди чи сторонні бібліотеки Python.

6. Якщо знайдено посилання, повернися до старого інстансу й перевір зазначені
   файли/чанки. Після чистої перевірки встанови повний кандидат 0.4.10 у новий
   профіль, перенеси підготовлену копію світу й перевір сховища, рецепти та квести.

Перевірка **тільки читає** NBT гравців, дані світу й region/entity-файли, зокрема
інші виміри та зовнішні `.mcc` чанки. `ready_for_removal: true` означає, що
підтримані записи прочитано й рядків `ironchest:` не знайдено. Це консервативний
пошук посилань, а не переписування NBT чи доказ сумісності будь-якого мода.
Збережені текстові згадки або невикористані палітри теж можуть потребувати
ручної перевірки. Невідома компресія, пошкоджений файл чи зміна даних під час
читання дають `complete: false`, а не удаваний успіх.

Коди завершення: **0** — посилань не знайдено; **2** — є старі посилання;
**3** — перевірка неповна. Скрипт не видаляє предмети, не конвертує скрині та
не змінює світ. Для відкату використовуй початкову копію старого інстансу.

## English upgrade notes

0.4.10 removes Iron Chests. Before upgrading, back up the complete old instance,
run that copy with its old mod set, transfer every legacy chest's contents, and
remove legacy blocks/items from every dimension and player inventory. Stop the
game and run the read-only audit above against a stable backup. The candidate
ZIP also includes the same standalone script at `tools/audit_storage_upgrade.py`.
A clean scan
does not convert containers; unknown formats fail closed. Import the complete
new pack into a separate profile and test the prepared world copy. Preserve the
original backup for rollback.

The six quest slots are retargeted to Sophisticated Storage without changing
quest/task/reward IDs. Existing claims remain claimed. ME Drive and Matter
Condenser consume inventory-free tier upgrade kits, preventing an ordinary
machine recipe from consuming a filled storage item. Native Sophisticated
Storage upgrade serializers are retained for actual container upgrades.

## Sources and review boundary

- Exact pins: [`metadata/mods.lock.json`](../metadata/mods.lock.json).
- Retired mod pin and reviewed quest mapping:
  [`metadata/storage_migration.json`](../metadata/storage_migration.json).
- Native recipes inside `sophisticatedstorage-1.21.1-1.6.7.2184.jar`, SHA-256
  `377f733da3c155d82cfad2f7e546a216ed331bdaeb1b74f1eee55aac2e460af6`;
  [official file](https://www.curseforge.com/minecraft/mc-mods/sophisticated-storage/files/9201424).
- Authored recipes: `qm_expert_v042.js`, `qm_expert_progression.js`,
  `qm_expert_v043.js`; tests: `tests/test_storage_upgrade.py`.

No real player save was supplied or changed during this update.
