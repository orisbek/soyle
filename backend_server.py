import base64
import numpy as np
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List
import speech_recognition as sr
import io
import wave
import uvicorn
import re

app = FastAPI()

class AnalyzeRequest(BaseModel):
    audio_base64: str
    phoneme: str
    mode: str

class AnalyzeResponse(BaseModel):
    score: int
    feedback: str
    waveform_data: List[float]
    duration_ms: int

def clean_text(text):
    # Очищаем текст: маленькие буквы, без лишних знаков
    if not text: return ""
    text = text.lower().strip()
    # Заменяем ё на е для стабильности
    text = text.replace("ё", "е")
    # Оставляем только буквы и цифры
    text = re.sub(r'[^а-я0-9\s]', '', text)
    return text

def get_score_and_feedback(audio_pcm, target_text):
    recognizer = sr.Recognizer()
    audio_data = np.frombuffer(audio_pcm, dtype=np.int16)
    rms = np.sqrt(np.mean(audio_data.astype(float)**2))
    
    byte_io = io.BytesIO()
    with wave.open(byte_io, 'wb') as wav_file:
        wav_file.setnchannels(1)
        wav_file.setsampwidth(2)
        wav_file.setframerate(16000)
        wav_file.writeframes(audio_pcm)
    byte_io.seek(0)
    
    with sr.AudioFile(byte_io) as source:
        audio = recognizer.record(source)
    
    try:
        # Пытаемся распознать текст
        heard_raw = recognizer.recognize_google(audio, language="ru-RU")
        heard_text = clean_text(heard_raw)
        target = clean_text(target_text)
        
        # ДЛЯ ОТЛАДКИ: выводим в консоль сервера
        print(f"--- АНАЛИЗ ---")
        print(f"Цель (исходная): '{target_text}'")
        print(f"Цель (очищенная): '{target}'")
        print(f"Услышано (исходное): '{heard_raw}'")
        print(f"Услышано (очищенное): '{heard_text}'")

        # 1. Идеальное совпадение
        if target == heard_text or target in heard_text or heard_text in target:
            return 100, f"Отлично! Ты правильно сказал '{target_text.upper()}'! 🎉"

        # 2. Проверка замены Р на Л
        # Создаем маски, где р и л считаются одной буквой
        # Используем именно РУССКИЕ буквы 'р' и 'л'
        target_mask = target.replace("р", "л")
        heard_mask = heard_text.replace("р", "л")
        
        print(f"Сравнение масок: '{target_mask}' vs '{heard_mask}'")

        if target_mask == heard_mask or target_mask in heard_mask or heard_mask in target_mask:
            return 50, f"Почти! Я слышу '{heard_raw.upper()}', но нужно порычать: 'Р-Р-Р'. Давай еще раз! 💪"

        # 3. Если услышали Г или Х (горловое Р)
        target_g = target.replace("р", "г")
        target_h = target.replace("р", "х")
        if heard_text == target_g or heard_text == target_h or target_g in heard_text or target_h in heard_text:
             return 50, "Похоже на горловое 'Р'. Попробуй заставить дрожать кончик языка! 👅"

        # 4. Другие ошибки
        return 20, f"Я услышал '{heard_raw.upper()}', а нужно '{target_text.upper()}'. Попробуй снова!"

    except sr.UnknownValueError:
        print("Google не распознал речь")
        if rms > 400 and "р" in target_text.lower():
            return 50, "Я слышу рычание! Скажи слово целиком, у тебя получится! 🦜"
        return 10, f"Скажи '{target_text.upper()}' громче! 🎙"
    except Exception as e:
        print(f"Ошибка: {e}")
        return 0, f"Ошибка сервера: {e}"

@app.post("/analyze", response_model=AnalyzeResponse)
async def analyze(request: AnalyzeRequest):
    try:
        audio_bytes = base64.b64decode(request.audio_base64)
        score, feedback = get_score_and_feedback(audio_bytes, request.phoneme)
        
        audio_np = np.frombuffer(audio_bytes, dtype=np.int16)
        waveform = audio_np[::400].astype(float).tolist()

        return AnalyzeResponse(
            score=score,
            feedback=feedback,
            waveform_data=waveform,
            duration_ms=len(audio_np) // 16
        )
    except Exception as e:
        print(f"Global Error: {e}")
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
