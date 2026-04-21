package com.example.soyle.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.soyle.ui.components.*
import com.example.soyle.ui.theme.*

// ── Модель данных онбординга ──────────────────────────────────────────────────

data class OnboardingData(
    val goal         : String? = null,
    val ageGroup     : String? = null,
    val morningAlert : Boolean = true,
    val eveningAlert : Boolean = true
)

// ── Главный экран онбординга ──────────────────────────────────────────────────

@Composable
fun OnboardingScreen(onFinish: (OnboardingData) -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    var data by remember { mutableStateOf(OnboardingData()) }

    val totalSteps = 5

    AnimatedContent(
        targetState   = step,
        transitionSpec = {
            if (targetState > initialState) {
                slideInHorizontally { it } + fadeIn() togetherWith
                        slideOutHorizontally { -it } + fadeOut()
            } else {
                slideInHorizontally { -it } + fadeIn() togetherWith
                        slideOutHorizontally { it } + fadeOut()
            }
        },
        label = "onboardStep"
    ) { currentStep ->
        when (currentStep) {
            0 -> OnboardingSplash(
                onBegin = { step++ }
            )
            1 -> OnboardingGoalStep(
                selected = data.goal,
                onSelect = { data = data.copy(goal = it) },
                onNext   = { step++ },
                onBack   = { step-- }
            )
            2 -> OnboardingAgeStep(
                selected = data.ageGroup,
                onSelect = { data = data.copy(ageGroup = it) },
                onNext   = { step++ },
                onBack   = { step-- }
            )
            3 -> OnboardingTimeStep(
                morning  = data.morningAlert,
                evening  = data.eveningAlert,
                onMorningToggle = { data = data.copy(morningAlert = it) },
                onEveningToggle = { data = data.copy(eveningAlert = it) },
                onNext   = { step++ },
                onBack   = { step-- }
            )
            4 -> OnboardingReadyStep(
                onFinish = { onFinish(data) }
            )
        }
    }
}

// ── Шаг 0: Сплэш (как Stoic — чёрный фон, птица, кнопка Begin) ───────────────

@Composable
private fun OnboardingSplash(onBegin: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "bird")
    val floatY by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = -12f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "birdFloat"
    )
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoyleBg)
    ) {
        // Крест закрытия (как в Stoic)
        Text(
            text     = "×",
            fontSize = 22.sp,
            color    = SoyleTextMuted,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp)
                .clickable(onClick = onBegin)
        )

        Column(
            modifier            = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = visible,
                enter   = fadeIn(tween(800)) + slideInVertically { -40 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Название приложения — стиль Stoic (строчные + точка)
                    Text(
                        text       = "söyle.",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 36.sp,
                        color      = SoyleTextPrimary,
                        letterSpacing = (-1).sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text      = "твой помощник в развитии речи",
                        fontSize  = 16.sp,
                        color     = SoyleTextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(60.dp))

            // Иллюстрация птицы — контурная, белая (как в Stoic)
            AnimatedVisibility(
                visible = visible,
                enter   = fadeIn(tween(1000, 300))
            ) {
                SpeechBirdIllustration(
                    modifier = Modifier
                        .size(260.dp)
                        .offset(y = floatY.dp)
                )
            }
        }

        // Кнопка Begin внизу
        AnimatedVisibility(
            visible = visible,
            enter   = fadeIn(tween(800, 600)) + slideInVertically { 40 },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 40.dp)
                .fillMaxWidth()
        ) {
            SoylePrimaryButton(text = "Начать", onClick = onBegin)
        }
    }
}

// ── Контурная иллюстрация (голова + птица, как в Stoic) ──────────────────────

