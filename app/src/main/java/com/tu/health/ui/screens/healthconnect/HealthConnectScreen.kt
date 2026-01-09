package com.tu.health.ui.screens.healthconnect

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.HealthConnectClient
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.tu.health.data.healthconnect.HealthReadPermissions
import com.tu.health.viewmodels.healthconnect.HcUiState
import com.tu.health.viewmodels.healthconnect.HealthConnectViewModel

@Composable
fun HealthConnectScreen(
    navController: NavController,
    vm: HealthConnectViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by vm.state.collectAsState()

    LaunchedEffect(state) {
        if (state is HcUiState.Ready) {
            navController.navigate("profile") {
                popUpTo("health-connect-setup") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) {
        vm.refresh()
    }

    LaunchedEffect(Unit) { vm.refresh() }

    when (val s = state) {
        is HcUiState.Checking -> {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is HcUiState.NotSupported -> {
            InfoCard(
                title = "Health Connect not available",
                body = "This device can’t use Health Connect."
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    navController.navigate("profile") {
                        popUpTo("health-connect-setup") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) { Text("Continue") }
        }

        is HcUiState.NeedsProviderUpdate -> {
            InfoCard(
                title = "Update Health Connect",
                body = "Health Connect needs to be installed or updated from Google Play."
            )
            Spacer(Modifier.height(12.dp))
            Row(Modifier.padding(16.dp)) {
                Button(onClick = { openHealthConnectOnPlay(context) }) {
                    Text("Open in Play Store")
                }
                Spacer(Modifier.width(12.dp))
                OutlinedButton(
                    onClick = {
                        navController.navigate("profile") {
                            popUpTo("health-connect-setup") { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Skip for now")
                }
            }
        }

        is HcUiState.NeedsPermission -> {
            Column(Modifier.padding(16.dp)) {
                Text("Connect Health Connect", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(
                    "We request access to Steps, Heart Rate/HRV, Sleep, and Exercise sessions " +
                            "to power your health insights. You can revoke access anytime in Settings."
                )
                Spacer(Modifier.height(16.dp))

                Button(onClick = { permissionLauncher.launch(HealthReadPermissions) }) {
                    Text("Grant permissions")
                }

                Spacer(Modifier.height(12.dp))

                OutlinedButton(onClick = { openManageHealthPermissions(context) }) {
                    Text("Manage access")
                }

                Spacer(Modifier.height(12.dp))

                TextButton(
                    onClick = {
                        navController.navigate("profile") {
                            popUpTo("health-connect-setup") { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Skip for now")
                }
            }
        }

        is HcUiState.Ready -> {
            val snap = s.snapshot
            Column(Modifier.padding(16.dp)) {
                Text("Connected!", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))

                Text("Today’s steps: ${snap.todaySteps}")

                Spacer(Modifier.height(8.dp))
                Text(
                    "Heart rate (today): " +
                            "min ${snap.heartRateToday.minBpm ?: "-"} / " +
                            "avg ${snap.heartRateToday.avgBpm ?: "-"} / " +
                            "max ${snap.heartRateToday.maxBpm ?: "-"} / " +
                            "latest ${snap.heartRateToday.latestBpm ?: "-"}"
                )

                Spacer(Modifier.height(8.dp))
                Text(
                    "HRV RMSSD (today): " +
                            "avg ${snap.hrvToday.avgRmssdMs?.let { "%.1f".format(it) } ?: "-"} ms / " +
                            "latest ${snap.hrvToday.latestRmssdMs?.let { "%.1f".format(it) } ?: "-"} ms"
                )

                Spacer(Modifier.height(8.dp))
                Text("Sleep duration: ${snap.latestSleep.durationMinutes ?: "-"} min")

                Spacer(Modifier.height(8.dp))
                Text(
                    "Exercise today: ${snap.exerciseToday.totalDurationMinutes} min, " +
                            "active kcal ${snap.exerciseToday.totalActiveCaloriesKcal?.let { "%.0f".format(it) } ?: "-"}"
                )

                Spacer(Modifier.height(16.dp))
                CircularProgressIndicator()
            }
        }

        is HcUiState.Error -> {
            InfoCard(
                title = "Error",
                body = s.message
            )
            Spacer(Modifier.height(12.dp))
            Row(Modifier.padding(16.dp)) {
                Button(onClick = { vm.refresh() }) { Text("Retry") }
                Spacer(Modifier.width(12.dp))
                OutlinedButton(
                    onClick = {
                        navController.navigate("profile") {
                            popUpTo("health-connect-setup") { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) { Text("Skip") }
            }
        }
    }
}

@Composable
private fun InfoCard(title: String, body: String) {
    Card(Modifier.padding(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(body)
        }
    }
}

private fun openManageHealthPermissions(context: android.content.Context) {
    try {
        if (Build.VERSION.SDK_INT >= 34) {
            val intent = Intent(android.health.connect.HealthConnectManager.ACTION_MANAGE_HEALTH_PERMISSIONS).apply {
                putExtra(Intent.EXTRA_PACKAGE_NAME, context.packageName)
            }
            context.startActivity(intent)
        } else {
            val intent = Intent(HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS)
            context.startActivity(intent)
        }
    } catch (_: Throwable) { }
}

private fun openHealthConnectOnPlay(context: android.content.Context) {
    val providerPackage = "com.google.android.apps.healthdata"
    val uriString = "market://details?id=$providerPackage&url=healthconnect%3A%2F%2Fonboarding"
    context.startActivity(
        Intent(Intent.ACTION_VIEW).apply {
            setPackage("com.android.vending")
            data = Uri.parse(uriString)
            putExtra("overlay", true)
            putExtra("callerId", context.packageName)
        }
    )
}
