package com.example.northstar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.northstar.ui.theme.ThemePreferenceManager
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.northstar.data.repository.AuthRepository
import com.example.northstar.ui.lock.PinLockManager
import com.example.northstar.ui.lock.PinMode
import com.example.northstar.ui.lock.PinScreen
import com.example.northstar.ui.navigation.BottomNavBar
import com.example.northstar.ui.theme.NorthStarTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var pinLockManager: PinLockManager

    @Inject
    lateinit var themePreferenceManager: ThemePreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by themePreferenceManager.isDarkMode.collectAsState()
            NorthStarTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val isLocked by pinLockManager.isLocked.collectAsState()
                var showPinSetup by remember { mutableStateOf(false) }

                val startDestination = if (authRepository.isLoggedIn()) {
                    Screen.Dashboard.route
                } else {
                    Screen.Login.route
                }

                // Auto-prompt PIN setup on first login
                LaunchedEffect(authRepository.isLoggedIn()) {
                    if (authRepository.isLoggedIn() && !pinLockManager.hasPin()) {
                        showPinSetup = true
                    }
                }

                // PIN setup on first login
                if (showPinSetup) {
                    PinScreen(
                        mode = PinMode.SETUP,
                        onSuccess = { showPinSetup = false },
                        pinLockManager = pinLockManager
                    )
                    return@NorthStarTheme
                }

                // PIN lock on resume
                if (isLocked && authRepository.isLoggedIn()) {
                    PinScreen(
                        mode = PinMode.UNLOCK,
                        onSuccess = { },
                        pinLockManager = pinLockManager
                    )
                    return@NorthStarTheme
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent,
                    contentWindowInsets = WindowInsets(0),
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
                    Box(modifier = Modifier.fillMaxSize()) {
                        NavGraph(
                            navController = navController,
                            startDestination = startDestination,
                            pinLockManager = pinLockManager
                        )
                    }
                }
            }
        }
    }
}