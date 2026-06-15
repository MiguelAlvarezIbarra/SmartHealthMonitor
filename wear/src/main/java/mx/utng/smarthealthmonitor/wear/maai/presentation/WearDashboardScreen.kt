package mx.utng.smarthealthmonitor.wear.maai.presentation

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.scrollAway
import kotlinx.coroutines.launch
import mx.utng.smarthealthmonitor.wear.maai.presentation.components.WearFCCard

@Composable
fun WearDashboardScreen(
    onAlertClick: () -> Unit = {},
    viewModel: WearDashboardViewModel = viewModel()
) {
    val fc by viewModel.fc.collectAsState()
    val pasos by viewModel.pasos.collectAsState()
    val listState = rememberScalingLazyListState()

    // — Rotary Input —
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    // Solicita el foco al entrar en composición para recibir eventos de corona/bisel
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // ✅ El botón de alerta solo aparece cuando FC es anormal (< 60 o > 100)
    // Si fc == 0 todavía no hay lectura, tampoco mostramos alerta
    val fcAnormal = fc > 0 && fc !in 60..100

    Scaffold(
        timeText = {
            TimeText(modifier = Modifier.scrollAway(listState))
        },
        positionIndicator = {
            PositionIndicator(scalingLazyListState = listState)
        }
    ) {
        ScalingLazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                // Recibe eventos rotary (corona/bisel) y hace scroll animado
                .onRotaryScrollEvent { event ->
                    coroutineScope.launch {
                        listState.animateScrollBy(event.verticalScrollPixels)
                    }
                    true
                }
                .focusRequester(focusRequester)
                .focusable()
        ) {
            item {
                WearFCCard(
                    fc = fc,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                CompactChip(
                    onClick = { },
                    label = {
                        // ✅ Muestra "--" si aún no hay lectura de pasos
                        Text(text = if (pasos == 0) "-- pasos" else "$pasos pasos")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            // ✅ Solo renderiza el botón si la FC es anormal
            if (fcAnormal) {
                item {
                    Chip(
                        label = { Text("⚠ Alerta FC: $fc bpm") },
                        onClick = onAlertClick,
                        colors = ChipDefaults.primaryChipColors(
                            backgroundColor = MaterialTheme.colors.error
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}