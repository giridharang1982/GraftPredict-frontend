package com.example.graftpredict.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.graftpredict.ui.screens.*

object Destinations {
    const val Landing = "landing"
    const val Login = "login"
    const val Signup = "signup"
    const val SignupVerify = "signup_verify"
    const val ForgotSend = "forgot_send"
    const val ForgotVerify = "forgot_verify"
    const val ForgotReset = "forgot_reset"
    const val Home = "home"
    const val AdminHome = "admin_home"
    const val GraftSizing = "graft_sizing"
    const val Profile = "profile"
    const val Report = "report"
    const val ManageReport = "manage_report"
    const val PatientReport = "patient_report"
    const val Sent = "sent"
    const val ReportResult = "report_result"
    const val ShareReport = "share_report"
    const val HelpAndSupport = "help_and_support"
    const val PrivacyPolicy = "privacy_policy"
    const val TermsAndConditions = "terms_and_conditions"
}

@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier = Modifier, startDestination: String = Destinations.Landing) {
    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
        composable(Destinations.Landing) { LandingScreen(onNavigateToLogin = { navController.navigate(Destinations.Login) }) }
        composable(Destinations.Login) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Destinations.Home) {
                        // Clear auth flow from back stack so back button doesn't return to login
                        popUpTo(Destinations.Landing) { inclusive = true }
                    }
                },
                onAdminLoginSuccess = {
                    navController.navigate(Destinations.AdminHome) {
                        // Clear auth flow from back stack so back button doesn't return to login
                        popUpTo(Destinations.Landing) { inclusive = true }
                    }
                },
                onForgot = { navController.navigate(Destinations.ForgotSend) },
                onCreateAccount = { navController.navigate(Destinations.Signup) }
            )
        }
        composable(Destinations.Signup) {
            RegistrationScreen(
                onBackPressed = { navController.popBackStack() },
                onLoginClick = { navController.navigate(Destinations.Login) },
                onRequestOtp = { fullName, email, password ->
                    val encodedFullName = java.net.URLEncoder.encode(fullName, Charsets.UTF_8.name())
                    val encodedEmail = java.net.URLEncoder.encode(email, Charsets.UTF_8.name())
                    val encodedPassword = java.net.URLEncoder.encode(password, Charsets.UTF_8.name())
                    navController.navigate("${Destinations.SignupVerify}/$encodedFullName/$encodedEmail/$encodedPassword")
                },
                navController = navController
            )
        }
        composable(Destinations.HelpAndSupport) {
            HelpAndSupportScreen(
                onBackClick = { navController.popBackStack() },
                onTermsClick = { navController.navigate(Destinations.TermsAndConditions) },
                onPrivacyClick = { navController.navigate(Destinations.PrivacyPolicy) }
            )
        }
        composable(Destinations.PrivacyPolicy) {
            PrivacyPolicyScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Destinations.TermsAndConditions) {
            TermsAndConditionsScreen(onBackClick = { navController.popBackStack() }, onAcceptClick = { /* handle accept */ })
        }
        composable(Destinations.ForgotSend) {
            ForgotPasswordScreen(
                onBackPressed = { navController.popBackStack() },
                onSendOtp = { email -> navController.navigate("${Destinations.ForgotVerify}/$email") },
                onLoginPressed = { navController.navigate(Destinations.Login) }
            )
        }
        composable("${Destinations.ForgotVerify}/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerifyOtpScreen(navController = navController, email = email)
        }
        composable("${Destinations.SignupVerify}/{fullName}/{email}/{password}/{dateOfBirth}/{gender}") { backStackEntry ->
            val fullName = backStackEntry.arguments?.getString("fullName")?.let {
                java.net.URLDecoder.decode(it, Charsets.UTF_8.name())
            } ?: ""
            val email = backStackEntry.arguments?.getString("email")?.let {
                java.net.URLDecoder.decode(it, Charsets.UTF_8.name())
            } ?: ""
            val password = backStackEntry.arguments?.getString("password")?.let {
                java.net.URLDecoder.decode(it, Charsets.UTF_8.name())
            } ?: ""
            val dateOfBirth = backStackEntry.arguments?.getString("dateOfBirth")?.let {
                java.net.URLDecoder.decode(it, Charsets.UTF_8.name())
            } ?: ""
            val gender = backStackEntry.arguments?.getString("gender")?.let {
                java.net.URLDecoder.decode(it, Charsets.UTF_8.name())
            } ?: ""
            SignupVerifyOtpScreen(navController = navController, fullName = fullName, email = email, password = password, dateOfBirth = dateOfBirth, gender = gender)
        }
        composable("${Destinations.ForgotReset}/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ResetPasswordScreen(navController = navController, email = email)
        }
        composable(Destinations.Profile) { 
            ProfileScreen(
                onBackPressed = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Destinations.Landing) {
                        // Clear entire back stack so user can't go back after logout
                        popUpTo(Destinations.Home) { inclusive = true }
                    }
                },
                onHomeClick = {
                    navController.navigate(Destinations.Home) {
                        popUpTo(Destinations.Home) { inclusive = false }
                    }
                },
                onReportClick = {
                    navController.navigate(Destinations.ManageReport) {
                        popUpTo(Destinations.Home) { inclusive = false }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Destinations.Landing) {
                        // Clear entire back stack after account deletion so user can't go back
                        popUpTo(Destinations.Home) { inclusive = true }
                    }
                },
                onHelpClick = { navController.navigate(Destinations.HelpAndSupport) }
            ) 
        }
        composable(Destinations.Home) { 
            HomeScreen(
                onProfileClick = { navController.navigate(Destinations.Profile) },
                onGraftSizingClick = { navController.navigate(Destinations.GraftSizing) },
                onManageReportsClick = { navController.navigate(Destinations.Report) },
                onReportClick = { navController.navigate(Destinations.ManageReport) },
                onPatientReportsClick = { navController.navigate(Destinations.PatientReport) }
            ) 
        }
        composable(Destinations.AdminHome) {
            AdminHomeScreen(
                navController = navController,
                onLogout = {
                    navController.navigate(Destinations.Landing) {
                        // Clear entire back stack so user can't go back after logout
                        popUpTo(Destinations.AdminHome) { inclusive = true }
                    }
                }
            )
        }
        composable(Destinations.GraftSizing) { GraftSizeCalculatorScreen(onBackPressed = { navController.popBackStack() }) }
        composable(Destinations.Report) { 
            ReportScreen(
                onBackClick = { navController.popBackStack() },
                onCreateNewClick = { },
                onReportClick = { reportId -> 
                    navController.navigate("${Destinations.ReportResult}/$reportId")
                },
                onProfileClick = { navController.navigate(Destinations.Profile) }
            ) 
        }
        composable(Destinations.PatientReport) {
            PatientsReportsScreen(
                onBackClick = { navController.popBackStack() },
                onHomeClick = { navController.navigate(Destinations.Home) },
                onProfileClick = { navController.navigate(Destinations.Profile) },
                onReportClick = { reportId ->
                    navController.navigate("${Destinations.ReportResult}/$reportId")
                }
            )
        }
        composable(Destinations.ManageReport) { 
            ManageScreen(
                onBackClick = { navController.popBackStack() },
                onAddReportClick = { navController.navigate(Destinations.GraftSizing) },
                onReportClick = { reportId -> 
                    navController.navigate("${Destinations.ReportResult}/$reportId")
                },
                onReceivedReportsClick = { navController.navigate(Destinations.Sent) },
                onHomeClick = { navController.navigate(Destinations.Home) },
                onProfileClick = { navController.navigate(Destinations.Profile) }
            ) 
        }
        composable(Destinations.Sent) { 
            SentScreen(
                onBackClick = { navController.popBackStack() },
                onShareClick = { },
                onHomeClick = { navController.navigate(Destinations.Home) },
                onReportsClick = { navController.navigate(Destinations.ManageReport) },
                onProfileClick = { navController.navigate(Destinations.Profile) }
            )
        }
        composable("${Destinations.ReportResult}/{reportId}") { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId")?.toIntOrNull() ?: 0
            ReportResultScreen(
                reportId = reportId,
                onBackClick = { navController.popBackStack() },
                onShareClick = { navController.navigate("${Destinations.ShareReport}/$reportId") }
            )
        }
        composable("${Destinations.ShareReport}/{reportId}") { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId")?.toIntOrNull() ?: 0
            ShareReportScreen(
                reportId = reportId,
                onBackClick = { navController.popBackStack() },
                onShareClick = { },
                onRevokeAccessClick = { }
            )
        }
    }
}


