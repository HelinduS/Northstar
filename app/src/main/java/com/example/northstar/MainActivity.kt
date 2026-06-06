package com.example.northstar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.northstar.data.repository.AuthRepository
import com.example.northstar.data.repository.SyncManager
import com.example.northstar.ui.lock.PinLockManager
import com.example.northstar.ui.lock.PinMode
import com.example.northstar.ui.lock.PinScreen
import com.example.northstar.ui.navigation.BottomNavBar
import com.example.northstar.ui.notifications.NotificationHelper
import com.example.northstar.ui.notifications.RequestNotificationPermission
import com.example.northstar.ui.theme.NorthStarTheme
import com.example.northstar.ui.theme.ThemePreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.compose.foundation.layout.padding


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var pinLockManager: PinLockManager
    @Inject lateinit var themePreferenceManager: ThemePreferenceManager
    @Inject lateinit var syncManager: SyncManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Sync any Room data that never made it to Firestore (e.g. written offline, app killed)
        if (authRepository.isLoggedIn()) {
            lifecycleScope.launch {
                syncManager.syncRoomToFirestore()
            }
        }

        // Create notification channels once on app start
        NotificationHelper.createNotificationChannels(this)

        // FR18: Start the 24h repeating alarm that checks for 3-day gap
        NotificationHelper.scheduleExpenseReminder(this)

        setContent {
            val isDarkMode by themePreferenceManager.isDarkMode.collectAsState()

            NorthStarTheme(darkTheme = isDarkMode) {

                // Request POST_NOTIFICATIONS permission on Android 13+
                RequestNotificationPermission()

                val navController     = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute      = navBackStackEntry?.destination?.route
                val isLocked          by pinLockManager.isLocked.collectAsState()
                var showPinSetup      by remember { mutableStateOf(false) }

                val startDestination = if (authRepository.isLoggedIn()) {
                    Screen.Dashboard.route
                } else {
                    Screen.Login.route
                }

                LaunchedEffect(authRepository.isLoggedIn()) {
                    if (authRepository.isLoggedIn() && !pinLockManager.hasPin()) {
                        showPinSetup = true
                    }
                }

                if (showPinSetup) {
                    PinScreen(
                        mode           = PinMode.SETUP,
                        onSuccess      = { showPinSetup = false },
                        pinLockManager = pinLockManager
                    )
                    return@NorthStarTheme
                }

                if (isLocked && authRepository.isLoggedIn()) {
                    PinScreen(
                        mode           = PinMode.UNLOCK,
                        onSuccess      = { },
                        pinLockManager = pinLockManager
                    )
                    return@NorthStarTheme
                }

                val showNavBar = currentRoute in listOf(
                    Screen.Dashboard.route,
                    Screen.Analytics.route,
                    Screen.TransactionHistory.route,
                    Screen.Profile.route,
                    Screen.Goals.route,
                    Screen.Budgets.route
                )

                Scaffold(
                    modifier            = Modifier.fillMaxSize(),
                    containerColor      = Color.Transparent,
                    contentWindowInsets = WindowInsets(0),
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(top = innerPadding.calculateTopPadding())
                            .fillMaxSize()
                    ) {
                        NavGraph(
                            navController    = navController,
                            startDestination = startDestination,
                            pinLockManager   = pinLockManager
                        )
                        if (showNavBar) {
                            BottomNavBar(
                                navController = navController,
                                modifier      = Modifier.align(Alignment.BottomCenter)
                            )
                        }
                    }
                }
            }
        }
    }
}