package com.example.cashcactus.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.cashcactus.R
import com.example.cashcactus.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {

    val pastelBg = Color(0xFF796A6A)
    val glassColor = Color.White.copy(alpha = 0.15f)

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    var passwordVisible by remember { mutableStateOf(false) }
    var loginSuccess by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF7E57C2), Color(0xFF26A69A))
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = stringResource(R.string.welcome_user),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(30.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {

                Column(modifier = Modifier.padding(20.dp)) {

                    // EMAIL
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

                    if (emailError) {
                        Text("Enter valid email", color = Color.Red)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // PASSWORD
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
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (passwordError) {
                        Text("Minimum 6 characters required", color = Color.Red)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    TextButton(
                        onClick = {
                            navController.navigate("forgotPassword")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.forgot_password))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(onClick = {

                        if (email.isNotEmpty() && password.isNotEmpty()) {

                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->

                                    if (task.isSuccessful) {

                                        val user = auth.currentUser

                                        // Save user ID in ViewModel
                                        viewModel.currentUserId = user?.uid?.hashCode() ?: 0

                                        navController.navigate("home")

                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Login Failed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                        } else {
                            Toast.makeText(context, "Enter all fields", Toast.LENGTH_SHORT).show()
                        }

                    }) {
                        Text("Login")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = { navController.navigate("register") }) {
                Text("Don't have an account? Register", color = Color.White)
            }
        }
    }

    if (loginSuccess) {
        LaunchedEffect(Unit) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }
}