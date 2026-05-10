package com.example.northstar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.northstar.data.repository.AuthRepository
import com.example.northstar.ui.navigation.BottomNavBar
import com.example.northstar.ui.theme.NorthStarTheme
import com.example.northstar.ui.theme.Surface as AppSurface
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NorthStarTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val startDestination = if (authRepository.isLoggedIn()) {
                    Screen.Dashboard.route
                } else {
                    Screen.Login.route
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent,
                    bottomBar = {
                        val showNavBar = currentRoute in listOf(
                            Screen.Dashboard.route,
                            Screen.Analytics.route,
                            Screen.TransactionHistory.route,
                            Screen.Profile.route,
                            Screen.Goals.route
                        )
                        if (showNavBar) {
                            BottomNavBar(navController = navController)
                        }
                    }
                ) { _ ->
                    // Screens handle their own insets individually
                    Box(modifier = Modifier.fillMaxSize()) {
                        NavGraph(
                            navController = navController,
                            startDestination = startDestination
                        )
                    }
                }
            }
        }
    }
}