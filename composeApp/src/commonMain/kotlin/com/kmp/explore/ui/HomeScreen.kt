package com.kmp.explore.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kmp.explore.models.ApodResponse
import com.kmp.explore.ui.components.ApodListItem
import org.koin.compose.koinInject

@Composable
fun HomeScreen(
    onApodClick: (ApodResponse) -> Unit,
    viewModel: ApodViewModel = koinInject()
) {
    val uiState by viewModel.homeUiState.collectAsState()
    val listState = rememberLazyListState()

    // Load more items when near the end
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null &&
                    lastVisibleIndex >= uiState.apodList.size - 3 &&
                    uiState.hasMorePages &&
                    !uiState.isLoading) {
                    viewModel.loadMoreItems()
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home Gallery") },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
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
                .padding(innerPadding)
        ) {
            when {
                uiState.apodList.isEmpty() && uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.apodList) { apod ->
                            ApodListItem(
                                apod = apod,
                                onItemClick = { onApodClick(apod) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (uiState.isLoading && uiState.apodList.isNotEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}