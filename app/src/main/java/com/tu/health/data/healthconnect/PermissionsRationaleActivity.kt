package com.tu.health.data.healthconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text

class PermissionsRationaleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Text(
                "Sync health data to your account to power health insights and progress tracking." +
                        "We don’t sell this data. See our Privacy Policy in the app settings."
            )
        }
    }
}
