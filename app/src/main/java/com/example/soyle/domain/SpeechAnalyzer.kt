package com.example.soyle.domain

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

data class AnalysisResult(
    val score: Int,
    val feedback: String,
    val isSuccess: Boolean
)

@Singleton
class SpeechAnalyzer @Inject constructor() {

    /**
     * Основная функция расчета точности произношения.
     * Использует расстояние Левенштейна для определения схожести строк.
     */
    fun calculatePronunciationScore(expected: String, actual: String): AnalysisResult {
        val exp = expected.lowercase().trim()
        val act = actual.lowercase().trim()

        if (exp == act) {
            return AnalysisResult(100, "Отлично! Идеальное произношение!", true)
        }

        val distance = levenshteinDistance(exp, act)
        val maxLength = max(exp.length, act.length)
        
        // Расчет процента схожести
        val similarity = if (maxLength > 0) {
            ((1.0 - distance.toDouble() / maxLength) * 100).toInt()
        } else 0

        val feedback = generateFeedback(exp, act)
        
        return AnalysisResult(
            score = similarity.coerceIn(0, 100),
            feedback = feedback,
            isSuccess = similarity >= 80
        )
    }

    /**
     * Алгоритм Левенштейна (редакторское расстояние)
     */
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }

        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,      // удаление
                    dp[i][j - 1] + 1,      // вставка
                    dp[i - 1][j - 1] + cost // замена
                )
            }
        }
        return dp[s1.length][s2.length]
    }

    /**
     * Анализ типичных логопедических ошибок
     */
    private fun generateFeedback(expected: String, actual: String): String {
        return when {
            actual.isEmpty() -> "Я тебя не услышал. Попробуй еще раз!"
            
            // Проверка замены Р на Л (частая проблема у детей)
            expected.contains("р") && actual.contains("л") && !actual.contains("р") -> 
                "Похоже, ты заменяешь 'Р' на 'Л'. Попробуй поднять кончик языка выше!"
                
            // Проверка пропуска звука
            expected.length > actual.length && !actual.contains(expected.first()) ->
                "Ты пропустил первую букву. Давай попробуем медленнее."
                
            else -> when {
                levenshteinDistance(expected, actual) <= 1 -> "Почти правильно! Еще чуть-чуть усилий."
                else -> "Попробуй еще раз, следи за положением языка."
            }
        }
    }
}
