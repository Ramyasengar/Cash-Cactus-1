package com.example.cashcactus.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
fun LoginScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    BaseScreen(title = stringResource(R.string.login)) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.welcome_user),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            CashCactusCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                        },
                        label = { Text(stringResource(R.string.email)) },
                        isError = emailError,
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (emailError) Text("Enter valid email", style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = password.length < 6
                        },
                        label = { Text(stringResource(R.string.password)) },
                        isError = passwordError,
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (passwordError) Text("Minimum 6 characters required", style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = { navController.navigate("forgotPassword") }, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.forgot_password))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (email.isNotEmpty() && password.isNotEmpty()) {
                                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val user = auth.currentUser
                                        viewModel.currentUserId = user?.uid?.hashCode() ?: 0
                                        navController.navigate("home") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } else {
                                        Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Enter all fields", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(stringResource(R.string.login)) }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = { navController.navigate("register") }, modifier = Modifier.fillMaxWidth()) {
                Text("Don't have an account? Register")
            }
        }
    }
}
