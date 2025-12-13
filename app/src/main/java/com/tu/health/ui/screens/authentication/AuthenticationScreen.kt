package com.tu.health.ui.screens.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tu.health.R
import com.tu.health.ui.navigation.Screen
import com.tu.health.ui.theme.HealthAppTheme

@Composable
fun AuthenticationScreen(
    navController: NavController
) {
    val brandName = stringResource(id = R.string.app_name)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Welcome to",
                        fontSize = MaterialTheme.typography.displaySmall.fontSize,
                        fontWeight = MaterialTheme.typography.displaySmall.fontWeight,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = brandName,
                        fontSize = MaterialTheme.typography.displayMedium.fontSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                MaterialTheme.colorScheme.secondary

                Surface(
                    modifier = Modifier
                        .size(120.dp),
                    shape = CircleShape,
                    color = Color.White,
                    tonalElevation = 0.dp,
                    shadowElevation = 6.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.health),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(70.dp),
                        )
                    }
                }
            }

            // Buttons card
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = MaterialTheme.shapes.large
                    )
                    .padding(24.dp)
            ) {
                SignUpButton(navController)

                // Divider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = DividerDefaults.Thickness,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "or",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.outline
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = DividerDefaults.Thickness,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                LogInButton(navController)
            }

        }
    }
}

@Composable
fun SignUpButton(navController: NavController) {
    Button(
        onClick = { navController.navigate(Screen.SignUp.route) },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            text = "Sign Up",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LogInButton(navController: NavController) {
    Button(
        onClick = { navController.navigate(Screen.LogIn.route) },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        )
    ) {
        Text(
            text = "Log In",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AuthenticationScreenLightPreview() {
    HealthAppTheme(darkTheme = false) {
        AuthenticationScreen(
            navController = androidx.navigation.testing.TestNavHostController(
                LocalContext.current
            )
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AuthenticationScreenDarkPreview() {
    HealthAppTheme(darkTheme = true) {
        AuthenticationScreen(
            navController = androidx.navigation.testing.TestNavHostController(
                LocalContext.current
            )
        )
    }
}
