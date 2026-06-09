package mx.utng.smarthealthmonitor.wear.maai

import android.content.Context
import android.util.Log
import androidx.health.services.client.HealthServices
import androidx.health.services.client.PassiveListenerService
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.PassiveListenerConfig
import androidx.health.services.client.data.SampleDataPoint
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.*

class HealthDataService : PassiveListenerService() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val TAG = "HealthDataService"

    override fun onNewDataPointsReceived(dataPoints: DataPointContainer) {
        val fcDataPoints = dataPoints.getData(DataType.HEART_RATE_BPM)
        val lastFC = fcDataPoints.lastOrNull()
        if (lastFC is SampleDataPoint<Double>) {
            val bpm = lastFC.value.toInt()
            Log.d(TAG, "FC recibida (última del lote): $bpm BPM")
            // scope.launch {
            //     enviarDato(applicationContext, "/smarthealthmonitor/fc", bpm.toString(), "BPM")
            // }
        }

        val stepsDataPoints = dataPoints.getData(DataType.STEPS_DAILY)
        val lastSteps = stepsDataPoints.lastOrNull()
        if (lastSteps != null) {
            val value = lastSteps.value
            val pasos = when (value) {
                is Long   -> value.toInt()
                is Double -> value.toInt()
                is Int    -> value
                else      -> value.toString().toIntOrNull() ?: 0
            }
            Log.d(TAG, "Pasos recibidos: $pasos")
            // scope.launch {
            //     enviarDato(applicationContext, "/smarthealthmonitor/pasos", pasos.toString(), "Pasos")
            // }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        Log.d(TAG, "HealthDataService destruido")
    }

    companion object {
        private const val TAG = "HealthDataService"

        private suspend fun enviarDato(context: Context, path: String, valor: String, label: String) =
            withContext(Dispatchers.IO) {
                try {
                    Log.i(TAG, "📡 Intentando enviar $label=$valor por path=$path")
                    val nodeClient    = Wearable.getNodeClient(context)
                    val messageClient = Wearable.getMessageClient(context)
                    val data          = valor.toByteArray(Charsets.UTF_8)

                    val nodes = Tasks.await(nodeClient.connectedNodes)
                    Log.i(TAG, "🔗 Nodos conectados: ${nodes.size} → ${nodes.map { "${it.displayName}(${it.id})" }}")

                    if (nodes.isEmpty()) {
                        Log.w(TAG, "⚠️ Sin nodos conectados. ¿Están ambos emuladores pareados?")
                        return@withContext
                    }

                    for (node in nodes) {
                        try {
                            // Esperamos la confirmación real de envío
                            Tasks.await(messageClient.sendMessage(node.id, path, data))
                            Log.i(TAG, "✅ $label enviado a ${node.displayName} (${node.id}): $valor")
                        } catch (e: Exception) {
                            Log.e(TAG, "❌ Error enviando a ${node.displayName}: ${e.message}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Error general en enviarDato($label): ${e.javaClass.simpleName}: ${e.message}")
                }
            }

        suspend fun registrar(context: Context) {
            try {
                val hsClient      = HealthServices.getClient(context)
                val passiveClient = hsClient.passiveMonitoringClient

                val config = PassiveListenerConfig.builder()
                    .setDataTypes(setOf(
                        DataType.HEART_RATE_BPM,
                        DataType.STEPS_DAILY
                    ))
                    .setShouldUserActivityInfoBeRequested(true)
                    .build()

                passiveClient.setPassiveListenerServiceAsync(
                    HealthDataService::class.java,
                    config
                )
                Log.d(TAG, "Health Services registrado correctamente")
            } catch (e: Exception) {
                Log.e(TAG, "Error registrando: ${e.message}")
            }
        }

        suspend fun enviarFCDirectamente(context: Context, bpm: Int) {
            enviarDato(context, "/smarthealthmonitor/fc", bpm.toString(), "BPM")
        }

        suspend fun enviarPasosDirectamente(context: Context, pasos: Int) {
            enviarDato(context, "/smarthealthmonitor/pasos", pasos.toString(), "Pasos")
        }
    }
}