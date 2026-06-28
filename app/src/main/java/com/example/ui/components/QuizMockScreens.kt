package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.MathLearnViewModel
import com.example.viewmodel.Question
import com.example.viewmodel.Screen
import kotlinx.coroutines.delay

// --- Quiz Configuration / Category Picker ---
@Composable
fun QuizConfigScreen(viewModel: MathLearnViewModel) {
    var selectedCategory by remember { mutableStateOf("Mixed Math") }
    var selectedDifficulty by remember { mutableStateOf("Easy") }

    val categories = listOf("Mixed Math", "Tables", "Squares", "Roots", "Cubes", "Addition", "Subtraction", "Multiplication", "Division")
    val difficulties = listOf("Easy", "Medium", "Hard")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        FeatureHeader(
            title = "Test Your Skills",
            onBack = { viewModel.navigateTo(Screen.Dashboard) }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Category Section
            item {
                Text(
                    text = "Select Practice Category",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.height(280.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(categories) { _, cat ->
                        val isSelected = selectedCategory == cat
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            border = if (isSelected) BorderStroke(2.dp, IndigoPrimary) else null,
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) IndigoPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clickable { selectedCategory = cat }
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cat,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            }

            // Difficulty Section
            item {
                Text(
                    text = "Select Difficulty Level",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    difficulties.forEach { diff ->
                        val isSelected = selectedDifficulty == diff
                        val color = when (diff) {
                            "Easy" -> EmeraldSuccess
                            "Medium" -> AmberWarning
                            else -> RoseError
                        }
                        Button(
                            onClick = { selectedDifficulty = diff },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) color else MaterialTheme.colorScheme.surface,
                                contentColor = if (isSelected) Color.White else color
                            ),
                            border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
                        ) {
                            Text(text = diff, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Divider and CTA Info
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = "Info", tint = IndigoPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Exam Format Info",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• Standard Practice Quiz has 10 random questions.\n• Completing quizzes unlocks performance analytics.\n• To play a timed full competitive Mock Paper with negative marking, check out the Mock Test option directly on the Home Screen.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Start Buttons
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.startNewQuiz(selectedCategory, selectedDifficulty, isMock = false) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Text("START ARITHMETIC QUIZ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { viewModel.startNewQuiz("Mixed Math", selectedDifficulty, isMock = true) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, IndigoPrimary)
                ) {
                    Text("START COMPETITIVE MOCK TEST", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = IndigoPrimary)
                }
            }
        }
    }
}

// --- Quiz Active Playing Screen ---
@Composable
fun QuizPlayScreen(viewModel: MathLearnViewModel) {
    val quizState by viewModel.quizState.collectAsState()

    // Timer Ticker Loop
    LaunchedEffect(key1 = quizState.isFinished) {
        while (!quizState.isFinished) {
            delay(1000)
            viewModel.tickTimer()
        }
    }

    val currentQ = quizState.questions.getOrNull(quizState.currentQuestionIndex)
    val minutes = quizState.secondsLeft / 60
    val seconds = quizState.secondsLeft % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Quiz Header Info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${quizState.category} - ${quizState.difficulty}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Question ${quizState.currentQuestionIndex + 1} of ${quizState.questions.size}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Timers
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (quizState.secondsLeft < 60) RoseError.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Timer",
                        tint = if (quizState.secondsLeft < 60) RoseError else IndigoPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formattedTime,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (quizState.secondsLeft < 60) RoseError else MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        // Horizontal Progress Bar
        val progress = (quizState.currentQuestionIndex + 1).toFloat() / quizState.questions.size
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(6.dp),
            color = IndigoPrimary,
            trackColor = MaterialTheme.colorScheme.surface
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (currentQ != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Main Question Text Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentQ.text,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Answer Choices
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    currentQ.options.forEach { option ->
                        val isSelected = quizState.answers[quizState.currentQuestionIndex] == option
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clickable { viewModel.submitAnswer(option) },
                            shape = RoundedCornerShape(16.dp),
                            border = if (isSelected) BorderStroke(2.dp, IndigoPrimary) else null,
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) IndigoPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = option,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.onBackground
                                )
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.submitAnswer(option) },
                                    colors = RadioButtonDefaults.colors(selectedColor = IndigoPrimary)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Bottom Navigation controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.previousQuestion() },
                        enabled = quizState.currentQuestionIndex > 0,
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Previous")
                    }

                    Button(
                        onClick = { viewModel.finishQuiz() },
                        colors = ButtonDefaults.buttonColors(containerColor = RoseError),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("SUBMIT", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    IconButton(
                        onClick = { viewModel.nextQuestion() },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next")
                    }
                }
            }
        }
    }
}

