package mx.utng.smarthealthmonitor.maai

import android.content.Context
import androidx.health.services.client.HealthServices
import androidx.health.services.client.PassiveListenerService
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.PassiveListenerConfig
import androidx.health.services.client.data.SampleDataPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import mx.utng.smarthealthmonitor.maai.data.SmartHealthRepository

class HealthDataService : PassiveListenerService() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onNewDataPointsReceived(dataPoints: DataPointContainer) {
        val fcDataPoints = dataPoints.getData(DataType.HEART_RATE_BPM)
        fcDataPoints.forEach { dataPoint ->
            if (dataPoint is SampleDataPoint<*>) {
                val bpm = (dataPoint.value as Double).toInt()
                scope.launch { SmartHealthRepository.actualizarFC(bpm) }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object {
        suspend fun registrar(context: Context) {
            val hsClient = HealthServices.getClient(context)
            val passiveClient = hsClient.passiveMonitoringClient
            val config = PassiveListenerConfig.builder()
                .setDataTypes(setOf(DataType.HEART_RATE_BPM))
                .setShouldUserActivityInfoBeRequested(true)
                .build()
            passiveClient.setPassiveListenerServiceAsync(
                HealthDataService::class.java, config
            )
        }
    }
}