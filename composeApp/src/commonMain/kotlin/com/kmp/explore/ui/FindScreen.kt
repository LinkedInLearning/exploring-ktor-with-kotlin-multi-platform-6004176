package com.kmp.explore.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kmp.explore.models.ApodResponse
import com.kmp.explore.repository.ApodRepository
import org.koin.compose.koinInject

@Composable
fun FindScreen(
    onApodClick: (ApodResponse) -> Unit,
    repository: ApodRepository = koinInject()
) {
    var selectedDate by remember { mutableStateOf("") }
    var foundApod by remember { mutableStateOf<ApodResponse?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun searchByDate() {
        if (selectedDate.isBlank()) {
            errorMessage = "Please enter a date"
            return
        }

        // Basic date validation
        if (!selectedDate.matches(Regex("""^\d{4}-\d{2}-\d{2}$"""))) {
            errorMessage = "Please enter date in YYYY-MM-DD format"
            return
        }

        // Clear previous results and start search
        foundApod = null
        errorMessage = null
        isLoading = true
    }

    // Perform the actual search when isLoading becomes true
    LaunchedEffect(isLoading) {
        if (isLoading && selectedDate.isNotBlank()) {
            try {
                foundApod = repository.getApodByDate(selectedDate).getOrThrow()
            } catch (e: Exception) {
                errorMessage = "Failed to find picture for $selectedDate: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Find Picture by Date") },
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "🔍 Find a Picture",
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.primary
            )

            Text(
                "Enter a date to find the Astronomy Picture of the Day for that specific date.",
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date input field
            OutlinedTextField(
                value = selectedDate,
                onValueChange = {
                    selectedDate = it
                    // Clear previous results when user types
                    foundApod = null
                    errorMessage = null
                },
                label = { Text("Date (YYYY-MM-DD)") },
                placeholder = { Text("2024-01-15") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = errorMessage != null
            )

            Text(
                "💡 Tip: NASA APOD started on June 16, 1995",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )

            // Search button
            Button(
                onClick = { searchByDate() },
                enabled = selectedDate.isNotBlank() && !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colors.onPrimary
                        )
                        Text("Searching...")
                    }
                } else {
                    Text("🔍 Find Picture")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Results area
            when {
                errorMessage != null -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = MaterialTheme.colors.error.copy(alpha = 0.1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "❌ Error",
                                style = MaterialTheme.typography.h6,
                                color = MaterialTheme.colors.error
                            )
                            Text(
                                errorMessage!!,
                                style = MaterialTheme.typography.body2,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                foundApod != null -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "📅 ${foundApod!!.date}",
                                style = MaterialTheme.typography.caption,
                                color = MaterialTheme.colors.primary
                            )
                            Text(
                                foundApod!!.title,
                                style = MaterialTheme.typography.h6
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                foundApod!!.explanation.take(150) + "...",
                                style = MaterialTheme.typography.body2,
                                maxLines = 3
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    // TODO: This button doesn't work yet - Chapter 6 challenge!
                                    // onApodClick(foundApod!!)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("🌌 View Full Picture")
                            }
                        }
                    }
                }

                else -> {
                    // Empty state - show some helpful information
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "🌌",
                            style = MaterialTheme.typography.h2
                        )
                        Text(
                            "Enter a date above to discover what amazing picture NASA featured that day!",
                            style = MaterialTheme.typography.body2,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}