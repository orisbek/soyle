package com.example.soyle.mascot

import com.example.soyle.domain.model.MascotEmotion

/**
 * Определяет эмоцию маскота по результату произношения.
 * Простая функция без зависимостей — удобно тестировать.
 */
object MascotEmotionManager {

    fun fromScore(score: Int, isNewRecord: Boolean = false): MascotEmotion = when {
        isNewRecord   -> MascotEmotion.CELEBRATING
        score >= 85   -> MascotEmotion.HAPPY
        score >= 65   -> MascotEmotion.GOOD
        score >= 45   -> MascotEmotion.NEUTRAL
        else          -> MascotEmotion.SUPPORTIVE
    }

    fun fromStreakDay(streakDay: Int): MascotEmotion = when {
        streakDay == 0                   -> MascotEmotion.GREETING
        streakDay % 7 == 0 && streakDay > 0 -> MascotEmotion.CELEBRATING
        streakDay >= 3                   -> MascotEmotion.HAPPY
        else                             -> MascotEmotion.GREETING
    }

    fun greeting(): MascotEmotion = MascotEmotion.GREETING

    /** Текстовая фраза для каждой эмоции */
    fun phraseFor(emotion: MascotEmotion, phoneme: String = ""): String = when (emotion) {
        MascotEmotion.HAPPY       -> listOf(
            "Вау! Просто супер! 🎉",
            "Ты молодец! Так держать! 🌟",
            "Отлично! Я горжусь тобой! ⭐"
        ).random()
        MascotEmotion.GOOD        -> listOf(
            "Хорошо! Ещё немного — и будет отлично! 😊",
            "Неплохо! Попробуй ещё раз! 💪",
            "Почти! Ты на верном пути! 🎯"
        ).random()
        MascotEmotion.NEUTRAL     -> listOf(
            "Продолжай стараться! 🙂",
            "Не сдавайся, у тебя всё получится!",
            "Попробуй ещё раз, ты справишься!"
        ).random()
        MascotEmotion.SUPPORTIVE  -> listOf(
            "Не переживай, это просто требует практики! 💙",
            "Я верю в тебя! Попробуем вместе? 🦉",
            "Каждая попытка делает тебя лучше! ✨"
        ).random()
        MascotEmotion.CELEBRATING -> listOf(
            "Новый рекорд! Ты невероятный! 🏆",
            "ВАУ! Это лучшее, что я слышал! 🎊",
            "ПОТРЯСАЮЩЕ! Ты настоящий мастер! 🌈"
        ).random()
        MascotEmotion.GREETING    -> listOf(
            "Привет! Я рад тебя видеть! 👋",
            "Привет! Пора тренироваться! 🦉",
            "Добро пожаловать! Сегодня мы научимся говорить звук «$phoneme»!"
        ).random()
    }
}