// --- Mock Test Active Playing Screen ---
@Composable
fun MockTestPlayScreen(viewModel: MathLearnViewModel) {
    val quizState by viewModel.quizState.collectAsState()

    // Timer Ticker Loop
    LaunchedEffect(key1 = quizState.isFinished) {
        while (!quizState.isFinished) {
            delay(1000)
            viewModel.tickTimer()
        }
    }

    val currentQ = quizState.questions.getOrNull(quizState.currentQuestionIndex)
    val minutes = quizState.secondsLeft / 60
    val seconds = quizState.secondsLeft % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Exam Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Math Competitive Mock Exam",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Question ${quizState.currentQuestionIndex + 1} of ${quizState.questions.size}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = RoseError.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Timer, contentDescription = "Timer", tint = RoseError)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formattedTime,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoseError
                    )
                }
            }
        }

        // Negative Marking Active Banner
        if (quizState.negativeMarking) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .background(RoseError.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚠️ NEGATIVE MARKING ACTIVE: -2.5 points for incorrect answers. Skip if unsure!",
                    color = RoseError,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (currentQ != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Question Text Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentQ.text,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Options
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currentQ.options.forEach { option ->
                        val isSelected = quizState.answers[quizState.currentQuestionIndex] == option
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clickable { viewModel.submitAnswer(option) },
                            shape = RoundedCornerShape(12.dp),
                            border = if (isSelected) BorderStroke(2.dp, IndigoPrimary) else null,
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) IndigoPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = option,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.onBackground
                                )
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.submitAnswer(option) },
                                    colors = RadioButtonDefaults.colors(selectedColor = IndigoPrimary)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mock Grid Navigation Matrix
                Text(
                    text = "Exam Navigation Grid",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    itemsIndexed(quizState.questions) { index, _ ->
                        val userAns = quizState.answers[index]
                        val isActive = quizState.currentQuestionIndex == index
                        val color = when {
                            isActive -> IndigoPrimary
                            userAns != null -> EmeraldSuccess
                            else -> MaterialTheme.colorScheme.surface
                        }
                        val txtColor = if (isActive || userAns != null) Color.White else MaterialTheme.colorScheme.onBackground

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .background(color, RoundedCornerShape(6.dp))
                                .clickable {
                                    viewModel.setCurrentQuestionIndex(index)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = txtColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.previousQuestion() },
                        enabled = quizState.currentQuestionIndex > 0,
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Previous")
                    }

                    Button(
                        onClick = { viewModel.finishQuiz() },
                        colors = ButtonDefaults.buttonColors(containerColor = RoseError),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("SUBMIT EXAM", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    IconButton(
                        onClick = { viewModel.nextQuestion() },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next")
                    }
                }
            }
        }
    }
}

// --- Quiz/Mock Results Screen ---
@Composable
fun QuizResultScreen(viewModel: MathLearnViewModel, isMock: Boolean) {
    val quizState by viewModel.quizState.collectAsState()

    var showAnswersReview by remember { mutableStateOf(false) }

    val correctCount = quizState.questions.filterIndexed { i, q -> quizState.answers[i] == q.correctAnswer }.size
    val accuracy = if (quizState.questions.isNotEmpty()) {
        (correctCount.toDouble() / quizState.questions.size * 100).toInt()
    } else 0

    val message = when {
        accuracy >= 90 -> "Excellent job! You have stellar computational accuracy. Ready for any contest! 🏆"
        accuracy >= 65 -> "Good job! Focus on your weak topics to achieve a perfect arithmetic layout."
        else -> "Practice makes perfect! Review math tables and squares to boost speeds."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        FeatureHeader(
            title = if (isMock) "Mock Exam Results" else "Practice Quiz Results",
            onBack = { viewModel.navigateTo(Screen.Dashboard) }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Main score circle card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Accuracy Rate",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .background(
                                    Brush.sweepGradient(colors = listOf(IndigoPrimary, CyanSecondary, IndigoPrimary)),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .background(MaterialTheme.colorScheme.surface, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$accuracy%",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = IndigoPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Correct answers: $correctCount / ${quizState.questions.size}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = message,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Suggestions and Analytics
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Performance Diagnostics",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Final Score", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "${quizState.score} Points", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EmeraldSuccess)
                            }
                            Column {
                                Text(text = "Exam Category", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = quizState.category, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }

            // CTAs
            item {
                Button(
                    onClick = { showAnswersReview = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Assignment, contentDescription = "Review")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("REVIEW ANSWERS & SOLUTIONS", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.navigateTo(Screen.Dashboard) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text("BACK TO DASHBOARD", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }
    }

    // Full Answers Review Popup
    if (showAnswersReview) {
        AlertDialog(
            onDismissRequest = { showAnswersReview = false },
            confirmButton = {
                Button(onClick = { showAnswersReview = false }) {
                    Text("DONE")
                }
            },
            title = {
                Text(text = "Correct Solutions & Review", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(480.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(quizState.questions) { index, q ->
                        val userAns = quizState.answers[index]
                        val isCorrect = userAns == q.correctAnswer
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.dp,
                                if (isCorrect) EmeraldSuccess.copy(alpha = 0.5f) else RoseError.copy(alpha = 0.5f)
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Q${index + 1}: ${q.text}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Your Answer: ${userAns ?: "SKIPPED"}",
                                    fontSize = 12.sp,
                                    color = if (isCorrect) EmeraldSuccess else RoseError,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Correct Option: ${q.correctAnswer}",
                                    fontSize = 12.sp,
                                    color = EmeraldSuccess,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "Explanation: ${q.explanation}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}
