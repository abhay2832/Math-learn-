package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.viewmodel.MathLearnViewModel
import com.example.viewmodel.Screen

@Composable
fun MathLearnApp(viewModel: MathLearnViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    var showNotificationTray by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    Screen.Welcome -> WelcomeScreen(viewModel)
                    Screen.Dashboard -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Continuous Horizontal Scrolling Marquee announcement bar
                            AnnouncementBar(text = viewModel.runningNoticeText)
                            
                            DashboardHeader(
                                viewModel = viewModel,
                                onNotificationsClick = { showNotificationTray = true }
                            )
                            
                            QuickStatsBanner(viewModel = viewModel)
                            
                            DashboardGrid(viewModel = viewModel)
                        }
                    }
                    Screen.Tables -> TablesScreen(viewModel)
                    Screen.Squares -> SquaresScreen(viewModel)
                    Screen.Cubes -> CubesScreen(viewModel)
                    Screen.Roots -> RootsScreen(viewModel)
                    Screen.QuizConfig -> QuizConfigScreen(viewModel)
                    Screen.QuizPlay -> QuizPlayScreen(viewModel)
                    Screen.QuizResultScreen -> QuizResultScreen(viewModel, isMock = false)
                    Screen.MockTestPlay -> MockTestPlayScreen(viewModel)
                    Screen.MockTestResultScreen -> QuizResultScreen(viewModel, isMock = true)
                    Screen.PYPPapers -> PYPPapersScreen(viewModel)
                    Screen.PYPViewPaper -> PYPViewPaperScreen(viewModel)
                    Screen.Timetable -> TimetableScreen(viewModel)
                    Screen.ProgressAnalytics -> ProgressAnalyticsScreen(viewModel)
                    Screen.Achievements -> AchievementsScreen(viewModel)
                    Screen.Settings -> SettingsScreen(viewModel)
                }
            }

            // Overlay RemindersTray Dialog
            if (showNotificationTray) {
                NotificationCenterDialog(
                    viewModel = viewModel,
                    onDismiss = { showNotificationTray = false }
                )
            }
        }
    }
}
