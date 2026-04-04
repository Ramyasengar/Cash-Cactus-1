package com.example.cashcactus.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.cashcactus.data.local.AppDatabase   // ✅ FIXED
import com.example.cashcactus.data.model.*
import com.example.cashcactus.utils.PasswordSecurity
import com.example.cashcactus.utils.UserSessionManager
import com.example.cashcactus.utils.VaultSessionManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.example.cashcactus.data.model.DashboardData
import com.example.cashcactus.utils.BudgetPredictor
import com.example.cashcactus.data.remote.RetrofitInstance
import com.example.cashcactus.data.remote.PredictionRequest
import com.example.cashcactus.data.remote.FirebaseRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // ✅ FIXED DATABASE
    private val dao = AppDatabase.getDatabase(application).appDao()

    // ================= USER =================
    var currentUserId: Int = 0
    var currentUserName: String = ""
    var currentUserEmail: String = ""
    var currentUserPassword: String = ""

    // ================= DASHBOARD =================
    var monthlySalary: Double = 0.0
    var dashboardAge: Int = 0

    // ================= EXPENSES =================
    var food: Double = 0.0
    var rent: Double = 0.0
    var medical: Double = 0.0
    var emi: Double = 0.0
    var additional: Double = 0.0
    var education: Double = 0.0
    var prediction by mutableStateOf(0.0)
        private set

    var insight by mutableStateOf("")
        private set
    var alerts by mutableStateOf(listOf<String>())
        private set
    var savingsPercent by mutableStateOf(0)
        private set

    var trend by mutableStateOf("")
        private set
    // ================= LOGIN =================
    fun loginUser(email: String, password: String, onResult: (Boolean) -> Unit) {

        val auth = FirebaseAuth.getInstance()

        auth.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    viewModelScope.launch {

                        val user = dao.getUserByEmail(email.trim().lowercase())

                        if (user != null) {
                            applyCurrentUser(user)
                            UserSessionManager.saveSession(getApplication(), user)

                            loadDashboardData()
                            loadExpenses()
                        }

                        onResult(true)
                    }

                } else {
                    onResult(false)
                }
            }
    }

    // ================= REGISTER =================
    fun register(
        name: String,
        email: String,
        password: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {

            val existing = dao.getUserByEmail(email.trim().lowercase())

            if (existing != null) {
                onResult(false)
                return@launch
            }

            val user = User(
                name = name,
                email = email.trim().lowercase(),
                password = PasswordSecurity.hash(password.trim())
            )

            dao.insertUser(user)

            val savedUser = dao.getUserByEmail(email.trim().lowercase())

            savedUser?.let {
                applyCurrentUser(it)
                UserSessionManager.saveSession(getApplication(), it)
            }

            onResult(savedUser != null)
        }
    }

    // ================= DASHBOARD =================
    fun saveDashboardData(age: Int, salary: Double) {
        dashboardAge = age
        monthlySalary = salary

        viewModelScope.launch {
            dao.insertDashboard(
                DashboardData(
                    userId = currentUserId,
                    category = "General",
                    age = age,
                    salary = salary.toFloat(),
                    savingType = "Monthly",
                    savingValue = 0f
                )
            )
        }
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            val data = dao.getDashboard(currentUserId)
            data?.let {
                dashboardAge = it.age
                monthlySalary = it.salary.toDouble()
            }
        }
    }

    // ================= LOAD EXPENSES =================
    fun loadExpenses() {
        viewModelScope.launch {

            val list = dao.getExpenses(currentUserId)

            food = list.find { it.title == "Food" }?.amount ?: 0.0
            rent = list.find { it.title == "Rent" }?.amount ?: 0.0
            medical = list.find { it.title == "Medical" }?.amount ?: 0.0
            emi = list.find { it.title == "EMI" }?.amount ?: 0.0
            additional = list.find { it.title == "Additional" }?.amount ?: 0.0
            education = list.find { it.title == "Education" }?.amount ?: 0.0

            // 🔥 ADD THIS BLOCK (MOST IMPORTANT)

            val transactions = listOf(
                Transaction(amount = food, type = "expense", category = "Food", date = System.currentTimeMillis()),
                Transaction(amount = rent, type = "expense", category = "Rent", date = System.currentTimeMillis()),
                Transaction(amount = medical, type = "expense", category = "Medical", date = System.currentTimeMillis()),
                Transaction(amount = emi, type = "expense", category = "EMI", date = System.currentTimeMillis()),
                Transaction(amount = additional, type = "expense", category = "Other", date = System.currentTimeMillis()),
                Transaction(amount = education, type = "expense", category = "Education", date = System.currentTimeMillis())
            )

            updatePrediction(transactions)        // local AI
            fetchRealPrediction(transactions)     // 🔥 backend AI
        }
    }

    // ================= SMS AUTO UPDATE =================
    fun updateExpensesFromSms(transactions: List<Transaction>) {

        transactions
            .filter { it.type.equals("expense", ignoreCase = true) } // ✅ FIXED
            .forEach { transaction ->

                val msg = transaction.category.lowercase()   // ✅ FIXED
                val amt = transaction.amount                // ✅ FIXED

                when {
                    msg.contains("food") -> food += amt
                    msg.contains("rent") -> rent += amt
                    msg.contains("medical") -> medical += amt
                    msg.contains("emi") -> emi += amt
                    msg.contains("education") -> education += amt
                    else -> additional += amt
                }
            }
    }

    private fun applyCurrentUser(user: User) {
        currentUserId = user.id
        currentUserName = user.name
        currentUserEmail = user.email
        currentUserPassword = user.password
    }
    fun restoreSession(context: Context) {
        val userId = UserSessionManager.getUserId(context)

        if (userId == 0) return

        viewModelScope.launch {
            val user = dao.getUserById(userId) ?: return@launch

            currentUserId = user.id
            currentUserName = user.name
            currentUserEmail = user.email
            currentUserPassword = user.password

            loadDashboardData()
            loadExpenses()
        }
    }
    fun updateUser(
        name: String,
        email: String,
        password: String,
        oldEmail: String
    ) {
        viewModelScope.launch {

            val normalizedEmail = email.trim().lowercase()

            val safePassword = if (password.isBlank()) {
                currentUserPassword
            } else {
                PasswordSecurity.hash(password.trim())
            }

            dao.updateUser(name, normalizedEmail, safePassword, oldEmail)

            currentUserName = name
            currentUserEmail = normalizedEmail
            currentUserPassword = safePassword

            UserSessionManager.saveSession(
                getApplication(),
                User(
                    id = currentUserId,
                    name = name,
                    email = normalizedEmail,
                    password = safePassword
                )
            )
        }
    }
    fun saveMonthlyExpenses(
        food: Double,
        rent: Double,
        medical: Double,
        emi: Double,
        education: Double,
        additional: Double
    ) {
        viewModelScope.launch {

            val total = food + rent + medical + emi + education + additional

            val dashboard = DashboardData(
                userId = currentUserId,

                // KEEP OLD DATA SAFE
                category = "General",
                age = dashboardAge,
                salary = monthlySalary.toFloat(),
                savingType = "Monthly",
                savingValue = 0f,

                // NEW EXPENSE DATA
                totalBalance = total,
                food = food,
                rent = rent,
                medical = medical,
                emi = emi,
                education = education,
                additional = additional
            )

            dao.insertDashboard(dashboard)
        }
        FirebaseRepository.saveUserData(
            currentUserId.toString(),
            mapOf(
                "food" to food,
                "rent" to rent,
                "medical" to medical,
                "emi" to emi,
                "education" to education,
                "additional" to additional,
                "salary" to monthlySalary
            )
        )
    }

    fun logout(navController: NavHostController) {
        com.google.firebase.auth.FirebaseAuth.getInstance().signOut()

        navController.navigate("login") {
            popUpTo("home") { inclusive = true }
        }
    }
    fun addTransaction(
        amount: Double,
        type: String,
        category: String,
        message: String
    ) {
        viewModelScope.launch {
            val transaction = Transaction(
                amount = amount,
                type = type,
                category = category,
                date = System.currentTimeMillis()
            )
            dao.insertTransaction(transaction)
        }
    }
    fun getAIPredictedExpenses(): List<Pair<String, Float>> {
        return listOf(
            "Food" to 2000f,
            "Rent" to 8000f,
            "Medical" to 1000f,
            "EMI" to 3000f,
            "Education" to 1500f,
            "Other" to 1000f
        )
    }
    fun updatePrediction(transactions: List<Transaction>) {
        prediction = BudgetPredictor.predictMonthlySpending(transactions)
        insight = BudgetPredictor.generateInsight(transactions)
        alerts = BudgetPredictor.getSmartAlerts(transactions)

        savingsPercent = BudgetPredictor.calculateSavingsPercentage(transactions)
        trend = BudgetPredictor.weeklyTrend(transactions)
    }
    fun fetchRealPrediction(transactions: List<Transaction>) {

        viewModelScope.launch {

            try {

                val expenses = transactions
                    .filter { it.type == "expense" }
                    .map { it.amount }

                val response = RetrofitInstance.api.getPrediction(
                    PredictionRequest(expenses)
                )

                prediction = response.prediction
                trend = response.trend

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}