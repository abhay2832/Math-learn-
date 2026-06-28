package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import com.example.viewmodel.MathLearnViewModel
import com.example.viewmodel.Screen
import kotlinx.coroutines.delay

// --- Splash / Welcome Screen ---
@Composable
fun WelcomeScreen(viewModel: MathLearnViewModel) {
    var visible by remember { mutableStateOf(false) }
    var buttonScale by remember { mutableStateOf(1f) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Animated Math geometric icon
            AnimatedVisibility(
                visible = visible,
                enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn()
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(IndigoPrimary, CyanSecondary)
                            ),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .shadow(16.dp, RoundedCornerShape(32.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "√x",
                            color = Color.White,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "+ - × ÷",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Title
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(initialOffsetY = { 50 }) + fadeIn()
            ) {
                Text(
                    text = "Math Learn",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(initialOffsetY = { 80 }) + fadeIn()
            ) {
                Text(
                    text = "Professional Offline Mathematics Hub",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            // Enter Button
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(initialOffsetY = { 110 }) + fadeIn()
            ) {
                Button(
                    onClick = {
                        viewModel.navigateTo(Screen.Dashboard)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(56.dp)
                        .shadow(8.dp, RoundedCornerShape(28.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IndigoPrimary
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "ENTER HUB",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Enter Dashboard",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

// --- Top Announcement Bar ---
@Composable
fun AnnouncementBar(text: String) {
    val scrollState = rememberScrollState()
    
    // Auto scrolls notice continuously
    LaunchedEffect(key1 = scrollState.maxValue) {
        if (scrollState.maxValue > 0) {
            while (true) {
                scrollState.animateScrollTo(
                    value = scrollState.maxValue,
                    animationSpec = tween(durationMillis = 15000, easing = LinearEasing)
                )
                delay(100)
                scrollState.scrollTo(0)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        IndigoPrimary.copy(alpha = 0.9f),
                        VioletTertiary.copy(alpha = 0.9f)
                    )
                )
            )
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.horizontalScroll(scrollState, enabled = false),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

// --- Header / Top Bar ---
@Composable
fun DashboardHeader(
    viewModel: MathLearnViewModel,
    onNotificationsClick: () -> Unit
) {
    val notifications by viewModel.inAppNotifications.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Welcome Back,",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Math Scholar 🎓",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Notification Icon with Badge
            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        CircleShape
                    )
            ) {
                Box {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                    if (notifications.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(RoseError, CircleShape)
                                .align(Alignment.TopEnd)
                        )
                    }
                }
            }

            // Dark Mode Toggle
            IconButton(
                onClick = { viewModel.isDarkTheme = !viewModel.isDarkTheme },
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (viewModel.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Dark Mode",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            // Settings Shortcut
            IconButton(
                onClick = { viewModel.navigateTo(Screen.Settings) },
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

// --- Quick Stats Banner ---
@Composable
fun QuickStatsBanner(viewModel: MathLearnViewModel) {
    val results by viewModel.quizResultList.collectAsState()
    val timetables by viewModel.timetableList.collectAsState()

    val totalCompletedQuizzes = results.size
    val activeStudyTargets = timetables.count { !it.completed }
    val avgScore = if (results.isNotEmpty()) {
        results.map { it.percentage }.average().toInt()
    } else 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$totalCompletedQuizzes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = IndigoPrimary
            )
            Text(
                text = "Quizzes Done",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Divider(
            modifier = Modifier
                .height(30.dp)
                .width(1.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$avgScore%",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldSuccess
            )
            Text(
                text = "Avg Accuracy",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Divider(
            modifier = Modifier
                .height(30.dp)
                .width(1.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$activeStudyTargets",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AmberWarning
            )
            Text(
                text = "Study Targets",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --- Dashboard Navigation Hub (Grid) ---
data class DashboardItem(
    val screen: Screen,
    val title: String,
    val icon: ImageVector,
    val startColor: Color,
    val endColor: Color,
    val description: String
)

@Composable
fun DashboardGrid(viewModel: MathLearnViewModel) {
    val items = listOf(
        DashboardItem(Screen.Tables, "Math Tables", Icons.Default.GridOn, IndigoPrimary, CyanSecondary, "Multiples from 2 to 1000"),
        DashboardItem(Screen.Squares, "Squares 1-1000", Icons.Default.ViewModule, CyanSecondary, VioletTertiary, "Calculate and search squares"),
        DashboardItem(Screen.Cubes, "Cubes 1-1000", Icons.Default.Widgets, VioletTertiary, RoseError, "Master cubed math powers"),
        DashboardItem(Screen.Roots, "Roots 1-1000", Icons.Default.Functions, RoseError, AmberWarning, "Find square root decimals"),
        DashboardItem(Screen.QuizConfig, "Daily Quiz", Icons.Default.PlayArrow, EmeraldSuccess, IndigoPrimary, "Random arithmetic challenges"),
        DashboardItem(Screen.PYPPapers, "PYP Exam Papers", Icons.Default.MenuBook, IndigoPrimary, VioletTertiary, "Math from competitive exams"),
        DashboardItem(Screen.Timetable, "Study Timetable", Icons.Default.DateRange, AmberWarning, RoseError, "Schedule daily targets"),
        DashboardItem(Screen.ProgressAnalytics, "Performance Charts", Icons.Default.BarChart, EmeraldSuccess, CyanSecondary, "Canvas graphs and insights"),
        DashboardItem(Screen.Achievements, "Achievements", Icons.Default.Star, AmberWarning, IndigoPrimary, "Collect unlocked badges"),
        DashboardItem(Screen.Settings, "App Settings", Icons.Default.Settings, VioletTertiary, IndigoPrimary, "Data backup & configurations")
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items) { item ->
            DashboardCard(item = item) {
                viewModel.navigateTo(item.screen)
            }
        }
    }
}

@Composable
fun DashboardCard(item: DashboardItem, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(item.startColor.copy(alpha = 0.15f), item.endColor.copy(alpha = 0.05f))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Styled Icon Sphere
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Brush.linearGradient(colors = listOf(item.startColor, item.endColor)),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = item.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = item.description,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

// --- Notification Dialog Center ---
@Composable
fun NotificationCenterDialog(
    viewModel: MathLearnViewModel,
    onDismiss: () -> Unit
) {
    val list by viewModel.inAppNotifications.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notifications Tray",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (list.isEmpty()) {
                    Text(
                        text = "No new reminders. Start practicing math and stay consistent!",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp)
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        list.take(5).forEach { msg ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(IndigoPrimary, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = msg,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.clearNotifications() },
                        colors = ButtonDefaults.buttonColors(containerColor = RoseError),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Clear All Reminders", color = Color.White)
                    }
                }
            }
        }
    }
}
