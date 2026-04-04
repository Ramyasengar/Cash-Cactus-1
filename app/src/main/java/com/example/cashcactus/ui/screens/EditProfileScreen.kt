package com.example.cashcactus.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.res.stringResource
import com.example.cashcactus.R
import com.example.cashcactus.viewmodel.MainViewModel

@Composable
fun EditProfileScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {

    val pastelBg = Color(0xFF796A6A)
    val glassColor = Color.White.copy(alpha = 0.15f)
    val context = LocalContext.current

    var name by remember { mutableStateOf(viewModel.currentUserName) }
    var email by remember { mutableStateOf(viewModel.currentUserEmail) }
    var password by remember { mutableStateOf(viewModel.currentUserPassword) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(pastelBg)   // ✅ ADD THIS
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            stringResource(id = R.string.edit_profile),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(id = R.string.name)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(id = R.string.email)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(id = R.string.password)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                if (name.isBlank() || email.isBlank() || password.isBlank()) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.all_fields_required),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

                viewModel.updateUser(
                    name,
                    email,
                    password,
                    viewModel.currentUserEmail
                )

                Toast.makeText(
                    context,
                    context.getString(R.string.profile_updated),
                    Toast.LENGTH_SHORT
                ).show()

                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.update_profile))
        }
    }
}