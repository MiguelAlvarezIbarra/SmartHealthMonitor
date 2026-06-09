package mx.utng.smarthealthmonitor.maai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import mx.utng.smarthealthmonitor.maai.ui.theme.SmartHealthMonitorTheme

@Composable
fun AlertaScreen(
    fc: Int,
    onDismiss: () -> Unit,
    onConfirmar: (nota: String) -> Unit
) {
    var enviando by remember { mutableStateOf(false) }
    var nota by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "Enviar alerta de emergencia",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "FC actual: $fc bpm",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Se notificará a tus contactos de emergencia.\n" +
                           "Esta acción no se puede deshacer."
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                OutlinedTextField(
                    value = nota,
                    onValueChange = { nota = it },
                    label = { Text("Nota opcional") },
                    placeholder = { Text("Ej. Me siento mareado") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    enviando = true
                    onConfirmar(nota)
                },
                enabled = !enviando,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                if (enviando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onError,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "CONFIRMAR ALERTA",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Preview(showBackground = true, name = "Alerta - Light")
@Preview(showBackground = true, name = "Alerta - Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AlertaScreenPreview() {
    SmartHealthMonitorTheme {
        AlertaScreen(fc = 145, onDismiss = { }, onConfirmar = { })
    }
}
