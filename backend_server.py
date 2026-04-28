import base64
import io
import re
import wave
import numpy as np
import uvicorn
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List

# ── Загружаем Whisper один раз при старте ──────────────────────────────────────
from faster_whisper import WhisperModel

print("Загрузка модели Whisper (tiny — быстрая, ~40 МБ)...")
# tiny = максимальная скорость, base = чуть точнее
# Модель скачивается автоматически при первом запуске
whisper_model = WhisperModel("tiny", device="cpu", compute_type="int8")
print("Модель загружена! Сервер готов.")

# ── FastAPI ────────────────────────────────────────────────────────────────────
app = FastAPI(title="Söyle Speech API")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

# ── Модели запроса/ответа ──────────────────────────────────────────────────────
class AnalyzeRequest(BaseModel):
    audio_base64: str
    phoneme: str
    mode: str

class AnalyzeResponse(BaseModel):
    score: int
    feedback: str
    waveform_data: List[float]
    duration_ms: int

# ── Утилиты ───────────────────────────────────────────────────────────────────

def clean_text(text: str) -> str:
    """Нормализация: в нижний регистр, убрать ё→е, оставить только кириллицу."""
    if not text:
        return ""
    text = text.lower().strip()
    text = text.replace("ё", "е")
    text = re.sub(r'[^а-я\s]', '', text)
    return text.strip()

def pcm_to_wav_bytes(pcm_bytes: bytes, sample_rate: int = 16000) -> bytes:
    """Оборачивает сырые PCM-байты в WAV-контейнер."""
    buf = io.BytesIO()
    with wave.open(buf, 'wb') as wf:
        wf.setnchannels(1)       # моно
        wf.setsampwidth(2)       # 16 бит = 2 байта
        wf.setframerate(sample_rate)
        wf.writeframes(pcm_bytes)
    return buf.getvalue()

def levenshtein(a: str, b: str) -> int:
    """Расстояние Левенштейна для сравнения строк."""
    if not a: return len(b)
    if not b: return len(a)
    dp = [[0] * (len(b) + 1) for _ in range(len(a) + 1)]
    for i in range(len(a) + 1): dp[i][0] = i
    for j in range(len(b) + 1): dp[0][j] = j
    for i in range(1, len(a) + 1):
        for j in range(1, len(b) + 1):
            cost = 0 if a[i-1] == b[j-1] else 1
            dp[i][j] = min(dp[i-1][j] + 1, dp[i][j-1] + 1, dp[i-1][j-1] + cost)
    return dp[len(a)][len(b)]

# ── Основная логика анализа ────────────────────────────────────────────────────

def analyze_audio(pcm_bytes: bytes, phoneme: str):
    """
    Принимает сырые PCM bytes с Android (16kHz, 16bit, моно).
    Возвращает (score: int, feedback: str).
    """
    # 1. Проверка громкости
    audio_np = np.frombuffer(pcm_bytes, dtype=np.int16)
    rms = float(np.sqrt(np.mean(audio_np.astype(np.float64) ** 2)))
    print(f"[Анализ] Фонема='{phoneme}' | RMS={rms:.1f} | Длина={len(pcm_bytes)} байт")

    if rms < 80:
        return 0, "Слишком тихо. Говори громче и ближе к микрофону!"

    # 2. Конвертация PCM → WAV для Whisper
    wav_bytes = pcm_to_wav_bytes(pcm_bytes)
    wav_buf   = io.BytesIO(wav_bytes)

    # 3. Распознавание через Whisper
    try:
        segments, info = whisper_model.transcribe(
            wav_buf,
            language="ru",
            beam_size=3,
            vad_filter=True,          # фильтр тишины
            vad_parameters={"min_silence_duration_ms": 300}
        )
        heard_raw = " ".join(seg.text for seg in segments).strip()
    except Exception as e:
        print(f"[Whisper ошибка] {e}")
        # Если Whisper не смог — анализируем только по громкости
        return (70, "Слышу тебя! Попробуй ещё раз чётче.") if rms > 400 else (0, "Не удалось распознать. Попробуй ещё раз.")

    heard = clean_text(heard_raw)
    target = clean_text(phoneme)

    print(f"[Whisper] Ожидали='{target}' | Услышали='{heard}'")

    if not heard:
        # Whisper ничего не услышал, но громкость была — могли рычать
        if target in ("р", "рр") and rms > 350:
            return 85, "Слышу звук! Попробуй ещё раз произнести «р-р-р» чётче."
        return 0, "Ничего не услышал. Попробуй сказать громче и чётче!"

    # 4. Оценка по фонеме/слову
    return score_pronunciation(target, heard, rms)


