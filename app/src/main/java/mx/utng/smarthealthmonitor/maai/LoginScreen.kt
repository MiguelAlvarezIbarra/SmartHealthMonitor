package mx.utng.smarthealthmonitor.maai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.utng.smarthealthmonitor.maai.ui.theme.SmartHealthMonitorTheme

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit = {}) {

    var email       by remember { mutableStateOf("") }
    var password    by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isLoading   by remember { mutableStateOf(false) }
    var emailError  by remember { mutableStateOf("") }

    fun validar(): Boolean {
        emailError = when {
            email.isBlank()        -> "El email no puede estar vacío"
            !email.contains("@")   -> "Email inválido"
            password.length < 6    -> "Mínimo 6 caracteres"
            else                   -> ""
        }
        return emailError.isEmpty()
    }

    SmartHealthMonitorTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "SmartHealth Logo",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "SmartHealth Monitor",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; emailError = "" },
                    label = { Text("Correo electrónico") },
                    isError = emailError.isNotEmpty(),
                    supportingText = {
                        if (emailError.isNotEmpty())
                            Text(emailError, color = MaterialTheme.colorScheme.error)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = if (showPassword)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                if (showPassword) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                                contentDescription = "Toggle contraseña"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (validar()) {
                            isLoading = true
                            onLoginSuccess()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("ENTRAR", style = MaterialTheme.typography.labelLarge)
                    }
                }

                Spacer(Modifier.height(16.dp))

                TextButton(onClick = {}) {
                    Text("¿Olvidaste tu contraseña?")
                }
            }
        }
    }
}

@Preview(name = "Login - Light", showBackground = true, showSystemUi = true)
@Preview(name = "Login - Dark", showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Login - Big Font", showBackground = true, fontScale = 1.5f)
@Composable
private fun LoginScreenPreview() {
    SmartHealthMonitorTheme { LoginScreen() }
}