package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import com.example.viewmodel.MathLearnViewModel
import com.example.viewmodel.PYPPaper
import com.example.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- Previous Year Papers Screen ---
@Composable
fun PYPPapersScreen(viewModel: MathLearnViewModel) {
    var selectedCategoryTab by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf("All", "Agniveer GD", "UP Police Constable", "UP Police SI", "Railway")

    val filteredPapers = viewModel.pypPapersList.filter { paper ->
        (selectedCategoryTab == "All" || paper.category == selectedCategoryTab) &&
        (searchQuery.isEmpty() || paper.title.contains(searchQuery, ignoreCase = true) || paper.topic.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        FeatureHeader(
            title = "Previous Year Papers",
            onBack = { viewModel.navigateTo(Screen.Dashboard) }
        )

        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search papers, topics, or exams...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal Tabs
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategoryTab == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategoryTab = cat },
                    label = { Text(cat, fontWeight = FontWeight.SemiBold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = IndigoPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Papers List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            if (filteredPapers.isEmpty()) {
                item {
                    Text(
                        text = "No papers found matching filters. Check back soon!",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(filteredPapers) { paper ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.openPYPPaper(paper) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = paper.category,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IndigoPrimary
                                )
                                Text(
                                    text = "Year ${paper.year}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = paper.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Topic: ${paper.topic}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${paper.questions.size} Solved Questions",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Solve Sheet ➜",
                                    color = CyanSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- PYP Active Solving Screen ---
@Composable
fun PYPViewPaperScreen(viewModel: MathLearnViewModel) {
    val paper = viewModel.selectedPaper
    val answers by viewModel.selectedPaperAnswers
    val submitted = viewModel.paperSubmitted

    if (paper != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            FeatureHeader(
                title = paper.title,
                onBack = { viewModel.navigateTo(Screen.PYPPapers) }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                itemsIndexed(paper.questions) { index, q ->
                    val userSelection = answers[q.id]
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Question ${index + 1}: ${q.text}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Choices
                            q.options.forEach { opt ->
                                val isSelected = userSelection == opt
                                val color = when {
                                    submitted && opt == q.correctAnswer -> EmeraldSuccess
                                    submitted && isSelected && opt != q.correctAnswer -> RoseError
                                    else -> if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.surfaceVariant
                                }
                                val txtColor = if (submitted && (opt == q.correctAnswer || (isSelected && opt != q.correctAnswer)) || isSelected) Color.White else MaterialTheme.colorScheme.onBackground

                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = color),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable(enabled = !submitted) {
                                            val current = answers.toMutableMap()
                                            current[q.id] = opt
                                            viewModel.selectedPaperAnswers.value = current
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = opt, color = txtColor, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                        if (isSelected && !submitted) {
                                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Checked", tint = Color.White)
                                        }
                                    }
                                }
                            }

                            // Explanation
                            if (submitted) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "Step-by-Step Solution:",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = IndigoPrimary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = q.explanation,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // CTA Submit
                if (!submitted) {
                    item {
                        Button(
                            onClick = { viewModel.submitPYPPaper() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                        ) {
                            Text("SUBMIT AND VIEW KEY EXPLANATIONS", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// --- Timetable Planner Screen ---
@Composable
fun TimetableScreen(viewModel: MathLearnViewModel) {
    val list by viewModel.timetableList.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    // Dialog state controllers
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Arithmetic") }
    var hours by remember { mutableStateOf("1.0") }
    var timeString by remember { mutableStateOf("09:00 AM") }
    var daySelected by remember { mutableStateOf("Everyday") }

    val categories = listOf("Arithmetic", "Multiplication Tables", "Squares & Cubes", "Square Roots", "Exam Mock")

    val completedPercentage = if (list.isNotEmpty()) {
        (list.count { it.completed }.toFloat() / list.size * 100).toInt()
    } else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        FeatureHeader(
            title = "Study Timetable",
            onBack = { viewModel.navigateTo(Screen.Dashboard) }
        ) {
            IconButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.background(IndigoPrimary, CircleShape)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Target", tint = Color.White)
            }
        }

        // Daily Tracker Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Daily Goals Completed: $completedPercentage%",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = completedPercentage.toFloat() / 100,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = EmeraldSuccess,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Timetable listings
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            if (list.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Empty", modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No study targets scheduled yet. Tap '+' to build your custom timetable!",
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                items(list) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (item.completed) MaterialTheme.colorScheme.surface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Checkbox(
                                    checked = item.completed,
                                    onCheckedChange = { viewModel.toggleTimetableItem(item.id, it) },
                                    colors = CheckboxDefaults.colors(checkedColor = EmeraldSuccess)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = item.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = if (item.completed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = "${item.subject} • ${item.timeString} • ${item.targetHours} Hours",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            IconButton(onClick = { viewModel.deleteTimetableItem(item.id) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = RoseError)
                            }
                        }
                    }
                }
            }
        }
    }

    // Add dialog
    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Add Study Target", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Task Title (e.g., Practice Table 17)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Category", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            val selected = category == cat
                            FilterChip(
                                selected = selected,
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = hours,
                            onValueChange = { hours = it },
                            label = { Text("Target Hours") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = timeString,
                            onValueChange = { timeString = it },
                            label = { Text("Time (e.g. 09:00 AM)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (title.isNotEmpty()) {
                                viewModel.addTimetableItem(
                                    title = title,
                                    subject = category,
                                    targetHours = hours.toDoubleOrNull() ?: 1.0,
                                    timeString = timeString,
                                    dayOfWeek = daySelected
                                )
                                title = ""
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("SCHEDULE TARGET", color = Color.White)
                    }
                }
            }
        }
    }
}

// --- Achievements Cabinet ---
@Composable
fun AchievementsScreen(viewModel: MathLearnViewModel) {
    val list by viewModel.achievementList.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        FeatureHeader(
            title = "Locked & Unlocked Badges",
            onBack = { viewModel.navigateTo(Screen.Dashboard) }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            items(list) { ach ->
                val dateStr = if (ach.unlocked && ach.unlockedAt != null) {
                    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                    sdf.format(Date(ach.unlockedAt))
                } else ""

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (ach.unlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    ),
                    border = if (ach.unlocked) BorderStroke(1.5.dp, EmeraldSuccess.copy(alpha = 0.3f)) else null
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icon Badge
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    if (ach.unlocked) EmeraldSuccess.copy(alpha = 0.15f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (ach.unlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                                contentDescription = "Trophy",
                                tint = if (ach.unlocked) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = ach.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (ach.unlocked) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = ach.description,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (ach.unlocked) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Unlocked on: $dateStr",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccess
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- Canvas Performance Analytics Charts ---
@Composable
fun ProgressAnalyticsScreen(viewModel: MathLearnViewModel) {
    val results by viewModel.quizResultList.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        FeatureHeader(
            title = "Performance Diagnostics",
            onBack = { viewModel.navigateTo(Screen.Dashboard) }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Scores history graph
            item {
                Text("Quiz Score Analytics History", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Accuracy Progression (%)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Custom drawn line chart using Canvas
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        ) {
                            val data = results.take(7).reversed().map { it.percentage.toFloat() }
                            val strokeColor = IndigoPrimary
                            val gridColor = Color.Gray.copy(alpha = 0.2f)

                            // Draw vertical grids
                            val steps = 4
                            for (i in 0..steps) {
                                val x = size.width / steps * i
                                drawLine(gridColor, Offset(x, 0f), Offset(x, size.height))
                            }
                            // Draw horizontal grids
                            for (i in 0..steps) {
                                val y = size.height / steps * i
                                drawLine(gridColor, Offset(0f, y), Offset(size.width, y))
                            }

                            if (data.isNotEmpty()) {
                                val path = Path()
                                val xInterval = size.width / (data.size - 1).coerceAtLeast(1)

                                data.forEachIndexed { index, pct ->
                                    val x = xInterval * index
                                    // Invert coordinate because Y goes down in Canvas
                                    val y = size.height - (pct / 100f * size.height)

                                    if (index == 0) {
                                        path.moveTo(x, y)
                                    } else {
                                        path.lineTo(x, y)
                                    }

                                    // Draw node dots
                                    drawCircle(CyanSecondary, 5.dp.toPx(), Offset(x, y))
                                }

                                drawPath(path, strokeColor, style = Stroke(width = 3.dp.toPx()))
                            }
                        }
                    }
                }
            }

            // Category summary breakdown
            item {
                Text("Topic Accuracies Breakdown", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val categories = listOf("Tables", "Squares", "Roots", "Mixed Math")
                        categories.forEach { cat ->
                            val list = results.filter { it.category == cat }
                            val avg = if (list.isNotEmpty()) list.map { it.percentage }.average().toInt() else 0
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = cat, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text(text = "$avg% Accuracy", fontSize = 12.sp, color = IndigoPrimary, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = avg.toFloat() / 100,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp),
                                    color = if (avg >= 75) EmeraldSuccess else if (avg >= 50) AmberWarning else RoseError,
                                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- App Settings Center ---
@Composable
fun SettingsScreen(viewModel: MathLearnViewModel) {
    val context = LocalContext.current

    var importText by remember { mutableStateOf("") }
    var showImportDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        FeatureHeader(
            title = "System Settings",
            onBack = { viewModel.navigateTo(Screen.Dashboard) }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Appearance Preference Group
            item {
                Text("Appearance & Theme", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = IndigoPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Dark Slate Layout Theme")
                        Switch(
                            checked = viewModel.isDarkTheme,
                            onCheckedChange = { viewModel.isDarkTheme = it }
                        )
                    }
                }
            }

            // Database Operations
            item {
                Text("Backup, Restores & Sync", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = IndigoPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Export
                        Button(
                            onClick = {
                                val data = viewModel.exportBackup()
                                copyToClipboard(context, data)
                                Toast.makeText(context, "Database backup JSON copied to Clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.CloudUpload, contentDescription = "Export")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("COPY DB BACKUP JSON", color = Color.White)
                            }
                        }

                        // Import
                        Button(
                            onClick = { showImportDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanSecondary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.CloudDownload, contentDescription = "Import")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("RESTORE FROM JSON STRING", color = Color.White)
                            }
                        }
                    }
                }
            }

            // Danger Wipe Data
            item {
                Text("System Control", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = RoseError)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.resetAllData()
                        Toast.makeText(context, "Database progress wiped clean.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = RoseError),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = "Wipe")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("RESET AND CLEAR ALL PROGRESS", color = Color.White)
                    }
                }
            }
        }
    }

    // Import Dialog Popup
    if (showImportDialog) {
        Dialog(onDismissRequest = { showImportDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Paste DB Backup JSON", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importText,
                        onValueChange = { importText = it },
                        label = { Text("Backup JSON String") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        maxLines = 10
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val success = viewModel.importBackup(importText)
                            if (success) {
                                showImportDialog = false
                                importText = ""
                            } else {
                                Toast.makeText(context, "Error: Invalid backup JSON pattern.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("RESTORE BACKUP PROGRESS", color = Color.White)
                    }
                }
            }
        }
    }
}
