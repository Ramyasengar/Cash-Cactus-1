package com.example.cashcactus

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cashcactus.ui.screens.AboutScreen
import com.example.cashcactus.ui.screens.AddVaultScreen
import com.example.cashcactus.ui.screens.ContactScreen
import com.example.cashcactus.ui.screens.DashboardScreen
import com.example.cashcactus.ui.screens.EditProfileScreen
import com.example.cashcactus.ui.screens.EmergencyScreen
import com.example.cashcactus.ui.screens.ExpenseCategoryScreen
import com.example.cashcactus.ui.screens.EditTransactionScreen
import com.example.cashcactus.ui.screens.ExpenseGraphScreen
import com.example.cashcactus.ui.screens.ForgotPasswordScreen
import com.example.cashcactus.ui.screens.HelpScreen
import com.example.cashcactus.ui.screens.HomeScreen
import com.example.cashcactus.ui.screens.InvestmentScreen
import com.example.cashcactus.ui.screens.LoginScreen
import com.example.cashcactus.ui.screens.PrivacyPolicyScreen
import com.example.cashcactus.ui.screens.RegisterScreen
import com.example.cashcactus.ui.screens.SettingsScreen
import com.example.cashcactus.ui.screens.VaultEntryScreen
import com.example.cashcactus.ui.screens.VaultHomeScreen
import com.example.cashcactus.ui.screens.VaultPinScreen
import com.example.cashcactus.ui.screens.ViewVaultScreen
import com.example.cashcactus.ui.theme.CashCactusTheme
import com.example.cashcactus.utils.createNotificationChannel
import com.example.cashcactus.utils.LanguageManager
import com.example.cashcactus.utils.UserSessionManager
import com.example.cashcactus.utils.VaultSessionManager
import com.example.cashcactus.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val code = LanguageManager.getSavedLanguage(newBase)
        super.attachBaseContext(LanguageManager.createLocalizedContext(newBase, code))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        createNotificationChannel(this)
        LanguageManager.applySavedLanguage(this)

        setContent {
            var isDark by remember { mutableStateOf(false) }
            var currentLanguage by remember {
                mutableStateOf(LanguageManager.getSavedLanguage(this@MainActivity))
            }

            val activityContext = this@MainActivity
            val localizedContext = remember(currentLanguage) {
                LanguageManager.createLocalizedContext(activityContext, currentLanguage)
            }
            val localizedConfig = remember(currentLanguage) {
                val locale = java.util.Locale(currentLanguage)
                Configuration(activityContext.resources.configuration).apply {
                    setLocale(locale)
                }
            }

            CashCactusTheme(darkTheme = isDark) {
                CompositionLocalProvider(
                    LocalContext provides localizedContext,
                    LocalConfiguration provides localizedConfig
                ) {
                    CashCactusApp(
                        isDarkTheme = isDark,
                        onThemeChange = { isDark = it },
                        onLanguageChange = { lang -> currentLanguage = lang }
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        VaultSessionManager.lock(this)
    }
}

@Composable
fun CashCactusApp(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onLanguageChange: (String) -> Unit
) {
    val viewModel: MainViewModel = viewModel()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser?.email != null) {
            viewModel.restoreOrCreateSessionFromFirebase(firebaseUser.email!!, context)
        } else if (UserSessionManager.isLoggedIn(context)) {
            viewModel.restoreSession(context)
        }
    }

    AppContent(viewModel, isDarkTheme, onThemeChange, onLanguageChange)
}

@Composable
fun AppContent(
    viewModel: MainViewModel,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onLanguageChange: (String) -> Unit
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val startDestination = remember(context) {
        when {
            FirebaseAuth.getInstance().currentUser != null -> "home"
            UserSessionManager.isLoggedIn(context) -> "home"
            else -> "login"
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") { LoginScreen(navController, viewModel) }
        composable("forgotPassword") { ForgotPasswordScreen(navController) }
        composable("register") { RegisterScreen(navController, viewModel) }

        composable("home") { HomeScreen(navController, viewModel) }
        composable("settings") {
            SettingsScreen(
                navController = navController,
                viewModel = viewModel,
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                onLanguageChange = onLanguageChange
            )
        }
        composable("about") { AboutScreen(navController) }
        composable("help") { HelpScreen(navController) }
        composable("contact") { ContactScreen(navController) }

        composable("dashboard") { DashboardScreen(navController, viewModel) }
        composable(
            route = "expenseCategory/{age}",
            arguments = listOf(navArgument("age") { type = NavType.IntType })
        ) { backStackEntry ->
            val age = backStackEntry.arguments?.getInt("age") ?: 0
            ExpenseCategoryScreen(navController = navController, age = age, viewModel = viewModel)
        }
        composable("expenseGraph") { ExpenseGraphScreen(navController, viewModel) }
        composable(
            route = "editTransaction/{transactionId}",
            arguments = listOf(navArgument("transactionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tid = backStackEntry.arguments?.getInt("transactionId") ?: 0
            EditTransactionScreen(navController, tid)
        }

        composable("edit") { EditProfileScreen(navController, viewModel) }
        composable("privacy_mandatory") { PrivacyPolicyScreen(navController, isMandatory = true) }
        composable(
            route = "privacy?fromRegister={fromRegister}",
            arguments = listOf(
                navArgument("fromRegister") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            PrivacyPolicyScreen(
                navController = navController,
                isMandatory = false,
                fromRegister = backStackEntry.arguments?.getBoolean("fromRegister") ?: false
            )
        }

        composable("investment") { InvestmentScreen() }

        composable("vaultEntry") { VaultEntryScreen(navController) }
        composable("vaultHome") { VaultHomeScreen(navController) }
        composable(
            route = "vaultPin/{mode}",
            arguments = listOf(navArgument("mode") { defaultValue = "unlock" })
        ) { backStackEntry ->
            VaultPinScreen(
                navController = navController,
                mode = backStackEntry.arguments?.getString("mode") ?: "unlock"
            )
        }
        composable("addVault") { AddVaultScreen(navController) }
        composable("viewVault") { ViewVaultScreen(navController) }
        composable("emergency") { EmergencyScreen(navController) }
    }
}
