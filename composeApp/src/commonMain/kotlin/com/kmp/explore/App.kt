package com.kmp.explore

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kmp.explore.ui.HomeScreen
import com.kmp.explore.ui.PictureDetailScreen
import com.kmp.explore.ui.TodayScreen
import com.kmp.explore.ui.FindScreen
import com.kmp.explore.models.ApodResponse
import org.koin.compose.KoinApplication
import com.kmp.explore.di.sharedModule
import com.kmp.explore.network.NetworkConfig

@Composable
fun App() {
    KoinApplication(application = {
        modules(sharedModule)
    }) {
        val navController = rememberNavController()

        var selectedApod by remember { mutableStateOf<ApodResponse?>(null) }

        val currentDestination by navController.currentBackStackEntryFlow.collectAsState(
            initial = navController.currentBackStackEntry
        )
        val currentRoute = currentDestination?.destination?.route

        val showBottomBar = currentRoute != "details"

        MaterialTheme {
            Scaffold(
                bottomBar = {
                    if (showBottomBar) {
                        BottomTabBar(
                            currentScreen = currentRoute ?: "home",
                            onTabSelected = { route ->
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            ) { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = "home",
                    modifier = if (showBottomBar) Modifier.padding(paddingValues) else Modifier
                ) {
                    composable("home") {
                        getLogger().d("Nav", "Rendering ApodListScreen")
                        HomeScreen(
                            onApodClick = { apod ->
                                selectedApod = apod
                                navController.navigate("details")
                            }
                        )
                    }

                    composable("today") {
                        getLogger().d("Nav", "Rendering TodayScreen")
                        TodayScreen(
                            onApodClick = { apod ->
                                selectedApod = apod
                                navController.navigate("details")
                            }
                        )
                    }

                    composable("find") {
                        getLogger().d("Nav", "Rendering FindScreen")
                        FindScreen(
                            onApodClick = { apod ->
                                selectedApod = apod
                                navController.navigate("details")
                            }
                        )
                    }

                    composable("details") {
                        selectedApod?.let { apod ->
                            PictureDetailScreen(
                                apod = apod,
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }

                    composable("about") {
                        getLogger().d("Nav", "Rendering AboutScreen")
                        AboutScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun AboutScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About APOD") },
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "🌌 Astronomy Picture of the Day",
                style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.primary
            )

            Text(
                "Discover the cosmos! Each day NASA features a different image or photograph of our fascinating universe, along with a brief explanation written by a professional astronomer.",
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "This app is built with:",
                style = MaterialTheme.typography.h6
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("• Kotlin Multiplatform")
                Text("• Jetpack Compose")
                Text("• NASA APOD API")
                Text("• Ktor Server")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Current server: ${NetworkConfig.DEFAULT_BASE_URL}",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun BottomTabBar(currentScreen: String, onTabSelected: (String) -> Unit) {
    BottomNavigation {
        BottomNavigationItem(
            selected = currentScreen == "home",
            onClick = { onTabSelected("home") },
            icon = { Text("🏠") },
            label = { Text("Home") }
        )
        BottomNavigationItem(
            selected = currentScreen == "today",
            onClick = { onTabSelected("today") },
            icon = { Text("📅") },
            label = { Text("Today") }
        )
        BottomNavigationItem(
            selected = currentScreen == "find",
            onClick = { onTabSelected("find") },
            icon = { Text("🔍") },
            label = { Text("Find") }
        )
        BottomNavigationItem(
            selected = currentScreen == "about",
            onClick = { onTabSelected("about") },
            icon = { Text("ℹ️") },
            label = { Text("About") }
        )
    }
}