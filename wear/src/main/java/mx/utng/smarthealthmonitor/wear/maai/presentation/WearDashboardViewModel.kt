package mx.utng.smarthealthmonitor.wear.maai.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import mx.utng.smarthealthmonitor.maai.data.SmartHealthRepository

class WearDashboardViewModel : ViewModel() {

    val fc: StateFlow<Int> = SmartHealthRepository.fcFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val pasos: StateFlow<Int> = SmartHealthRepository.pasosFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
}