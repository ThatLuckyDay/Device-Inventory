# Device-Inventory

---
Device-Inventory - это REST приложение для учета устройств.

Каждому устройству присваевается QR код. Список имеющихся устройств и их место нахождения доступны каждому пользователю
Device-Inventory. Пользователь имеющий Google Account может воспользоваться приложением Device-Inventory для получения 
устройства.

QR коды, а также информация об устройствах и пользователях, хранятся в БД PostgreSql. 
Серверная часть приложения Device-Inventory запускается в контейнере Docker. 
Предусмотрен пользовательский интерфейс, разработанный на React.

# Software

Для запуск приложения Device-Inventory необходимы следующие компоненты:
  - Docker v20.10.17
  - docker-compose v2.9.0
  - Node.js v18.6

Альтернативный вариант: 
  - Java 11, 
  - PostgreSql 14.1 
  - Node.js v18.6 

Запуск без использования Docker не указан.

# Start

1. Скачать код: git clone 
[https://github.com/ThatLuckyDay/Device-Inventory.git](https://github.com/ThatLuckyDay/Device-Inventory.git).
2. Для запуска backend: перейти в {app directory}/ops-tools/docker и выполнить docker-compose up. 
3. Для запуска frontend:
   - перейти в {app directory}/di_frontend и выполнить команду npm install - загрузка необходимых 
   зависимостей node.js при первом запуске приложения;
   - выполнить npm start.

Приложение запуститься и будет работать по адресу [http://localhost:3000/](http://localhost:3000/).

# API
Для того, чтобы посмотреть API, представленные в проекте, перейдите по ссылке 
[http://localhost:8080/swagger-ui/](http://localhost:8080/swagger-ui/).