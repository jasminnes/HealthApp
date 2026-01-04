package com.tu.health.ui.screens.nutrition

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.tu.health.data.remote.dto.SearchedFoodDTO
import com.tu.health.viewmodels.nutrition.MacrosViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.roundToInt

@SuppressLint("UnrememberedGetBackStackEntry")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodSearchScreen(
    navController: NavController
) {
    // IMPORTANT: share the SAME viewmodel instance as MacrosScreen
    val parentEntry = remember(navController) { navController.getBackStackEntry("macros") }
    val viewModel: MacrosViewModel = hiltViewModel(parentEntry)

    val context = LocalContext.current

    val searched by viewModel.searched.collectAsState()
    val results by viewModel.results.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food search") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = searched,
                onValueChange = { viewModel.onSearchedChange(it) },
                label = { Text("Search food") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.tertiaryContainer,
                    focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.tertiaryContainer,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Button(
                onClick = { viewModel.search() },
                enabled = searched.isNotBlank() && !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLoading) "Searching..." else "Search")
            }

            if (results.isEmpty() && searched.isNotBlank() && !isLoading) {
                Text(
                    "No results.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(results) { item ->
                    SearchResultRow(
                        item = item,
                        onClick = {
                            viewModel.onSelectedNameChange(item.name)
                            viewModel.onQuantityChange(100f)

                            viewModel.onCaloriesChange(item.calories)
                            viewModel.onProteinChange(item.protein)
                            viewModel.onCarbsChange(item.carbs)
                            viewModel.onFatChange(item.fat)

                            viewModel.createFood()

                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    item: SearchedFoodDTO,
    onClick: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${item.calories.roundToInt()} kcal " +
                        "• P ${item.protein.pretty1()}g " +
                        "• C ${item.carbs.pretty1()}g " +
                        "• F ${item.fat.pretty1()}g (per 100g)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
