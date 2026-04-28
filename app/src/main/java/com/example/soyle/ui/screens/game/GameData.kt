package com.example.soyle.ui.screens.game

// ── Угадай слово — слова по уровням ───────────────────────────────────────────

object GuessWordData {

    val level1 = listOf(
        "рак", "рот", "рог", "ром", "рис", "рысь", "рука", "рыба",
        "рост", "роза", "мир", "сыр", "жир", "пар", "жар", "дар",
        "шар", "пир", "бор", "жор"
    ).shuffled()

    val level2 = listOf(
        "радуга", "работа", "рыбалка", "рубашка", "ремонт", "ребёнок",
        "рисунок", "радость", "ромашка", "речка", "трава", "береза",
        "зеркало", "горка", "город", "корова", "парус", "парад",
        "морковь", "дорога", "забор", "ковёр", "мотор", "пожар"
    ).shuffled()

    val level3 = listOf(
        "рыболов", "ракетка", "рукопись", "регистр", "развитие",
        "рассказ", "рыбачить", "рабочий", "разбудить", "разложить",
        "различать", "разговор", "руководство", "радоваться",
        "мороженое", "коридор", "трактор", "трамвай", "прибор"
    ).shuffled()

    fun forLevel(level: Int) = when (level) {
        1    -> level1
        2    -> level2
        else -> level3
    }
}

// ── Где буква Р — слова с позицией ────────────────────────────────────────────

enum class RPosition { BEGINNING, MIDDLE, END }

data class WhereRWord(val word: String, val position: RPosition)

object WhereRData {

    private val beginning = listOf(
        "рак", "рама", "рана", "рот", "рог", "ром", "рука", "ручка",
        "рыба", "ряд", "рис", "река", "речь", "рост", "роза", "рысь",
        "риф", "рыжий", "работа", "робот", "радость", "радио", "ранец",
        "раковина", "рубашка", "рубль", "рукав", "рыбалка", "рыбка",
        "рынок", "рябина", "рюкзак", "речка", "резина", "рецепт",
        "редис", "ребёнок", "ремонт", "рисунок", "ромашка", "роль",
        "роща", "рабочий", "радужный", "ракетка", "рассказ",
        "рукопись", "рыболов", "рычаг", "розовый"
    ).map { WhereRWord(it, RPosition.BEGINNING) }

    private val middle = listOf(
        "гора", "горка", "город", "горох", "корова", "корень", "кора",
        "пирог", "парус", "парад", "парк", "трава", "тропа", "трактор",
        "трамвай", "зеркало", "зерно", "берёза", "берлога", "серый",
        "мороз", "море", "морковь", "дорога", "дорожка", "коробка",
        "коридор", "арбуз", "армия", "арена", "сироп", "мороженое"
    ).map { WhereRWord(it, RPosition.MIDDLE) }

    private val end = listOf(
        "мир", "сыр", "жир", "пар", "жар", "дар", "шар",
        "удар", "пожар", "комар", "топор", "забор", "двор",
        "ковёр", "костёр", "мотор", "прибор", "набор", "узор", "забор"
    ).map { WhereRWord(it, RPosition.END) }

    fun forLevel(level: Int): List<WhereRWord> {
        val beginPart = beginning.shuffled()
        val midPart   = middle.shuffled()
        val endPart   = end.shuffled()
        return when (level) {
            1    -> (beginPart.take(5) + midPart.take(3) + endPart.take(2)).shuffled()
            2    -> (beginPart.take(5) + midPart.take(5) + endPart.take(5)).shuffled()
            else -> (beginPart.take(6) + midPart.take(7) + endPart.take(7)).shuffled()
        }
    }
}

// ── Поймай букву Р — набор букв для игр ──────────────────────────────────────

object CatchLetterData {
    val otherLetters = listOf(
        "А", "Б", "В", "Г", "Д", "Е", "К", "Л",
        "М", "Н", "О", "П", "С", "Т", "У", "Х"
    )

    fun generateGrid(rCount: Int = 5): List<String> {
        val others = otherLetters.shuffled().take(20 - rCount)
        return (others + List(rCount) { "Р" }).shuffled()
    }

    fun generateSequence(length: Int = 15): List<String> {
        val result = mutableListOf<String>()
        repeat(length) { i ->
            result.add(if (i % 3 == 1) "Р" else otherLetters.random())
        }
        return result.shuffled()
    }
}

// ── Скороговорки с буквой Р ───────────────────────────────────────────────────

data class TongueTwister(
    val text  : String,
    val hint  : String,
    val level : Int    // 1-3
)

object TongueTwistersData {
    val all = listOf(
        TongueTwister("Рыла свинья тупорыла, полдвора рылом изрыла.", "Говори медленно", 1),
        TongueTwister("Рар-рар-рар — шумит самовар.", "Три раза подряд", 1),
        TongueTwister("Рара-рара — нора детвора.", "Чётко букву Р", 1),
        TongueTwister("Рыбак рыбачил, рыбка рыбачила.", "Ударение на Р", 1),
        TongueTwister("На горе Арарат растёт крупный виноград.", "Следи за ритмом", 2),
        TongueTwister("Три трубача трубят в трубы.", "Не торопись", 2),
        TongueTwister("Рима Рита рисовала, рисовала — не устала.", "Плавно и чётко", 2),
        TongueTwister("Карл у Клары украл кораллы.", "Классика", 2),
        TongueTwister("Проворонила ворона воронёнка.", "Длинная, не спеши", 3),
        TongueTwister("Курьер курьера обогнал курьера.", "Три раза быстро", 3),
        TongueTwister("Рапортовал да не дорапортовал, дорапортовывал да зарапортовался.", "Самая сложная!", 3),
        TongueTwister("Всех скороговорок не перескороговоришь, не перевыскороговоришь.", "Три раза подряд", 3),
    )

