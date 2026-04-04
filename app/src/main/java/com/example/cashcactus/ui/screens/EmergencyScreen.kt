package com.example.cashcactus.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import androidx.compose.ui.res.stringResource
import com.example.cashcactus.utils.EmergencyAccessManager
import com.example.cashcactus.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyScreen(navController: NavHostController) {

    val pastelBg = Color(0xFF796A6A)
    val glassColor = Color.White.copy(alpha = 0.15f)
    val context = LocalContext.current
    var tempPin by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedDuration by remember { mutableLongStateOf(5 * 60 * 1000L) }
    var remainingSeconds by remember { mutableIntStateOf(
        EmergencyAccessManager.getRemainingSeconds(
            context
        )
    ) }

    val durations = listOf(
        stringResource(R.string.duration_5) to 5 * 60 * 1000L,
        stringResource(R.string.duration_15) to 15 * 60 * 1000L,
        stringResource(R.string.duration_30) to 30 * 60 * 1000L
    )

    LaunchedEffect(remainingSeconds) {
        if (remainingSeconds > 0) {
            delay(1000)
            remainingSeconds -= 1
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.emergency_mode)) }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = stringResource(R.string.emergency_desc),
                style = MaterialTheme.typography.bodyLarge
            )

            Card(modifier = Modifier.fillMaxWidth()) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    durations.forEach { (label, duration) ->

                        OutlinedButton(
                            onClick = { selectedDuration = duration },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (selectedDuration == duration)
                                    "$label ${stringResource(R.string.selected)}"
                                else label
                            )
                        }
                    }

                    OutlinedTextField(
                        value = tempPin,
                        onValueChange = {
                            if (it.length <= 4 && it.all(Char::isDigit)) tempPin = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.temp_pin)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Button(
                        onClick = {
                            if (tempPin.length == 4) {

                                EmergencyAccessManager.createTemporaryPin(
                                    context = context,
                                    pin = tempPin,
                                    durationMillis = selectedDuration
                                )

                                remainingSeconds = (selectedDuration / 1000L).toInt()

                                message = context.getString(R.string.pin_active)

                            } else {
                                message = context.getString(R.string.pin_invalid)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.generate_access))
                    }
                }
            }

            if (message.isNotBlank()) {
                Text(text = message, color = MaterialTheme.colorScheme.primary)
            }

            if (remainingSeconds > 0) {
                Text(
                    text = stringResource(
                        R.string.access_expires,
                        remainingSeconds
                    )
                )
            }

            OutlinedButton(
                onClick = {
                    EmergencyAccessManager.clear(context)
                    remainingSeconds = 0
                    tempPin = ""
                    message = context.getString(R.string.access_revoked)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.revoke_access))
            }

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.back_to_vault))
            }
        }
    }
}
