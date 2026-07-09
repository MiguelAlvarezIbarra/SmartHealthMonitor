package mx.utng.smarthealthmonitor.maai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mx.utng.smarthealthmonitor.maai.data.SmartHealthRepository
import mx.utng.smarthealthmonitor.maai.ui.components.FilaHistorial
import mx.utng.smarthealthmonitor.maai.ui.components.TarjetaDato
import mx.utng.smarthealthmonitor.maai.ui.theme.SmartHealthMonitorTheme
import mx.utng.smarthealthmonitor.maai.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onHistorialClick: () -> Unit = {},
    onAlertClick: () -> Unit = {},
    viewModel: DashboardViewModel = viewModel()
) {
    val fc by viewModel.fc.collectAsState()
    val pasos by viewModel.pasos.collectAsState()
    val historial by viewModel.historial.collectAsState()

    // ── Estado del diálogo y Snackbar ──────────────────────
    var mostrarAlerta by remember { mutableStateOf(false) }
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // ── Diálogo condicional ────────────────────────────────
    if (mostrarAlerta) {
        AlertaScreen(
            fc = fc,
            onDismiss = { mostrarAlerta = false },
            onConfirmar = { nota ->
                mostrarAlerta = false
                scope.launch(Dispatchers.IO) {
                    mx.utng.smarthealthmonitor.maai.data.MqttHelper.publishAlerta(if (nota.isNotBlank()) "Alerta: $nota" else "ALERTA DE EMERGENCIA")
                }
                scope.launch {
                    val result = snackbarHost.showSnackbar(
                        message = "✅ Alerta enviada a tus contactos de emergencia${if (nota.isNotBlank()) " con nota" else ""}",
                        actionLabel = "Deshacer",
                        duration = SnackbarDuration.Long
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        snackbarHost.showSnackbar("Alerta cancelada")
                    }
                }
            }
        )
    }

    SmartHealthMonitorTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHost) },
            topBar = {
                TopAppBar(
                    title = { Text("SmartHealth", style = MaterialTheme.typography.titleLarge) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { mostrarAlerta = true },
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Enviar alerta de emergencia",
                        tint = MaterialTheme.colorScheme.onError
                    )
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    TarjetaDato(
                        valor = if (fc > 0) "$fc" else "--",
                        unidad = "bpm",
                        label = "Frecuencia cardíaca",
                        colorValor = MaterialTheme.colorScheme.error
                    )
                }
                item {
                    TarjetaDato(
                        valor = if (pasos > 0) "%,d".format(pasos) else "--",
                        unidad = "pasos",
                        label = "Pasos del día",
                        colorValor = MaterialTheme.colorScheme.primary
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Historial reciente", style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = onHistorialClick) { Text("Ver todo") }
                    }
                }
                items(historial, key = { it.id }) { lectura ->
                    FilaHistorial(lectura = lectura)
                }
                item {
                    OutlinedButton(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                val newFc = (60..110).random()
                                val newPasos = (3000..8000).random()
                                SmartHealthRepository.actualizarFC(newFc)
                                SmartHealthRepository.actualizarPasos(newPasos)
                                mx.utng.smarthealthmonitor.maai.data.MqttHelper.publishFC(newFc)
                                mx.utng.smarthealthmonitor.maai.data.MqttHelper.publishPasos(newPasos)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Simular dato del wearable (DEBUG)")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Dashboard - Light",
    showSystemUi = true, device = "id:pixel_6")
@Composable
private fun DashboardScreenPreview() {
    SmartHealthMonitorTheme { DashboardScreen() }
}