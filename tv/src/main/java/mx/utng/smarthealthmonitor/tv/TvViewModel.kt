// tv/src/main/java/mx/utng/smarthealthmonitor/tv/TvViewModel.kt
package mx.utng.smarthealthmonitor.tv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import mx.utng.smarthealthmonitor.maai.data.SmartHealthRepository
import mx.utng.smarthealthmonitor.maai.data.db.LecturaFC

/**
 * ViewModel del módulo TV.
 *
 * Expone los datos del [SmartHealthRepository] como [StateFlow] para que
 * [MainFragment] pueda observarlos de forma reactiva con collectAsState.
 *
 * - [fc]       → Frecuencia cardíaca actual del wearable (0 si no hay dato)
 * - [pasos]    → Pasos actuales del wearable
 * - [historial] → Últimas lecturas de FC almacenadas en Room
 *
 * WhileSubscribed(5_000) cancela el Flow 5 segundos después de que no haya
 * observers, lo que evita mantener recursos activos en background.
 */
class TvViewModel : ViewModel() {

    // FC actual del wearable (o 0 si no hay dato aún)
    val fc: StateFlow<Int> = SmartHealthRepository.fcFlow
        .stateIn(
            scope            = viewModelScope,
            started          = SharingStarted.WhileSubscribed(5_000),
            initialValue     = 0
        )

    // Pasos actuales del wearable
    val pasos: StateFlow<Int> = SmartHealthRepository.pasosFlow
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )

    // Historial de lecturas desde Room DAO (últimas 20 por defecto en el DAO)
    val historial: StateFlow<List<LecturaFC>> =
        SmartHealthRepository.obtenerHistorial()
            .stateIn(
                scope        = viewModelScope,
                started      = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
}
