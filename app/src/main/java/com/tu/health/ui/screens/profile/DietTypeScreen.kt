package com.tu.health.ui.screens.profile

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.tu.health.data.remote.dto.DietTypeDTO
import com.tu.health.viewmodels.profile.ProfileViewModel
import com.tu.health.viewmodels.profile.ProfileUiEvent
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("UnrememberedGetBackStackEntry")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietTypeScreen(
    navController: NavController,
) {
    val parentEntry = remember(navController) { navController.getBackStackEntry("profile") }
    val profileViewModel: ProfileViewModel = hiltViewModel(parentEntry)

    val uiState by profileViewModel.uiState.collectAsState()
    val selectedDietType by profileViewModel.selectedDietType.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        profileViewModel.events.collectLatest { event ->
            when (event) {
                is ProfileUiEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(uiState.allDietTypes.size) {
        if (uiState.allDietTypes.isEmpty()) {
            profileViewModel.loadDiets()
        }
    }

    var pendingId by remember(uiState.selectedDietTypeId) {
        mutableStateOf(uiState.selectedDietTypeId)
    }

    val isLoading = uiState.isLoading
    val allDietTypes = uiState.allDietTypes
    val selectedDietTypeId = uiState.selectedDietTypeId

    val saveEnabled = pendingId != null && pendingId != selectedDietTypeId

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diet type") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        enabled = saveEnabled && !isLoading,
                        onClick = {
                            pendingId?.let { profileViewModel.onDietTypeSelected(it) }

                            profileViewModel.updateUserDietType { success ->
                                if (success) {
                                    navController.popBackStack()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        ),
                    ) { Text("Save") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .padding(bottom = 20.dp)
        ) {
            when {
                isLoading && allDietTypes.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                allDietTypes.isEmpty() -> {
                    Text(
                        text = "No diet types found.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CurrentDietCard(selectedDietType)

                        RecommendedDietTypes(uiState.recommendedDietTypes)

                        Text(
                            text = "Change your diet type",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        allDietTypes.forEach { diet ->
                            DietTypeItem(
                                diet = diet,
                                selected = (diet.id == pendingId),
                                onClick = { pendingId = diet.id }
                            )
                        }
                    }
                }
            }

            if (isLoading && allDietTypes.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 6.dp)
                ) {
                    CircularProgressIndicator(
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrentDietCard(selectedDietType: DietTypeDTO?) {
    ElevatedCard(
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                "Current",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = selectedDietType?.name ?: "Not set",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (selectedDietType != null) {
                Text(
                    text = selectedDietType.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RecommendedDietTypes(recommendedDietTypes: List<DietTypeDTO>) {
    ElevatedCard(
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Recommended Diets",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (recommendedDietTypes.isEmpty()) {
                Text(
                    text = "Not set",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    recommendedDietTypes.forEach { diet ->
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    text = diet.name,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DietTypeItem(
    diet: DietTypeDTO,
    selected: Boolean,
    onClick: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.tertiary,
                    unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledSelectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledUnselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = diet.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = diet.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Protein ${diet.proteinRatio}% • Carbs ${diet.carbsRatio}% • Fat ${diet.fatRatio}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
