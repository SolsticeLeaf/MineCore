<h1 align="center">
  <img width=250 height=250 src="https://raw.githubusercontent.com/kiinse/MineCore/master/.github/img/logo.png"  alt=""/>
  <br>☕MineCore<br>
</h1>

<p align="center">
  <b>Библиотека для <code>SpigotMC 1.18 и выше</code>, которая направлена на улучшение кода и производительности плагинов</b><br><br>

  <a href="https://app.codacy.com/gh/kiinse/MineCore/dashboard">
    <img src="https://app.codacy.com/project/badge/Grade/04669f7c982b4ec8ba4783493dfb1ca9" alt="codacy"/>
  </a>

  <a href="https://repo.kiinse.me/#/releases/kiinse/me/plugins/minecore">
    <img src="https://repo.kiinse.me/api/badge/latest/releases/kiinse/me/plugins/minecore/MineCore?color=40c14a&name=Reposilite&prefix=v" alt="reposilite"/>
  </a>
  <a href="https://github.com/kiinse/MineCore/releases">
    <img src="https://img.shields.io/github/v/release/kiinse/MineCore?include_prereleases&style=flat-square" alt="release">
  </a>
  <a href="https://github.com/kiinse/MineCore/actions/workflows/gradle-package.yml">
    <img src="https://img.shields.io/github/actions/workflow/status/kiinse/MineCore/gradle-package.yml?style=flat-square" alt="build"> 
  </a>
  <a href="https://github.com/kiinse/MineCore">
    <img src="https://img.shields.io/github/repo-size/kiinse/MineCore?style=flat-square" alt="size"> 
  </a>
  <a href="https://github.com/kiinse/MineCore/releases">
    <img src="https://img.shields.io/github/downloads/kiinse/MineCore/total?style=flat-square" alt="downloads"> 
  </a>
  <a href="https://github.com/kiinse/MineCore/issues">
    <img src="https://img.shields.io/github/issues/kiinse/MineCore?style=flat-square" alt="issues"> 
  </a>
  <a href="https://github.com/kiinse/MineCore/blob/master/LICENSE">
    <img src="https://img.shields.io/github/license/kiinse/MineCore?style=flat-square" alt="licence"> 
  </a><br><br>
  <a href="#поддержка">Поддержка</a> •
  <a href="#особенности">Особенности</a> •
  <a href="#загрузка">Загрузка</a> •
  <a href="#использование">Использование</a> •
  <a href="#команды">Команды</a> •
  <a href="#конфиг">Конфиг</a>
</p>
<p align="center">
  <a href="https://github.com/kiinse/MineCore/blob/master/README.md">English</a> • <ins>Русский</ins>
</p>

## 🍩Помощь проекту

---> Если вы хотите поддержать данный проект, то, пожалуйста, просто поставьте звезду на данный репозиторий и расскажите о
MineCore друзьям =3

## ❓Поддержка

---> Заходите на [Discord](https://discord.gg/ec7y5NY82b) если у вас есть какие-либо вопросы.
Пожалуйста, **не** открывайте новые заявки в issues из-за простых вопросов.

## ➕Особенности

- Простая и удобная система локализации плагинов
- Простое создание интерактивного текста в сообщениях
- Улучшенная работа с командами
- Отслеживание статистики игроков
- Методы 'isWalking(player)' и 'isJumping(player)', которых очень сильно не хватало =)
- Простое управление таймерами от Bukkit
- Удобная работа с файлами и данными в них
- Проверка версии конфига
- Включение, выключение и перезагрузка плагинов, использующих данную библиотеку
- И многое другое

## ⬇️Загрузка

---> Последняя версия может быть найдена на <a href="https://github.com/kiinse/MineCore/releases">странице
релизов.</a><br>

## 📲Установка

---> Для того чтобы MineCore работал - он должен быть помещён в **папку с плагинами**.

### Maven

```xml
<repositories>
  <repository>
    <id>minecore</id>
    <url>https://repo.kiinse.me/releases</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>kiinse.me.plugins.minecore</groupId>
    <artifactId>MineCore</artifactId>
    <version>ЗДЕСЬ_УКАЗАТЬ_ВЕРСИЮ</version>
    <scope>provided</scope>
  </dependency>
</dependencies>
```

