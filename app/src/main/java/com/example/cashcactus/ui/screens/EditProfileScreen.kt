package com.example.cashcactus.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cashcactus.R
import com.example.cashcactus.ui.components.BaseScreen
import com.example.cashcactus.ui.components.CashCactusCard
import com.example.cashcactus.viewmodel.MainViewModel

@Composable
fun EditProfileScreen(navController: NavHostController, viewModel: MainViewModel) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(viewModel.currentUserName) }
    var email by remember { mutableStateOf(viewModel.currentUserEmail) }
    var password by remember { mutableStateOf(viewModel.currentUserPassword) }

    BaseScreen(title = stringResource(id = R.string.edit_profile)) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            verticalArrangement = Arrangement.Center
        ) {
            CashCactusCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(stringResource(id = R.string.edit_profile), style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(id = R.string.name)) }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text(stringResource(id = R.string.email)) }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text(stringResource(id = R.string.password)) }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(onClick = {
                        if (name.isBlank() || email.isBlank() || password.isBlank()) {
                            Toast.makeText(context, context.getString(R.string.all_fields_required), Toast.LENGTH_SHORT).show(); return@Button
                        }
                        viewModel.updateUser(name, email, password, viewModel.currentUserEmail)
                        Toast.makeText(context, context.getString(R.string.profile_updated), Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(id = R.string.update_profile))
                    }
                }
            }
        }
    }
}