@Composable
fun SpeechBirdIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokePaint = Stroke(width = 2.2f, cap = StrokeCap.Round)
        val lineColor   = Color.White

        // ── Контур лица (профиль) ─────────────────────────────────────────
        val path = Path().apply {
            moveTo(w * 0.52f, h * 0.10f)           // Верх головы
            cubicTo(
                w * 0.70f, h * 0.10f,
                w * 0.78f, h * 0.20f,
                w * 0.76f, h * 0.35f                // Лоб → нос
            )
            cubicTo(
                w * 0.75f, h * 0.42f,
                w * 0.70f, h * 0.47f,
                w * 0.66f, h * 0.52f                // Нос
            )
            cubicTo(
                w * 0.68f, h * 0.55f,
                w * 0.64f, h * 0.60f,
                w * 0.55f, h * 0.63f                // Рот → подбородок
            )
            cubicTo(
                w * 0.48f, h * 0.66f,
                w * 0.44f, h * 0.72f,
                w * 0.44f, h * 0.80f                // Шея
            )
            cubicTo(
                w * 0.44f, h * 0.88f,
                w * 0.40f, h * 0.94f,
                w * 0.34f, h * 0.98f                // Грудь
            )
        }
        drawPath(path, lineColor, style = strokePaint)

        // Волосы (дуга назад)
        val hairPath = Path().apply {
            moveTo(w * 0.52f, h * 0.10f)
            cubicTo(
                w * 0.40f, h * 0.08f,
                w * 0.30f, h * 0.14f,
                w * 0.25f, h * 0.28f
            )
            cubicTo(
                w * 0.22f, h * 0.38f,
                w * 0.28f, h * 0.46f,
                w * 0.36f, h * 0.50f
            )
        }
        drawPath(hairPath, lineColor, style = strokePaint)

        // Глаз (маленький изгиб)
        val eyePath = Path().apply {
            moveTo(w * 0.66f, h * 0.30f)
            cubicTo(
                w * 0.67f, h * 0.29f,
                w * 0.69f, h * 0.29f,
                w * 0.70f, h * 0.30f
            )
        }
        drawPath(eyePath, lineColor, style = strokePaint)

        // ── Птица на ладони ────────────────────────────────────────────────
        // Ладонь/рука
        val handPath = Path().apply {
            moveTo(w * 0.34f, h * 0.98f)
            cubicTo(
                w * 0.38f, h * 0.92f,
                w * 0.50f, h * 0.88f,
                w * 0.62f, h * 0.88f
            )
        }
        drawPath(handPath, lineColor, style = strokePaint)

        // Тело птицы (белое заполненное)
        drawOval(
            color   = lineColor,
            topLeft = Offset(w * 0.54f, h * 0.76f),
            size    = Size(w * 0.18f, w * 0.12f)
        )
        // Голова птицы
        drawCircle(
            color  = lineColor,
            radius = w * 0.05f,
            center = Offset(w * 0.74f, h * 0.75f)
        )
        // Клюв
        val beakPath = Path().apply {
            moveTo(w * 0.79f, h * 0.753f)
            lineTo(w * 0.83f, h * 0.760f)
            lineTo(w * 0.79f, h * 0.770f)
        }
        drawPath(beakPath, lineColor.copy(alpha = 0.6f), style = strokePaint)
        // Глаз птицы (точка)
        drawCircle(
            color  = Color.Black,
            radius = w * 0.012f,
            center = Offset(w * 0.755f, h * 0.745f)
        )
        // Лапки птицы
        val legPath1 = Path().apply {
            moveTo(w * 0.60f, h * 0.88f)
            lineTo(w * 0.60f, h * 0.92f)
            moveTo(w * 0.65f, h * 0.88f)
            lineTo(w * 0.65f, h * 0.92f)
        }
        drawPath(legPath1, lineColor, style = strokePaint)
    }
}

// ── Шаг 1: Выбор цели ─────────────────────────────────────────────────────────

