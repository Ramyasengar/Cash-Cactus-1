package com.example.cashcactus.ui.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cashcactus.R
import com.example.cashcactus.ui.components.BaseScreen
import com.example.cashcactus.ui.components.CashCactusCard
import com.example.cashcactus.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var submitAttempted by remember { mutableStateOf(false) }

    // ✅ Email validation
    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    // ✅ Password validation (8 chars + 1 special char)
    val isPasswordValid =
        password.length >= 8 && password.any { !it.isLetterOrDigit() }

    val showEmailError = submitAttempted && !isEmailValid
    val showPasswordError = submitAttempted && !isPasswordValid

    val isFormValid = name.isNotBlank() && isEmailValid && isPasswordValid

    BaseScreen(title = stringResource(R.string.register)) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.create_account),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            CashCactusCard(modifier = Modifier.fillMaxWidth()) {
                Column {

                    // ✅ NAME
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it.filter { ch ->
                                ch.isLetter() || ch.isWhitespace()
                            }
                        },
                        label = { Text(stringResource(R.string.full_name)) },
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // ✅ EMAIL
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(stringResource(R.string.email)) },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        isError = showEmailError,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (showEmailError) {
                        Text(
                            text = "Invalid email format",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // ✅ PASSWORD
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(stringResource(R.string.password)) },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = {
                                passwordVisible = !passwordVisible
                            }) {
                                Icon(
                                    if (passwordVisible)
                                        Icons.Default.Visibility
                                    else
                                        Icons.Default.VisibilityOff,
                                    null
                                )
                            }
                        },
                        visualTransformation =
                            if (passwordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                        isError = showPasswordError,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (showPasswordError) {
                        Text(
                            text = "Password must be 8+ chars with 1 special character",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ✅ BUTTON
                    Button(
                        onClick = {
                            submitAttempted = true

                            if (isFormValid) {
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                context,
                                                "Registration Successful",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            navController.navigate("privacy?fromRegister=true") {
                                                popUpTo("register") { inclusive = true }
                                            }
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Registration Failed",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Fix errors before continuing",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        enabled = isFormValid,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.register))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.already_account_login))
            }
        }
    }
}