### Gradle

```groovy
repositories {
    maven {
        url "https://repo.kiinse.me/releases"
    }
}

dependencies {
    compileOnly 'kiinse.me.plugins.minecore:MineCore:ЗДЕСЬ_УКАЗАТЬ_ВЕРСИЮ'
}
```

## 📖Использование

<b>Все примеры и более подробное объяснение можно найти на [Wiki](https://github.com/kiinse/MineCore/wiki).</b>

----

<b>Так как все примеры есть на WIKI, то приведу здесь небольшой пример работы с сообщениями.</b>

---> В папке ресурсов создаем папку «messages», где так же создаем несколько файлов локализации. Например, en.json и ru.json.
Получаем следующую структуру:

```txt
.
└── resources
    └── messages
        ├── en.json
        └── ru.json
```

---> После запуска плагина, содержащего главный класс, унаследованный от "MineCorePlugin" - эти файлы появятся в папке
плагина на сервере.

---> Отправка текста из этих файлов занимает всего две строки:

```java
public final class TestPlugin extends MineCorePlugin { // Main class

    @Override
    public void onStart() throws Exception {
        // Код при старте
    }

    @Override
    public void onStop() throws Exception {
        // Код при выключении
    }

    private void sendMessageToPlayer(Player player) {
        MessagesUtils messagesUtils = new MineMessagesUtils(this);
        messagesUtils.sendMessageWithPrefix(player, Message.MESSAGE_HELLO); // Отправляем игроку строку "message_hello" из json файлов с локализациями.
        // Определение языка игрока и из какого файла будет использована строка с текстом определяется автоматически.
    }
}

```

---> Содержание данных файлов:

Файл "en.json":

```json
{
  "prefix": "message prefix",
  "message_hello": "Hello player!"
}
```

Файл "ru.json":

```json
{
  "prefix": "message prefix",
  "message_hello": "Привет, игрок!"
}
```

## 💬Команды

| Команда                     | Права               | Описание                                              |
|-----------------------------|---------------------|-------------------------------------------------------|
| /playerLocale                     | playerLocale.status       | Отображает текущую локализацию                        |
| /playerLocale change              | playerLocale.change       | Открывает GUI для выбора языка                        |
| /playerLocale help                | playerLocale.help         | Команда помощи                                        |
| /playerLocale set [playerLocale]        | playerLocale.change       | Устанавливает язык без открытия GUI                   |
| /playerLocale list                | playerLocale.list         | Список языков, доступных для установки                |
| /playerLocale get [player]        | playerLocale.get          | Просмотр языка игрока                                 |
| /minecore reload [plugin]  | minecore.reload    | Перезагрузка плагина, который использует MineCore |
| /minecore disable [plugin] | minecore.disable   | Выключение плагина, который использует MineCore   |
| /minecore enable [plugin]  | minecore.enable    | Включение плагина, который использует MineCore    |
| /statistic                  | minecore.statistic | Просмотр статистики по количеству убитых мобов        |

## 🪧Placeholders

| Placeholder                                             | Описание                                            |
|---------------------------------------------------------|-----------------------------------------------------|
| %statistic_НАЗВАНИЕ-МОБА% (Пример: %statistic_CREEPER%) | Отображает количество убитого npc игроком           |
| %locale_player%                                         | Отображает выбранный язык                           |
| %locale_list%                                           | Отображает список всех языков, доступных для выбора |

## 📃Конфиг

```yaml
playerLocale.default: en # Язык по умолчанию, если по каким-то причинам он не смог определиться при первом заходе игрока

first.join.message: true # Сообщение при первом заходе игрока, которая говорит какой язык был определён у него.
actionbar.indicators: true # Индикаторы над toolbar. Сам MineCore не использует эту функцию, но она может быть нужна для других плагинов. Требуется PlaceholderAPI для работы.

config.version: 3 # ЭТО ТРОГАТЬ НЕ НУЖНО =)
debug: false # Этой строки нет в конфиге по умолчанию, но вы можете ввести ее в конфиг MineCore для отображения CONFIG логов в консоли сервера.
```
