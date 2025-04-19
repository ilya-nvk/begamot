package com.begamot.pethosting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.begamot.pethosting.ui.PetHostingApp
import com.begamot.pethosting.ui.theme.PetHostingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetHostingTheme {
                PetHostingApp()
            }
        }
    }
}
