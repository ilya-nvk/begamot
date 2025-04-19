package com.begamot.pethosting.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.begamot.pethosting.ui.auth.AuthViewModel
import com.begamot.pethosting.ui.auth.LoginScreen
import com.begamot.pethosting.ui.auth.RegisterScreen
import com.begamot.pethosting.ui.listings.ListingDetailScreen
import com.begamot.pethosting.ui.messages.ChatScreen
import com.begamot.pethosting.ui.messages.MessagesScreen
import com.begamot.pethosting.ui.payment.PaymentScreen
import com.begamot.pethosting.ui.profile.ProfileScreen

@Composable
fun PetHostingApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsState()

    Scaffold { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = if (isUserLoggedIn) "home" else "login",
            modifier = Modifier.padding(paddingValues)
        ) {
            // Authentication
            composable("login") {
                LoginScreen(navController)
            }
            composable("register") {
                RegisterScreen(navController)
            }

            // Main navigation
            composable("home") {
                HomeScreen(navController)
            }
            composable("profile") {
                ProfileScreen(navController)
            }
//            composable("create-listing") {
//                CreateListingScreen(navController)
//            }
            composable("listing/{listingId}") { backStackEntry ->
                val listingId = backStackEntry.arguments?.getString("listingId") ?: ""
                ListingDetailScreen(navController, listingId)
            }
            composable("messages") {
                MessagesScreen(navController)
            }
            composable("chat/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                ChatScreen(navController, userId)
            }
//            composable("settings") {
//                SettingsScreen(navController)
//            }
            composable("payment/{listingId}") { backStackEntry ->
                val listingId = backStackEntry.arguments?.getString("listingId") ?: ""
                PaymentScreen(navController, listingId)
            }
        }
    }
}
