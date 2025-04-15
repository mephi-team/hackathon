# Используем официальный Node.js образ для сборки
FROM node:18-alpine AS builder

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем package.json и package-lock.json
COPY package*.json ./

# Устанавливаем зависимости
RUN npm install

# Копируем остальные файлы проекта
COPY . .

# Собираем приложение в production-режиме
RUN npm run build

# Используем легковесный Nginx для раздачи статических файлов
FROM nginx:alpine-slim

# Копируем собранные файлы из первого этапа в каталог Nginx
COPY --from=builder /app/dist /usr/share/nginx/html

# Копируем конфигурацию Nginx (если нужно)
COPY nginx.conf /etc/nginx/nginx.conf
