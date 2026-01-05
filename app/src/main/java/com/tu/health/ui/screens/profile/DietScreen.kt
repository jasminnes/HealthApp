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

@SuppressLint("UnrememberedGetBackStackEntry")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietTypeScreen(
    navController: NavController,
) {
    val parentEntry = remember(navController) { navController.getBackStackEntry("profile") }
    val profileViewModel: ProfileViewModel = hiltViewModel(parentEntry)

    val allDietTypes by profileViewModel.allDietTypes.collectAsState()
    val selectedDietType by profileViewModel.selectedDietType.collectAsState()
    val selectedDietTypeId by profileViewModel.selectedDietTypeId.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (allDietTypes.isEmpty()) {
            profileViewModel.loadDiets()
        }
    }

    var pendingId by remember(selectedDietTypeId) { mutableStateOf(selectedDietTypeId) }

    val saveEnabled = pendingId != null && pendingId != selectedDietTypeId

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diet type") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        enabled = saveEnabled && !isLoading,
                        onClick = {
                            pendingId?.let { profileViewModel.onDietTypeSelected(it) }
                            profileViewModel.updateUserDietType { success, error ->
                                if (success) {
                                    navController.popBackStack()
                                } else {
                                    Toast.makeText(
                                        context,
                                        error ?: "Failed to update diet type",
                                        Toast.LENGTH_SHORT
                                    ).show()
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
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp).padding(bottom = 20.dp)
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

                        Text(
                            text = "Choose a diet type",
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
    ElevatedCard(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
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
                colors = RadioButtonColors(
                    selectedColor = MaterialTheme.colorScheme.tertiary,
                    unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledSelectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledUnselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = diet.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
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
