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
import com.example.cashcactus.ui.screens.clearAnalyticsPreferences
import com.example.cashcactus.ui.screens.clearGraphPreference
import com.example.cashcactus.ui.screens.getAnalyticsPeriod
import com.example.cashcactus.utils.PasswordSecurity
import com.example.cashcactus.utils.UserSessionManager
import com.example.cashcactus.utils.VaultSessionManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private var aiPredictedExpenses by mutableStateOf(defaultCategoryTotals())
    // ================= LOGIN =================
    fun loginUser(email: String, password: String, onResult: (Boolean) -> Unit) {

        val auth = FirebaseAuth.getInstance()

        auth.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    viewModelScope.launch {

                        val emailNorm = email.trim().lowercase()
                        var user = dao.getUserByEmail(emailNorm)
                        if (user == null) {
                            dao.insertUser(
                                User(
                                    name = emailNorm.substringBefore("@"),
                                    email = emailNorm,
                                    password = PasswordSecurity.hash(password.trim())
                                )
                            )
                            user = dao.getUserByEmail(emailNorm)
                        }

                        if (user != null) {
                            applyCurrentUser(user)
                            UserSessionManager.saveSession(getApplication(), user)
                            loadDashboardData()
                            loadExpenses()
                            onResult(true)
                        } else {
                            onResult(false)
                        }
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

            if (list.isNotEmpty()) {
                food = list.find { it.title == "Food" }?.amount ?: 0.0
                rent = list.find { it.title == "Rent" }?.amount ?: 0.0
                medical = list.find { it.title == "Medical" }?.amount ?: 0.0
                emi = list.find { it.title == "EMI" }?.amount ?: 0.0
                additional = list.find { it.title == "Additional" }?.amount ?: 0.0
                education = list.find { it.title == "Education" }?.amount ?: 0.0
            } else {
                dao.getDashboard(currentUserId)?.let { dashboard ->
                    food = dashboard.food
                    rent = dashboard.rent
                    medical = dashboard.medical
                    emi = dashboard.emi
                    additional = dashboard.additional
                    education = dashboard.education
                }
            }

            // 🔥 ADD THIS BLOCK (MOST IMPORTANT)

            val transactions = listOf(
                Transaction(amount = food, type = "expense", category = "Food", date = System.currentTimeMillis()),
                Transaction(amount = rent, type = "expense", category = "Rent", date = System.currentTimeMillis()),
                Transaction(amount = medical, type = "expense", category = "Medical", date = System.currentTimeMillis()),
                Transaction(amount = emi, type = "expense", category = "EMI", date = System.currentTimeMillis()),
                Transaction(amount = additional, type = "expense", category = "Other", date = System.currentTimeMillis()),
                Transaction(amount = education, type = "expense", category = "Education", date = System.currentTimeMillis())
            )

            refreshAIPredictedExpenses()
            updatePrediction(transactions)
            fetchRealPrediction(transactions)
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

    fun restoreOrCreateSessionFromFirebase(email: String, context: Context) {
        viewModelScope.launch {
            val normalized = email.trim().lowercase()
            var user = dao.getUserByEmail(normalized)
            if (user == null) {
                dao.insertUser(
                    User(
                        name = normalized.substringBefore("@"),
                        email = normalized,
                        password = PasswordSecurity.hash("_firebase_session_")
                    )
                )
                user = dao.getUserByEmail(normalized)
            }
            if (user != null) {
                applyCurrentUser(user)
                UserSessionManager.saveSession(getApplication(), user)
                loadDashboardData()
                loadExpenses()
            }
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
            syncMonthlyExpenseTransactions(
                food = food,
                rent = rent,
                medical = medical,
                emi = emi,
                education = education,
                additional = additional
            )
            refreshAIPredictedExpenses()
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
        UserSessionManager.clearSession(getApplication())

        navController.navigate("login") {
            popUpTo(0) { inclusive = true }
        }
    }

    /**
     * Deletes the Firebase Auth user, Firestore profile doc, local Room rows, and app prefs for this account.
     * On failure (e.g. requires recent login), [onResult] is called with false and an error message.
     */
    fun deleteAccount(onResult: (success: Boolean, errorMessage: String?) -> Unit) {
        val app = getApplication<Application>()
        val auth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser
        val userId = currentUserId

        suspend fun purgeLocalData() {
            withContext(Dispatchers.IO) {
                if (userId > 0) {
                    FirebaseRepository.deleteUserDocument(userId.toString())
                    dao.deleteDashboardByUserId(userId)
                    dao.deleteExpensesByUserId(userId)
                    dao.deleteUserById(userId)
                }
                dao.deleteAllTransactions()
            }
            clearAnalyticsPreferences(app)
            clearGraphPreference(app)
            UserSessionManager.clearSession(app)
            resetStateAfterAccountDeletion()
        }

        if (firebaseUser != null) {
            firebaseUser.delete().addOnCompleteListener { task ->
                viewModelScope.launch {
                    if (task.isSuccessful) {
                        purgeLocalData()
                        auth.signOut()
                        onResult(true, null)
                    } else {
                        val msg = task.exception?.localizedMessage
                            ?: task.exception?.message
                        onResult(false, msg)
                    }
                }
            }
        } else {
            viewModelScope.launch {
                purgeLocalData()
                auth.signOut()
                onResult(true, null)
            }
        }
    }

    private fun resetStateAfterAccountDeletion() {
        currentUserId = 0
        currentUserName = ""
        currentUserEmail = ""
        currentUserPassword = ""
        dashboardAge = 0
        monthlySalary = 0.0
        food = 0.0
        rent = 0.0
        medical = 0.0
        emi = 0.0
        additional = 0.0
        education = 0.0
        prediction = 0.0
        insight = ""
        alerts = emptyList()
        savingsPercent = 0
        trend = ""
        aiPredictedExpenses = defaultCategoryTotals()
    }

    private suspend fun syncMonthlyExpenseTransactions(
        food: Double,
        rent: Double,
        medical: Double,
        emi: Double,
        education: Double,
        additional: Double
    ) {
        val app = getApplication<Application>()
        val period = getAnalyticsPeriod(app)
        val txnDate = period?.startDateMillis ?: System.currentTimeMillis()
        dao.deleteTransactionsByOrigin(TRANSACTION_ORIGIN_MONTHLY)
        val rows = listOf(
            Transaction(amount = food, type = "expense", category = "Food", date = txnDate, origin = TRANSACTION_ORIGIN_MONTHLY),
            Transaction(amount = rent, type = "expense", category = "Rent", date = txnDate, origin = TRANSACTION_ORIGIN_MONTHLY),
            Transaction(amount = medical, type = "expense", category = "Medical", date = txnDate, origin = TRANSACTION_ORIGIN_MONTHLY),
            Transaction(amount = emi, type = "expense", category = "EMI", date = txnDate, origin = TRANSACTION_ORIGIN_MONTHLY),
            Transaction(amount = education, type = "expense", category = "Education", date = txnDate, origin = TRANSACTION_ORIGIN_MONTHLY),
            Transaction(amount = additional, type = "expense", category = "Additional", date = txnDate, origin = TRANSACTION_ORIGIN_MONTHLY)
        )
        rows.forEach { dao.insertTransaction(it) }
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
            refreshAIPredictedExpenses()
        }
    }
    fun getAIPredictedExpenses(): List<Pair<String, Float>> = aiPredictedExpenses

    private fun refreshAIPredictedExpenses() {
        viewModelScope.launch {
            val expenseTransactions = dao.getAllTransactionsList()
                .filter { it.type.equals("expense", ignoreCase = true) }
            aiPredictedExpenses = calculateCategoryTotals(expenseTransactions)
        }
    }

    private fun calculateCategoryTotals(transactions: List<Transaction>): List<Pair<String, Float>> {
        if (transactions.isEmpty()) {
            return listOf(
                "Food" to food.toFloat(),
                "Rent" to rent.toFloat(),
                "Medical" to medical.toFloat(),
                "EMI" to emi.toFloat(),
                "Education" to education.toFloat(),
                "Other" to additional.toFloat()
            )
        }

        val totals = mutableMapOf(
            "Food" to 0f,
            "Rent" to 0f,
            "Medical" to 0f,
            "EMI" to 0f,
            "Education" to 0f,
            "Other" to 0f
        )

        transactions.forEach { transaction ->
            val key = when (transaction.category.trim().lowercase()) {
                "food" -> "Food"
                "rent" -> "Rent"
                "medical" -> "Medical"
                "emi" -> "EMI"
                "education" -> "Education"
                else -> "Other"
            }
            totals[key] = totals.getValue(key) + transaction.amount.toFloat()
        }

        return totals.toList()
    }

    private fun defaultCategoryTotals(): List<Pair<String, Float>> = listOf(
        "Food" to 0f,
        "Rent" to 0f,
        "Medical" to 0f,
        "EMI" to 0f,
        "Education" to 0f,
        "Other" to 0f
    )
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