@Composable
private fun OnboardingGoalStep(
    selected : String?,
    onSelect : (String) -> Unit,
    onNext   : () -> Unit,
    onBack   : () -> Unit
) {
    val goals = listOf(
        "Поставить звук «Р»",
        "Поставить звук «Л»",
        "Исправить шипящие",
        "Развить общую речь",
        "Что-то другое"
    )
    OnboardingLayout(
        step      = 1,
        total     = 5,
        title     = "Что важнее всего\nсейчас?",
        subtitle  = "Это поможет подобрать\nнужные упражнения.",
        canNext   = selected != null,
        onNext    = onNext,
        onBack    = onBack
    ) {
        Spacer(Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            goals.forEach { goal ->
                SoyleOptionButton(
                    text       = goal,
                    isSelected = selected == goal,
                    onClick    = { onSelect(goal) }
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text      = "Выбор не ограничивает доступ к функциям.",
            fontSize  = 12.sp,
            color     = SoyleTextMuted,
            textAlign = TextAlign.Center,
            modifier  = Modifier.fillMaxWidth()
        )
    }
}

// ── Шаг 2: Возраст ───────────────────────────────────────────────────────────

@Composable
private fun OnboardingAgeStep(
    selected : String?,
    onSelect : (String) -> Unit,
    onNext   : () -> Unit,
    onBack   : () -> Unit
) {
    val ages = listOf("3–4 года", "5–6 лет", "7–9 лет", "10–12 лет", "13+ лет")
    OnboardingLayout(
        step      = 2,
        total     = 5,
        title     = "Сколько лет\nребёнку?",
        subtitle  = "Это помогает подобрать\nуровень сложности.",
        canNext   = selected != null,
        onNext    = onNext,
        onBack    = onBack
    ) {
        Spacer(Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ages.forEach { age ->
                SoyleOptionButton(
                    text       = age,
                    isSelected = selected == age,
                    onClick    = { onSelect(age) }
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text      = "Выбор не ограничивает доступ к функциям.",
            fontSize  = 12.sp,
            color     = SoyleTextMuted,
            textAlign = TextAlign.Center,
            modifier  = Modifier.fillMaxWidth()
        )
    }
}

// ── Шаг 3: Время напоминаний ─────────────────────────────────────────────────

@Composable
private fun OnboardingTimeStep(
    morning  : Boolean,
    evening  : Boolean,
    onMorningToggle : (Boolean) -> Unit,
    onEveningToggle : (Boolean) -> Unit,
    onNext   : () -> Unit,
    onBack   : () -> Unit
) {
    OnboardingLayout(
        step      = 3,
        total     = 5,
        title     = "Когда удобно\nзаниматься?",
        subtitle  = null,
        canNext   = true,
        onNext    = onNext,
        onBack    = onBack
    ) {
        // Иллюстрация уведомления
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(SoyleSurface),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier              = Modifier.padding(horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1A1A2E)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("s.", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.width(120.dp).height(8.dp).clip(RoundedCornerShape(4.dp)).background(SoyleSurface2))
                    Spacer(Modifier.height(6.dp))
                    Box(modifier = Modifier.width(160.dp).height(8.dp).clip(RoundedCornerShape(4.dp)).background(SoyleSurface2))
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            TimeToggleRow(
                label    = "Утро",
                time     = "08:00",
                enabled  = morning,
                onToggle = onMorningToggle
            )
            TimeToggleRow(
                label    = "День",
                time     = "14:30",
                enabled  = true,
                onToggle = {}
            )
            TimeToggleRow(
                label    = "Вечер",
                time     = "20:00",
                enabled  = evening,
                onToggle = onEveningToggle
            )
        }
    }
}

// ── Шаг 4: Готово ────────────────────────────────────────────────────────────

@Composable
private fun OnboardingReadyStep(onFinish: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoyleBg)
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier            = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(60.dp))

            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Text(
                    text       = "Всё готово.",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 34.sp,
                    color      = SoyleTextPrimary,
                    letterSpacing = (-1).sp
                )
                Text(
                    text      = "Здесь вы найдёте упражнения, которые помогают развить чёткую и красивую речь.",
                    fontSize  = 16.sp,
                    color     = SoyleTextSecondary,
                    lineHeight = 24.sp
                )

                // Мини-превью функций
                val features = listOf(
                    "🎯" to "Ежедневные упражнения",
                    "🎮" to "Игры для развития речи",
                    "📊" to "Отслеживание прогресса"
                )
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    features.forEach { (icon, text) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(icon, fontSize = 20.sp)
                            Text(text, fontSize = 15.sp, color = SoyleTextSecondary)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            SoylePrimaryButton(
                text    = "Начать занятия",
                onClick = onFinish,
                modifier = Modifier.padding(bottom = 40.dp)
            )
        }
    }
}

// ── Базовый layout онбординга ─────────────────────────────────────────────────

@Composable
private fun OnboardingLayout(
    step     : Int,
    total    : Int,
    title    : String,
    subtitle : String?,
    canNext  : Boolean,
    onNext   : () -> Unit,
    onBack   : () -> Unit,
    content  : @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoyleBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            // Заголовок навигации
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text    = "‹",
                    fontSize = 24.sp,
                    color   = SoyleTextSecondary,
                    modifier = Modifier.clickable(onClick = onBack)
                )
                Text(
                    text    = "Пропустить",
                    fontSize = 14.sp,
                    color   = SoyleTextSecondary,
                    modifier = Modifier.clickable(onClick = onNext)
                )
            }

            Spacer(Modifier.height(40.dp))

            // Заголовок
            Text(
                text          = title,
                fontWeight    = FontWeight.Bold,
                fontSize      = 28.sp,
                color         = SoyleTextPrimary,
                lineHeight    = 36.sp,
                letterSpacing = (-0.5).sp
            )
            if (subtitle != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text      = subtitle,
                    fontSize  = 14.sp,
                    color     = SoyleTextSecondary,
                    lineHeight = 20.sp
                )
            }

            Spacer(Modifier.height(32.dp))

            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                content  = content
            )

            Spacer(Modifier.height(16.dp))

            SoylePrimaryButton(
                text    = "Продолжить",
                enabled = canNext,
                onClick = onNext,
                modifier = Modifier.padding(bottom = 40.dp)
            )
        }
    }
}