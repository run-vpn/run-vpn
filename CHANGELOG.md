# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

- Проведен рефакторинг всей логики связанные с серверами. Получение, редактирование, удаление и
  обновление. Появился возможность добавлять функционал SwipeToRefresh. Сокращено количество
  запросов на сервер. [VPN-1230](https://maxtasks.net/issue/VPN-1230)

- Исправлено текст стоимости подписок на экарне "Ваша подписка" [VPN-1195](https://maxtasks.net/issue/VPN-1195)
- Добавление серверов в избранное. [VPN-1212](https://maxtasks.net/issue/VPN-1212)
- Исправлны баги уведомления. [VPN-1196](https://maxtasks.net/issue/VPN-1196)
- Обратный отсчет на главном экране, если включено "Автоотключение" [VPN-1266](https://maxtasks.net/issue/VPN-1266)
- События для уведомлений состояния VPN. [VPN-1166](https://maxtasks.net/issue/VPN-1166)
- UI баги настроек [VPN-1140](https://maxtasks.net/issue/VPN-1140)
- UI элемента обновления в настройках. [VPN-1217](https://maxtasks.net/issue/VPN-1217)
- Отображение спика приложений при перезаходе на экран "Раздельное туннелирование" [VPN-1246](https://maxtasks.net/issue/VPN-1246)
- Испавлено лагание приложения при скачивании файла обновления. [VPN-1251](https://maxtasks.net/issue/VPN-1251)


## [1.2.2] - 2024-05-28

### Added

- Hotfix. Автопереподключение включена по умолчанию. [VPN-1258](https://maxtasks.net/issue/VPN-1258)

## [1.2.1] - 2024-05-28

### Added

- Исправлен баг кнопки "Добавить" и "Протестировать" при переключении сервера на публичный или
  обратно [VPN-1208](https://maxtasks.net/issue/VPN-1208)
- Исправлены UI баги элеменов обновления приложения. [VPN-1217](https://maxtasks.net/issue/VPN-1217)
- Заменена ссылка в окне принудительного обновления. [VPN-1218](https://maxtasks.net/issue/VPN-1218)
- Исправлен логика обновления [VPN-1229](https://maxtasks.net/issue/VPN-1229)
- Внедрен сервис обратной связи в сязке с бекендом [VPN-1189](https://maxtasks.net/issue/VPN-1189)
- Исправлены плавающие краши связанные с Proxy SDK [VPN-1234](https://maxtasks.net/issue/VPN-1234)
- Изменена ссылка на ТГ бот поддержки [VPN-1231](https://maxtasks.net/issue/VPN-1231)

## [1.2.0] - 2024-05-23

### Added

- Отдельные иконки и названия приложения для сборок PROD и
  STAGE [VPN-1194](https://maxtasks.net/issue/VPN-1194)