    fun forLevel(level: Int) = all.filter { it.level == level }.shuffled()
}

// ── Стишки с буквой Р ─────────────────────────────────────────────────────────

data class RhymeItem(val lines: List<String>)

object RhymesData {
    val all = listOf(
        RhymeItem(listOf("Рыба в речке — раз, два, три,", "Рыбка, рыбка, посмотри")),
        RhymeItem(listOf("Рома рано встал с утра,", "Руку поднял — Ура! Ура!")),
        RhymeItem(listOf("Рак по речке тихо шёл,", "Камень круглый он нашёл")),
        RhymeItem(listOf("Рыжий кот мурлычет: Р-р-р,", "Рядом бегает комар")),
        RhymeItem(listOf("Роза в парке расцвела,", "Радость детям принесла")),
        RhymeItem(listOf("Ручка пишет: Ра-ра-ра,", "Получается игра")),
        RhymeItem(listOf("Рыбка в море — раз, два, три,", "Рядом плавают киты")),
        RhymeItem(listOf("Рома строит новый дом,", "Рядом дерево с окном")),
        RhymeItem(listOf("Рано утром ветерок", "Разбудил в саду цветок")),
        RhymeItem(listOf("Ракета в небо — раз и вверх,", "Радость, смех и звонкий смех")),
        RhymeItem(listOf("Рыжий кролик прыг да скок,", "Рядом вырос бугорок")),
        RhymeItem(listOf("Рома рисует яркий круг,", "Рядом с ним зелёный луг")),
        RhymeItem(listOf("Речка быстро вдаль бежит,", "Рыбка в глубине кружит")),
        RhymeItem(listOf("Рысь в лесу идёт легко,", "Рядом дерево высоко")),
        RhymeItem(listOf("Робот робко говорит:", "Р-р-р — мотор мой не шумит")),
        RhymeItem(listOf("Радуга над домом встала,", "Радость всем она дала")),
        RhymeItem(listOf("Рюкзак Ромы у двери,", "Рядом книги — посмотри")),
        RhymeItem(listOf("Рыбка прыгнула в ведро,", "Разбрызгала всё добро")),
        RhymeItem(listOf("Рома крутит колесо,", "Рядом катится оно")),
        RhymeItem(listOf("Рыжий воробей летит,", "Рядом ветер шелестит")),
    ).shuffled()
}

// ── Упражнения для языка ──────────────────────────────────────────────────────

data class TongueExercise(
    val name        : String,
    val description : String,
    val instruction : String,
    val durationSec : Int,
    val emoji       : String
)

object TongueExercisesData {
    val all = listOf(
        TongueExercise(
            name        = "Иголочка",
            description = "Вытяни язык острым жалом",
            instruction = "Открой рот. Вытяни язык вперёд как острую иголку. Держи 5 секунд.",
            durationSec = 5,
            emoji       = "📍"
        ),
        TongueExercise(
            name        = "Лопатка",
            description = "Язык широкий и расслабленный",
            instruction = "Открой рот. Положи широкий расслабленный язык на нижнюю губу. Держи 5 секунд.",
            durationSec = 5,
            emoji       = "🏏"
        ),
        TongueExercise(
            name        = "Качели",
            description = "Двигай языком вверх-вниз",
            instruction = "Открой рот. Двигай кончиком языка: вверх к носу, вниз к подбородку. Повтори 8 раз.",
            durationSec = 8,
            emoji       = "🎢"
        ),
        TongueExercise(
            name        = "Часики",
            description = "Язык из угла в угол рта",
            instruction = "Открой рот. Двигай кончиком языка к правому, затем к левому углу губ. 10 раз.",
            durationSec = 10,
            emoji       = "🕐"
        ),
        TongueExercise(
            name        = "Лошадка",
            description = "Цокай языком как лошадь",
            instruction = "Прижми язык к нёбу и резко оторви — получится цоканье. Повтори 10 раз.",
            durationSec = 8,
            emoji       = "🐴"
        ),
        TongueExercise(
            name        = "Чашечка",
            description = "Язык в форме чашки",
            instruction = "Открой рот, высуни язык. Подними края языка вверх — сделай «чашечку». 5 секунд.",
            durationSec = 5,
            emoji       = "☕"
        ),
        TongueExercise(
            name        = "Грибок",
            description = "Язык прилип к нёбу",
            instruction = "Широко открой рот. Прижми язык к нёбу и не отрывай. Держи 5 секунд.",
            durationSec = 5,
            emoji       = "🍄"
        ),
        TongueExercise(
            name        = "Дятел",
            description = "Бей кончиком языка по зубам",
            instruction = "Стучи кончиком языка по верхним зубам изнутри: да-да-да-да. 10 раз.",
            durationSec = 8,
            emoji       = "🦅"
        ),
    )
}
