package com.kmp.explore.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kmp.explore.models.ApodResponse
import com.kmp.explore.repository.ApodRepository
import org.koin.compose.koinInject

@Composable
fun TodayScreen(
    onApodClick: (ApodResponse) -> Unit,
    repository: ApodRepository = koinInject()
) {
    var todayApod by remember { mutableStateOf<ApodResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load today's APOD when screen is first displayed
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            errorMessage = null
            todayApod = repository.getTodayApod().getOrThrow()
        } catch (e: Exception) {
            errorMessage = "Failed to load today's picture: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Today's Picture") },
                actions = {
                    IconButton(onClick = {
                        // Refresh today's picture
                        isLoading = true
                        errorMessage = null
                    }) {
                        Text("🔄")
                    }
                },
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Loading today's picture...")
                    }
                }

                errorMessage != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            "😕 Oops!",
                            style = MaterialTheme.typography.h4
                        )
                        Text(
                            errorMessage!!,
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.error
                        )
                        Button(
                            onClick = {
                                isLoading = true
                                errorMessage = null
                            }
                        ) {
                            Text("Try Again")
                        }
                    }
                }

                todayApod != null -> {
                    PictureDetailScreen(
                        apod = todayApod!!,
                        onBackClick = { /* No back action for tab */ },
                        showBackButton = false
                    )
                }

                else -> {
                    Text("No picture available for today")
                }
            }
        }
    }
}