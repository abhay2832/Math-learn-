package com.example.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Achievement
import com.example.data.FavoriteItem
import com.example.data.MathLearnDatabase
import com.example.data.QuizResult
import com.example.data.TimetableItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.sqrt

sealed class Screen {
    object Welcome : Screen()
    object Dashboard : Screen()
    object Tables : Screen()
    object Squares : Screen()
    object Cubes : Screen()
    object Roots : Screen()
    object QuizConfig : Screen()
    object QuizPlay : Screen()
    object QuizResultScreen : Screen()
    object MockTestPlay : Screen()
    object MockTestResultScreen : Screen()
    object PYPPapers : Screen()
    object PYPViewPaper : Screen()
    object Timetable : Screen()
    object ProgressAnalytics : Screen()
    object Achievements : Screen()
    object Settings : Screen()
}

// Data structures for Quiz
data class Question(
    val category: String,
    val text: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String
)

data class QuizSessionState(
    val category: String = "",
    val difficulty: String = "",
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val score: Int = 0,
    val answers: List<String?> = emptyList(), // stores user's answers
    val timeLimitSeconds: Int = 0,
    val secondsLeft: Int = 0,
    val isFinished: Boolean = false,
    val isMock: Boolean = false,
    val negativeMarking: Boolean = false
)

// Data structures for Previous Year Papers
data class PYPQuestion(
    val id: String,
    val text: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String
)

data class PYPPaper(
    val id: String,
    val category: String, // "Agniveer GD", "UP Police Constable", "UP Police SI", "Railway"
    val year: String, // e.g., "2023"
    val topic: String, // e.g., "Simplification", "Ratios", "Percentages"
    val title: String,
    val questions: List<PYPQuestion>
)

class MathLearnViewModel(application: Application) : AndroidViewModel(application) {

    private val database = MathLearnDatabase.getDatabase(application)
    private val dao = database.mathLearnDao()