def score_pronunciation(target: str, heard: str, rms: float):
    """
    Вычисляет оценку произношения и формирует обратную связь.
    target — что должны были сказать
    heard  — что распознал Whisper
    """

    # ── Одиночный звук «Р» ────────────────────────────────────────────────
    if target in ("р", "рр"):
        has_r = "р" in heard

        if has_r:
            return 100, "Отлично! Ты правильно произнёс звук Р! 🎉"

        # Типичные замены: Р→Л, Р→Г, Р→В, Р→Й
        if "л" in heard:
            return 35, "Ты заменяешь «Р» на «Л». Подними кончик языка к бугоркам за зубами!"
        if "г" in heard or "ы" in heard:
            return 30, "Попробуй не горловое «Р», а переднеязычное — кончик языка вверх!"
        if "в" in heard or "у" in heard:
            return 25, "Чуть-чуть не то. Попробуй: «Д-Д-Д-Р-Р-Р» — медленно!"

        return 20, "Не совсем то. Попробуй сказать «ДРРР» или «ТРРР»!"

    # ── Звук «Л» ──────────────────────────────────────────────────────────
    if target in ("л", "лл"):
        has_l = "л" in heard
        if has_l:
            return 100, "Отлично! Звук Л получился! 🎉"
        if "р" in heard:
            return 35, "Ты говоришь «Р» вместо «Л». Опусти кончик языка к зубам!"
        return 20, "Попробуй прижать кончик языка к верхним зубам и сказать «Л-Л-Л»."

    # ── Шипящие («Ш», «Щ», «Ж», «Ч») ─────────────────────────────────────
    if target in ("ш", "щ", "ж", "ч"):
        if target in heard:
            return 100, f"Отлично! Звук «{target.upper()}» получился! 🎉"
        if "с" in heard or "з" in heard:
            return 40, f"Ты говоришь свистящий звук. Подними язык «ковшиком» для «{target.upper()}»!"
        return 30, f"Попробуй ещё раз. Язык — «ковшиком», тёплый воздух идёт посередине."

    # ── Слово ─────────────────────────────────────────────────────────────
    if len(target) > 1:
        # Точное совпадение
        if target == heard:
            return 100, f"Идеально! Слово «{target.upper()}» произнесено правильно! 🏆"

        # Слово есть в распознанном тексте (может быть лишние слова вокруг)
        if target in heard:
            return 95, f"Отлично! Слово «{target.upper()}» понятно! 🎉"

        # Вычисляем сходство через Левенштейна
        dist    = levenshtein(target, heard)
        max_len = max(len(target), len(heard))
        sim     = max(0, int((1 - dist / max_len) * 100))

        if sim >= 80:
            return sim, f"Очень близко! Ещё чуть-чуть — у тебя получится!"
        if sim >= 50:
            return sim, f"Неплохо! Ты сказал «{heard.upper()}», а нужно «{target.upper()}». Попробуй медленнее."
        # Проверим хотя бы наличие нужного звука в слове
        if target[0] in heard:
            return 30, f"Слышу начальный звук! Попробуй слово «{target.upper()}» целиком."
        return 15, f"Попробуй ещё раз сказать слово «{target.upper()}» медленно и чётко."

    # ── Общий случай ──────────────────────────────────────────────────────
    if target in heard or heard in target:
        return 90, "Отлично! Звук услышан правильно! 🎉"

    dist = levenshtein(target, heard)
    sim  = max(0, int((1 - dist / max(len(target), len(heard), 1)) * 100))
    if sim >= 70:
        return sim, "Почти правильно! Ещё раз — и будет отлично."
    return max(10, sim), "Попробуй ещё раз, не спеши."


# ── Эндпоинты ────────────────────────────────────────────────────────────────

@app.get("/")
async def health_check():
    return {"status": "ok", "message": "Söyle Speech API работает!", "engine": "faster-whisper"}

@app.post("/analyze", response_model=AnalyzeResponse)
async def analyze(request: AnalyzeRequest):
    print(f"\n=== Запрос /analyze | phoneme='{request.phoneme}' | mode='{request.mode}' ===")
    try:
        # Декодируем base64 → PCM байты
        pcm_bytes = base64.b64decode(request.audio_base64)

        if len(pcm_bytes) < 1000:
            raise HTTPException(status_code=400, detail="Аудио слишком короткое")

        score, feedback = analyze_audio(pcm_bytes, request.phoneme)

        # Форма волны для UI (прореживаем)
        audio_np  = np.frombuffer(pcm_bytes, dtype=np.int16)
        waveform  = (audio_np[::400].astype(float) / 32768.0).tolist()  # нормируем в [-1, 1]
        duration  = len(audio_np) // 16  # мс при 16kHz

        print(f"=== Результат: score={score}, feedback='{feedback}' ===\n")
        return AnalyzeResponse(
            score        = score,
            feedback     = feedback,
            waveform_data = waveform,
            duration_ms  = duration
        )

    except HTTPException:
        raise
    except Exception as e:
        print(f"[ОШИБКА] {e}")
        raise HTTPException(status_code=500, detail=f"Ошибка сервера: {str(e)}")


# ── Запуск ────────────────────────────────────────────────────────────────────

if __name__ == "__main__":
    print("=" * 50)
    print("  Söyle Speech API Server")
    print("  http://0.0.0.0:8000")
    print("  Для Android-эмулятора: http://10.0.2.2:8000")
    print("=" * 50)
    uvicorn.run(app, host="0.0.0.0", port=8000, log_level="info")
