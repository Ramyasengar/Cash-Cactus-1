package com.example.cashcactus


import com.example.cashcactus.ui.screens.*
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔐 Security
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        createNotificationChannel(this)

        setContent {

            var isDark by remember { mutableStateOf(false) }

            CashCactusTheme(darkTheme = isDark) {

                CashCactusApp(
                    onThemeChange = { isDark = it }
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        VaultSessionManager.lock(this)
    }
}

// ---------------- APP ROOT ----------------

@Composable
fun CashCactusApp(
    onThemeChange: (Boolean) -> Unit
) {
    val viewModel: MainViewModel = viewModel()
    val context = LocalContext.current

    val isLoggedIn = UserSessionManager.isLoggedIn(context)

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            viewModel.restoreSession(context)
        }
    }

    AppContent(viewModel, onThemeChange)
}

// ---------------- NAVIGATION ----------------

@Composable
fun AppContent(
    viewModel: MainViewModel,
    onThemeChange: (Boolean) -> Unit
) {

    val navController = rememberNavController()
    val context = LocalContext.current

    val startDestination = when {
        !isPrivacyAccepted(context) -> "privacy_mandatory"
        !UserSessionManager.isLoggedIn(context) -> "login"
        else -> "home"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // 🔐 Auth
        composable("login") {
            LoginScreen(navController, viewModel)
        }

        composable("forgotPassword") {
            ForgotPasswordScreen(navController)
        }

        composable("register") {
            RegisterScreen(navController, viewModel)
        }

        // 🏠 Home
        composable("home") {
            HomeScreen(navController, viewModel, onThemeChange)
        }

        composable("about") {
            AboutScreen(navController)
        }

        composable("help") {
            HelpScreen(navController)
        }

        composable("contact") {
            ContactScreen(navController)
        }

        // 📊 Dashboard
        composable("dashboard") {
            DashboardScreen(navController, viewModel)
        }

        composable(
            route = "expenseCategory/{age}",
            arguments = listOf(navArgument("age") { type = NavType.IntType })
        ) { backStackEntry ->

            val age = backStackEntry.arguments?.getInt("age") ?: 0

            ExpenseCategoryScreen(
                navController = navController,
                age = age,
                viewModel = viewModel
            )
        }

        composable("expenseGraph") {
            ExpenseGraphScreen(navController, viewModel)
        }

        // ⚙️ Profile
        composable("edit") {
            EditProfileScreen(navController, viewModel)
        }

        composable("privacy_mandatory") {
            PrivacyPolicyScreen(navController, isMandatory = true)
        }

        composable("privacy") {
            PrivacyPolicyScreen(navController, isMandatory = false)
        }

        // 💰 Investment
        composable("investment") {
            InvestmentScreen()
        }

        // 🔐 Vault
        composable("vaultEntry") {
            VaultEntryScreen(navController)
        }

        composable("vaultHome") {
            VaultHomeScreen(navController)
        }

        composable(
            route = "vaultPin/{mode}",
            arguments = listOf(navArgument("mode") { defaultValue = "unlock" })
        ) { backStackEntry ->
            VaultPinScreen(
                navController = navController,
                mode = backStackEntry.arguments?.getString("mode") ?: "unlock"
            )
        }

        composable("addVault") {
            AddVaultScreen(navController)
        }

        composable("viewVault") {
            ViewVaultScreen(navController)
        }

        composable("emergency") {
            EmergencyScreen(navController)
        }
    }
}


}

// ---------------- HELPERS ----------------

fun isPrivacyAccepted(context: Context): Boolean {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return prefs.getBoolean("privacy_accepted", false)
}