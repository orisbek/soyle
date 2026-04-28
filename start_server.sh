#!/bin/bash
cd "$(dirname "$0")"
echo "Запуск Söyle Speech API..."
echo "При первом запуске скачивается модель (~40 МБ) — подожди"
echo ""
python3 backend_server.py