    // Navigation state
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Welcome)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // App Preferences / Settings
    var isDarkTheme by mutableStateOf(true)
    var notificationPermissionApproved by mutableStateOf(true)
    var mockNegativeMarkingActive by mutableStateOf(true)

    // Running Notice Text
    val runningNoticeText = "🚀 New Feature: Added 1-1000 Square Roots, Cubes, and Squares tables with high-speed search filters! Complete Daily Quizzes to secure your spot on the top leaderboard. Study plan reminders are now live! 🚀"

    // Live Flowing Room Databases
    val timetableList: StateFlow<List<TimetableItem>> = dao.getTimetableFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quizResultList: StateFlow<List<QuizResult>> = dao.getQuizResultsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mockResultList: StateFlow<List<QuizResult>> = dao.getMockTestResultsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoritesList: StateFlow<List<FavoriteItem>> = dao.getFavoritesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val achievementList: StateFlow<List<Achievement>> = dao.getAchievementsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Quiz/Mock state
    private val _quizState = MutableStateFlow(QuizSessionState())
    val quizState: StateFlow<QuizSessionState> = _quizState.asStateFlow()

    // Active PYP State
    var selectedPaper by mutableStateOf<PYPPaper?>(null)
    var selectedPaperAnswers = mutableStateOf<Map<String, String>>(emptyMap())
    var paperSubmitted by mutableStateOf(false)

    // Notifications simulated
    private val _inAppNotifications = MutableStateFlow<List<String>>(
        listOf(
            "Welcome to Math Learn! Let's start with tables practicing.",
            "Study Tip: Try practicing Squares 1-50 today to boost calculation speeds!",
            "Daily Goal: Complete 1 full Arithmetic Quiz today!"
        )
    )
    val inAppNotifications: StateFlow<List<String>> = _inAppNotifications.asStateFlow()

    init {
        prepopulateAchievements()
    }

    private fun prepopulateAchievements() {
        viewModelScope.launch {
            val existing = dao.getAchievementsFlow().first()
            if (existing.isEmpty()) {
                val defaults = listOf(
                    Achievement("streak_1", "First Contact", "Open the app and complete a learning practice session.", false),
                    Achievement("streak_3", "Dedicated Mind", "Unlock a 3-day continuous study habit.", false),
                    Achievement("quiz_perfect", "Elite Perfectionist", "Achieve a perfect 100% score on any Quiz.", false),
                    Achievement("rapid_60", "Lightning Solver", "Complete any Quiz in under 60 seconds with 80%+ accuracy.", false),
                    Achievement("hard_conqueror", "Grandmaster", "Score 90%+ on a Hard difficulty Quiz.", false),
                    Achievement("mock_warrior", "Battle Tested", "Submit a complete Mock Test with negative marking active.", false)
                )
                dao.insertAchievements(defaults)
            }
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    // Toggle Favorite
    fun toggleFavorite(type: String, itemKey: String) {
        viewModelScope.launch {
            val list = favoritesList.value
            val exists = list.any { it.type == type && it.itemKey == itemKey }
            if (exists) {
                dao.deleteFavorite(type, itemKey)
                showToast("Removed from Favorites")
            } else {
                dao.insertFavorite(FavoriteItem(type = type, itemKey = itemKey))
                showToast("Added to Favorites!")
                checkAchievementUnlock("streak_1")
            }
        }
    }

    // Notification Simulator
    fun addNotification(message: String) {
        val current = _inAppNotifications.value.toMutableList()
        current.add(0, message)
        _inAppNotifications.value = current
    }

    fun clearNotifications() {
        _inAppNotifications.value = emptyList()
    }

    // Achievements engine check
    fun checkAchievementUnlock(id: String) {
        viewModelScope.launch {
            val list = achievementList.value
            val match = list.find { it.id == id }
            if (match != null && !match.unlocked) {
                dao.updateAchievementStatus(id, true, System.currentTimeMillis())
                addNotification("🏆 ACHIEVEMENT UNLOCKED: ${match.title}! (${match.description})")
                showToast("Achievement unlocked: ${match.title}! 🎉")
            }
        }
    }

    // Timetable Functions
    fun addTimetableItem(title: String, subject: String, targetHours: Double, timeString: String, dayOfWeek: String) {
        viewModelScope.launch {
            dao.insertTimetableItem(
                TimetableItem(
                    title = title,
                    subject = subject,
                    targetHours = targetHours,
                    timeString = timeString,
                    dayOfWeek = dayOfWeek
                )
            )
            addNotification("📅 Timetable task added: $title at $timeString")
            showToast("Study target scheduled!")
        }
    }

    fun toggleTimetableItem(id: Int, completed: Boolean) {
        viewModelScope.launch {
            dao.updateTimetableStatus(id, completed)
            if (completed) {
                addNotification("✅ Timetable task completed!")
                showToast("Task completed! Well done.")
                checkAchievementUnlock("streak_1")
            }
        }
    }

    fun deleteTimetableItem(id: Int) {
        viewModelScope.launch {
            dao.deleteTimetableItem(id)
            showToast("Study target deleted")
        }
    }

    // --- QUIZ & MOCK TEST GENERATOR ENGINE ---
    fun startNewQuiz(category: String, difficulty: String, isMock: Boolean = false) {
        val generatedQuestions = generateQuizQuestions(category, difficulty, isMock)
        val limit = if (isMock) 1200 else 300 // 20 mins for mock, 5 mins for quiz
        
        _quizState.value = QuizSessionState(
            category = category,
            difficulty = difficulty,
            questions = generatedQuestions,
            currentQuestionIndex = 0,
            score = 0,
            answers = List(generatedQuestions.size) { null },
            timeLimitSeconds = limit,
            secondsLeft = limit,
            isFinished = false,
            isMock = isMock,
            negativeMarking = if (isMock) mockNegativeMarkingActive else false
        )
        
        if (isMock) {
            navigateTo(Screen.MockTestPlay)
        } else {
            navigateTo(Screen.QuizPlay)
        }
    }

    fun submitAnswer(answer: String?) {
        val state = _quizState.value
        val index = state.currentQuestionIndex
        val updatedAnswers = state.answers.toMutableList()
        updatedAnswers[index] = answer

        _quizState.value = state.copy(
            answers = updatedAnswers
        )
    }

    fun nextQuestion() {
        val state = _quizState.value
        if (state.currentQuestionIndex < state.questions.size - 1) {
            _quizState.value = state.copy(
                currentQuestionIndex = state.currentQuestionIndex + 1
            )
        } else {
            finishQuiz()
        }
    }

    fun previousQuestion() {
        val state = _quizState.value
        if (state.currentQuestionIndex > 0) {
            _quizState.value = state.copy(
                currentQuestionIndex = state.currentQuestionIndex - 1
            )
        }
    }

    fun setCurrentQuestionIndex(index: Int) {
        val state = _quizState.value
        if (index in 0 until state.questions.size) {
            _quizState.value = state.copy(
                currentQuestionIndex = index
            )
        }
    }

    fun tickTimer() {
        val state = _quizState.value
        if (state.secondsLeft > 1 && !state.isFinished) {
            _quizState.value = state.copy(secondsLeft = state.secondsLeft - 1)
        } else if (state.secondsLeft == 1 && !state.isFinished) {
            _quizState.value = state.copy(secondsLeft = 0)
            finishQuiz()
        }
    }

    fun finishQuiz() {
        val state = _quizState.value
        if (state.isFinished) return

        var correctCount = 0
        var wrongCount = 0
        var skippedCount = 0

        // Detailed analytics topic tracker
        val categoryScores = mutableMapOf<String, Int>() // Category to total correct
        val categoryCounts = mutableMapOf<String, Int>() // Category to total occurrences

        state.questions.forEachIndexed { i, q ->
            val userAns = state.answers[i]
            categoryCounts[q.category] = (categoryCounts[q.category] ?: 0) + 1
            if (userAns == null) {
                skippedCount++
            } else if (userAns == q.correctAnswer) {
                correctCount++
                categoryScores[q.category] = (categoryScores[q.category] ?: 0) + 1
            } else {
                wrongCount++
            }
        }

        // Score logic
        // Standard quiz: 10 points per correct answer, no penalty.
        // Mock test: 10 points per correct, optional negative marking (e.g. -2.5)
        var totalScore = correctCount * 10
        if (state.negativeMarking) {
            totalScore -= (wrongCount * 2.5).toInt()
            if (totalScore < 0) totalScore = 0
        }

        val pct = (correctCount.toDouble() / state.questions.size) * 100
        val timeSpent = state.timeLimitSeconds - state.secondsLeft

        // Compute Strong and Weak topics
        val strongList = mutableListOf<String>()
        val weakList = mutableListOf<String>()

        categoryCounts.forEach { (cat, count) ->
            val score = categoryScores[cat] ?: 0
            val ratio = score.toDouble() / count
            if (ratio >= 0.8) {
                strongList.add(cat)
            } else if (ratio < 0.6) {
                weakList.add(cat)
            }
        }

        if (strongList.isEmpty() && correctCount > 0) {
            strongList.add(state.category)
        }
        if (weakList.isEmpty() && wrongCount > 0) {
            weakList.add(state.category)
        }

        val strongStr = if (strongList.isNotEmpty()) strongList.joinToString(", ") else "None"
        val weakStr = if (weakList.isNotEmpty()) weakList.joinToString(", ") else "None"

        val result = QuizResult(
            category = state.category,
            difficulty = state.difficulty,
            score = totalScore,
            totalQuestions = state.questions.size,
            percentage = pct,
            timeTakenSeconds = timeSpent,
            isMockTest = state.isMock,
            weakTopics = weakStr,
            strongTopics = strongStr
        )

        viewModelScope.launch {
            dao.insertQuizResult(result)
            
            // Achievement checkers
            if (pct >= 100.0) {
                checkAchievementUnlock("quiz_perfect")
            }
            if (timeSpent < 60 && pct >= 80.0) {
                checkAchievementUnlock("rapid_60")
            }
            if (state.difficulty == "Hard" && pct >= 90.0) {
                checkAchievementUnlock("hard_conqueror")
            }
            if (state.isMock) {
                checkAchievementUnlock("mock_warrior")
            }

            // Streak dynamic check (if we studied today, calculate previous results)
            val history = dao.getQuizResultsFlow().first()
            if (history.size >= 3) {
                checkAchievementUnlock("streak_3")
            }
        }

        _quizState.value = state.copy(
            score = totalScore,
            isFinished = true
        )

        if (state.isMock) {
            navigateTo(Screen.MockTestResultScreen)
        } else {
            navigateTo(Screen.QuizResultScreen)
        }
    }

    private fun generateQuizQuestions(category: String, difficulty: String, isMock: Boolean): List<Question> {
        val count = if (isMock) 15 else 10
        val questions = mutableListOf<Question>()

        // Potential question topics for full coverage
        val subCategories = if (category == "Mixed Math" || isMock) {
            listOf("Tables", "Squares", "Roots", "Cubes", "Addition", "Subtraction", "Multiplication", "Division")
        } else {
            listOf(category)
        }

        for (i in 1..count) {
            val subCat = subCategories.random()
            questions.add(generateSingleQuestion(subCat, difficulty))
        }

        return questions
    }

    private fun generateSingleQuestion(category: String, difficulty: String): Question {
        val range = when (difficulty) {
            "Easy" -> 2..15
            "Medium" -> 12..35
            else -> 25..90
        }

        val operandRange = when (difficulty) {
            "Easy" -> 1..30
            "Medium" -> 20..150
            else -> 100..800
        }

        var text = ""
        var correct = ""
        var explanation = ""
        val options = mutableListOf<String>()

        when (category) {
            "Tables" -> {
                val num = range.random()
                val mult = (1..20).random()
                val ans = num * mult
                text = "What is the product of $num × $mult?"
                correct = ans.toString()
                explanation = "Table multiplication: $num multiplied by $mult yields exactly $ans."
                
                options.add(correct)
                options.add((ans + num).toString())
                options.add((ans - num).toString())
                options.add((ans + 10).toString())
            }
            "Squares" -> {
                val num = when (difficulty) {
                    "Easy" -> (1..25).random()
                    "Medium" -> (25..60).random()
                    else -> (60..150).random()
                }
                val ans = num * num
                text = "Calculate the square of $num ($num²):"
                correct = ans.toString()
                explanation = "$num² means $num × $num, which equals $ans."

                options.add(correct)
                options.add((ans + num).toString())
                options.add((ans - num * 2).toString())
                options.add((ans + 100).toString())
            }
            "Cubes" -> {
                val num = when (difficulty) {
                    "Easy" -> (1..10).random()
                    "Medium" -> (11..25).random()
                    else -> (25..45).random()
                }
                val ans = num * num * num
                text = "Calculate the cube of $num ($num³):"
                correct = ans.toString()
                explanation = "$num³ means $num × $num × $num, which equals $ans."

                options.add(correct)
                options.add((ans + (num * num)).toString())
                options.add((ans - 100).toString())
                options.add((ans + 1000).toString())
            }
            "Roots" -> {
                val num = when (difficulty) {
                    "Easy" -> listOf(4, 9, 16, 25, 36, 49, 64, 81, 100, 121, 144).random()
                    "Medium" -> listOf(169, 196, 225, 256, 289, 324, 361, 400, 625, 900).random()
                    else -> (100..999).random()
                }
                val root = sqrt(num.toDouble())
                val isPerfect = root == root.toInt().toDouble()
                
                if (isPerfect) {
                    text = "What is the square root of $num (√$num)?"
                    correct = root.toInt().toString()
                    explanation = "√$num is perfect: $correct × $correct = $num."
                    
                    options.add(correct)
                    options.add((root.toInt() + 1).toString())
                    options.add((root.toInt() - 1).toString())
                    options.add((root.toInt() + 5).toString())
                } else {
                    text = "Approximate the square root of $num (√$num) to 4 decimal places:"
                    correct = String.format(Locale.US, "%.4f", root)
                    explanation = "√$num is approximately $correct because $correct × $correct ≈ $num."

                    options.add(correct)
                    options.add(String.format(Locale.US, "%.4f", root + 0.123))
                    options.add(String.format(Locale.US, "%.4f", root - 0.084))
                    options.add(String.format(Locale.US, "%.4f", root + 0.456))
                }
            }
            "Addition" -> {
                val a = operandRange.random()
                val b = operandRange.random()
                val ans = a + b
                text = "Find the sum of: $a + $b"
                correct = ans.toString()
                explanation = "Adding $a and $b gives $ans."

                options.add(correct)
                options.add((ans + 10).toString())
                options.add((ans - 10).toString())
                options.add((ans + 100).toString())
            }
            "Subtraction" -> {
                val a = operandRange.random()
                val b = (1..a).random()
                val ans = a - b
                text = "Calculate: $a - $b"
                correct = ans.toString()
                explanation = "Subtracting $b from $a equals $ans."

                options.add(correct)
                options.add((ans + 10).toString())
                options.add((ans - 10).toString())
                options.add((ans + 5).toString())
            }
            "Multiplication" -> {
                val a = range.random()
                val b = when (difficulty) {
                    "Easy" -> (2..12).random()
                    "Medium" -> (10..30).random()
                    else -> (30..100).random()
                }
                val ans = a * b
                text = "Calculate the product: $a × $b"
                correct = ans.toString()
                explanation = "Multiplying $a by $b equals $ans."

                options.add(correct)
                options.add((ans + a).toString())
                options.add((ans - b).toString())
                options.add((ans + 50).toString())
            }
            else -> { // Division
                val b = range.random()
                val ans = when (difficulty) {
                    "Easy" -> (1..10).random()
                    "Medium" -> (10..30).random()
                    else -> (25..60).random()
                }
                val a = b * ans
                text = "Find the quotient of: $a ÷ $b"
                correct = ans.toString()
                explanation = "$a divided by $b equals $ans because $b × $ans = $a."

                options.add(correct)
                options.add((ans + 1).toString())
                options.add((ans - 1).toString())
                options.add((ans + 5).toString())
            }
        }

        // Shuffle options dynamically
        return Question(
            category = category,
            text = text,
            options = options.distinct().shuffled(),
            correctAnswer = correct,
            explanation = explanation
        )
    }

    // --- PREVIOUS YEAR PAPERS REPOSITORY DATA ---
    val pypPapersList = listOf(
        PYPPaper(
            id = "agniveer_gd_2023",
            category = "Agniveer GD",
            year = "2023",
            topic = "General Mathematics Mix",
            title = "Agniveer GD Soldier Mathematics (2023)",
            questions = listOf(
                PYPQuestion(
                    "ag_1",
                    "A person buys a radio for ₹450 and sells it for ₹540. Find his profit percentage.",
                    listOf("15%", "18%", "20%", "25%"),
                    "20%",
                    "Profit = Selling Price - Cost Price = 540 - 450 = ₹90. Profit % = (Profit / Cost Price) * 100 = (90 / 450) * 100 = 20%."
                ),
                PYPQuestion(
                    "ag_2",
                    "If a car travels at a speed of 54 km/h, what is its speed in meters per second (m/s)?",
                    listOf("12 m/s", "15 m/s", "18 m/s", "20 m/s"),
                    "15 m/s",
                    "To convert km/h to m/s, multiply by 5/18. Hence, Speed = 54 * (5/18) = 3 * 5 = 15 m/s."
                ),
                PYPQuestion(
                    "ag_3",
                    "The average weight of 5 boys is 50 kg. If a new boy weighing 62 kg joins them, find the new average weight.",
                    listOf("51 kg", "52 kg", "53 kg", "54 kg"),
                    "52 kg",
                    "Total weight of 5 boys = 5 * 50 = 250 kg. New total weight = 250 + 62 = 312 kg. New average = 312 / 6 = 52 kg."
                )
            )
        ),
        PYPPaper(
            id = "up_constable_2024",
            category = "UP Police Constable",
            year = "2024",
            topic = "Number System & Ratio",
            title = "UP Police Constable Arithmetic Paper (2024)",
            questions = listOf(
                PYPQuestion(
                    "upc_1",
                    "What is the value of (0.35 * 0.35 - 0.15 * 0.15) / 0.20?",
                    listOf("0.20", "0.35", "0.50", "0.75"),
                    "0.50",
                    "Use algebraic identity: a² - b² = (a-b)(a+b). Here, a=0.35, b=0.15. Numerator = (0.35 - 0.15)(0.35 + 0.15) = 0.20 * 0.50. Dividing by 0.20 yields exactly 0.50."
                ),
                PYPQuestion(
                    "upc_2",
                    "Two numbers are in the ratio 3:4. If their HCF is 4, find their LCM.",
                    listOf("12", "24", "36", "48"),
                    "48",
                    "The numbers are 3*HCF and 4*HCF, i.e., 3*4=12 and 4*4=16. LCM of 12 and 16 = 48. Alternatively, LCM = Ratio1 * Ratio2 * HCF = 3 * 4 * 4 = 48."
                )
            )
        ),
        PYPPaper(
            id = "up_si_2021",
            category = "UP Police SI",
            year = "2021",
            topic = "Compound Interest",
            title = "UP Police SI Quantitive Aptitude (2021)",
            questions = listOf(
                PYPQuestion(
                    "upsi_1",
                    "Find the compound interest on ₹10,000 for 2 years at 10% per annum, compounded annually.",
                    listOf("₹1,000", "₹2,000", "₹2,100", "₹2,200"),
                    "₹2,100",
                    "Formula: Amount = P(1 + R/100)^T = 10000(1.1)^2 = ₹12,100. Compound Interest = Amount - Principal = 12100 - 10000 = ₹2,100."
                ),
                PYPQuestion(
                    "upsi_2",
                    "If A can complete a work in 10 days and B in 15 days, in how many days can they complete the work together?",
                    listOf("5 days", "6 days", "8 days", "9 days"),
                    "6 days",
                    "Total rate together = 1/10 + 1/15 = 5/30 = 1/6. Hence, time taken together is 6 days."
                )
            )
        ),
        PYPPaper(
            id = "railway_group_d",
            category = "Railway",
            year = "2022",
            topic = "Simplification & Algebra",
            title = "RRB Railway Math Practice Set (2022)",
            questions = listOf(
                PYPQuestion(
                    "rw_1",
                    "Find the value of x if: 12% of 1200 + x = 18% of 1500.",
                    listOf("102", "116", "126", "136"),
                    "126",
                    "12% of 1200 = 12 * 12 = 144. 18% of 1500 = 18 * 15 = 270. Therefore, 144 + x = 270 => x = 270 - 144 = 126."
                ),
                PYPQuestion(
                    "rw_2",
                    "If the perimeter of a semi-circle is 36 cm, what is its radius? (Take π = 22/7)",
                    listOf("3.5 cm", "7 cm", "14 cm", "21 cm"),
                    "7 cm",
                    "Perimeter of semi-circle = πr + 2r = r(π + 2). r(22/7 + 2) = 36 => r(36/7) = 36 => r = 7 cm."
                )
            )
        )
    )

    fun openPYPPaper(paper: PYPPaper) {
        selectedPaper = paper
        selectedPaperAnswers.value = emptyMap()
        paperSubmitted = false
        navigateTo(Screen.PYPViewPaper)
    }

    fun submitPYPPaper() {
        paperSubmitted = true
        checkAchievementUnlock("streak_1")
    }

    // --- SYSTEM SETTINGS: EXPORT / IMPORT / RESET ---
    fun resetAllData() {
        viewModelScope.launch {
            dao.clearQuizResults()
            // Clear timetables
            val timetables = dao.getTimetableFlow().first()
            timetables.forEach { dao.deleteTimetableItem(it.id) }
            
            // Clear favorites
            val favorites = dao.getFavoritesFlow().first()
            favorites.forEach { dao.deleteFavorite(it.type, it.itemKey) }

            // Re-lock achievements
            val list = dao.getAchievementsFlow().first()
            list.forEach { dao.updateAchievementStatus(it.id, false, null) }

            showToast("All progress and study logs reset successfully.")
            addNotification("⚠️ System reset complete. All local logs are wiped.")
        }
    }

    fun exportBackup(): String {
        return try {
            val rootObj = JSONObject()
            
            // Serialize quiz results
            val quizArray = JSONArray()
            quizResultList.value.forEach {
                val obj = JSONObject()
                obj.put("category", it.category)
                obj.put("difficulty", it.difficulty)
                obj.put("score", it.score)
                obj.put("totalQuestions", it.totalQuestions)
                obj.put("percentage", it.percentage)
                obj.put("timestamp", it.timestamp)
                obj.put("timeTakenSeconds", it.timeTakenSeconds)
                obj.put("isMockTest", it.isMockTest)
                obj.put("weakTopics", it.weakTopics)
                obj.put("strongTopics", it.strongTopics)
                quizArray.put(obj)
            }
            rootObj.put("quizzes", quizArray)

            // Serialize timetable
            val timetableArray = JSONArray()
            timetableList.value.forEach {
                val obj = JSONObject()
                obj.put("title", it.title)
                obj.put("subject", it.subject)
                obj.put("targetHours", it.targetHours)
                obj.put("timeString", it.timeString)
                obj.put("dayOfWeek", it.dayOfWeek)
                obj.put("completed", it.completed)
                timetableArray.put(obj)
            }
            rootObj.put("timetable", timetableArray)

            // Serialize favorites
            val favoritesArray = JSONArray()
            favoritesList.value.forEach {
                val obj = JSONObject()
                obj.put("type", it.type)
                obj.put("itemKey", it.itemKey)
                obj.put("timestamp", it.timestamp)
                favoritesArray.put(obj)
            }
            rootObj.put("favorites", favoritesArray)

            rootObj.toString(2)
        } catch (e: Exception) {
            ""
        }
    }

    fun importBackup(jsonString: String): Boolean {
        return try {
            val rootObj = JSONObject(jsonString)
            
            viewModelScope.launch {
                // Import quizzes
                if (rootObj.has("quizzes")) {
                    val array = rootObj.getJSONArray("quizzes")
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        dao.insertQuizResult(
                            QuizResult(
                                category = obj.getString("category"),
                                difficulty = obj.getString("difficulty"),
                                score = obj.getInt("score"),
                                totalQuestions = obj.getInt("totalQuestions"),
                                percentage = obj.getDouble("percentage"),
                                timestamp = obj.getLong("timestamp"),
                                timeTakenSeconds = obj.getInt("timeTakenSeconds"),
                                isMockTest = obj.optBoolean("isMockTest", false),
                                weakTopics = obj.optString("weakTopics", ""),
                                strongTopics = obj.optString("strongTopics", "")
                            )
                        )
                    }
                }

                // Import timetable
                if (rootObj.has("timetable")) {
                    val array = rootObj.getJSONArray("timetable")
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        dao.insertTimetableItem(
                            TimetableItem(
                                title = obj.getString("title"),
                                subject = obj.getString("subject"),
                                targetHours = obj.getDouble("targetHours"),
                                timeString = obj.getString("timeString"),
                                dayOfWeek = obj.getString("dayOfWeek"),
                                completed = obj.getBoolean("completed")
                            )
                        )
                    }
                }

                // Import favorites
                if (rootObj.has("favorites")) {
                    val array = rootObj.getJSONArray("favorites")
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        dao.insertFavorite(
                            FavoriteItem(
                                type = obj.getString("type"),
                                itemKey = obj.getString("itemKey"),
                                timestamp = obj.getLong("timestamp")
                            )
                        )
                    }
                }
                
                showToast("Data backup restored successfully!")
                addNotification("📥 Successfully restored progress database from local backup.")
                checkAchievementUnlock("streak_1")
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show()
    }
}
