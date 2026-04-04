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
import androidx.compose.material.icons.filled.Person

import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.res.stringResource
import com.example.cashcactus.R
import com.example.cashcactus.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable

fun RegisterScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    val pastelBg = Color(0xFF796A6A)
    val glassColor = Color.White.copy(alpha = 0.15f)

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var registerSuccess by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF26A69A), Color(0xFF7E57C2))
                )
            )
    ) {

        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = stringResource(R.string.create_account),
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(30.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {

                Column(modifier = Modifier.padding(20.dp)) {

                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it.filter { ch -> ch.isLetter() || ch.isWhitespace() }
                        },
                        label = { Text(stringResource(R.string.full_name)) },
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(stringResource(R.string.email)) },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

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
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(onClick = {

                        if (email.isNotEmpty() && password.isNotEmpty()) {

                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->

                                    if (task.isSuccessful) {

                                        Toast.makeText(
                                            context,
                                            "Registration Successful",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        navController.navigate("login")

                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Registration Failed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                        } else {
                            Toast.makeText(context, "Enter all fields", Toast.LENGTH_SHORT).show()
                        }

                    }) {
                        Text("Register")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = { navController.navigate("login") }) {
                Text(
                    text = stringResource(R.string.already_account_login),
                    color = Color.White
                )
            }
        }
    }

    if (registerSuccess) {
        LaunchedEffect(Unit) {
            navController.navigate("home") {
                popUpTo("register") { inclusive = true }
            }
        }
    }
}