package com.simats.graftpredict

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import com.simats.graftpredict.data.local.SessionManager
import com.simats.graftpredict.ui.components.BottomNavBar
import com.simats.graftpredict.ui.theme.GraftpredictTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GraftpredictTheme {
                val navController = androidx.navigation.compose.rememberNavController()
                val session = remember { SessionManager(this@MainActivity) }
                
                // Determine start destination based on login status and user role
                val startDestination = if (session.isLoggedIn()) {
                    val userRole = session.getUserRole()
                    if (userRole?.lowercase() == "admin") {
                        com.simats.graftpredict.ui.navigation.Destinations.AdminHome
                    } else {
                        com.simats.graftpredict.ui.navigation.Destinations.Home
                    }
                } else {
                    com.simats.graftpredict.ui.navigation.Destinations.Landing
                }
                
                // Get current route for navigation bar highlighting
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: ""
                
                // Routes that should show the bottom nav bar
                val showBottomNav = currentRoute in listOf(
                    com.simats.graftpredict.ui.navigation.Destinations.Home,
                    com.simats.graftpredict.ui.navigation.Destinations.ManageReport,
                    com.simats.graftpredict.ui.navigation.Destinations.Profile
                )
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomNav) {
                            BottomNavBar(
                                currentRoute = currentRoute,
                                onHomeClick = {
                                    navController.navigate(com.simats.graftpredict.ui.navigation.Destinations.Home) {
                                        popUpTo(com.simats.graftpredict.ui.navigation.Destinations.Home) { inclusive = false }
                                    }
                                },
                                onReportClick = {
                                    navController.navigate(com.simats.graftpredict.ui.navigation.Destinations.ManageReport) {
                                        popUpTo(com.simats.graftpredict.ui.navigation.Destinations.Home) { inclusive = false }
                                    }
                                },
                                onProfileClick = {
                                    navController.navigate(com.simats.graftpredict.ui.navigation.Destinations.Profile) {
                                        popUpTo(com.simats.graftpredict.ui.navigation.Destinations.Home) { inclusive = false }
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        com.simats.graftpredict.ui.navigation.AppNavGraph(
                            navController = navController,
                            startDestination = startDestination
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GraftpredictTheme {
        Greeting("Android")
    }
}
