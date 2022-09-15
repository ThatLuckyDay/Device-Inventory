# Device-Inventory

---
REST приложение для учета устройств.

Пользователь имеющий Google Account может использовать данное приложение для получения устройства.

В данном приложении используется БД PostgreSql для хранения QR кодов и информации об устройствах, а 
также информации о пользователях. Серверная часть приложения запускается в контейнере Docker.

Для взаимодействия пользователей с приложением предусмотрен UI, созданный при помощи React.

# Software

Для работы с приложением Device-Inventory необходимы следующие компоненты:
Docker v20.10.17, docker-compose v2.9.0 and Node.js v18.6.

Alternative: Java 11, PostgreSql 14.1 and Node.js v18.6 (запуск без использования Docker не указан).

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