package mx.utng.smarthealthmonitor.maai.data

import android.util.Log
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.util.UUID

object MqttHelper {

    private const val TAG = "MqttHelper"
    // Usamos el broker publico de HiveMQ
    private const val BROKER_URL = "tcp://broker.emqx.io:1883"
    
    // Topic único para evitar cruces con otros usuarios en el broker público
    private const val TOPIC_FC = "smarthealth/utng/mijel123/fc"
    private const val TOPIC_PASOS = "smarthealth/utng/mijel123/pasos"
    private const val TOPIC_ALERTAS = "smarthealth/utng/mijel123/alertas"

    private var mqttClient: MqttClient? = null

    fun connect(onMessageReceived: (topic: String, message: String) -> Unit) {
        if (mqttClient != null && mqttClient!!.isConnected) return

        try {
            val clientId = UUID.randomUUID().toString()
            mqttClient = MqttClient(BROKER_URL, clientId, MemoryPersistence())

            val options = MqttConnectOptions().apply {
                isCleanSession = true
                connectionTimeout = 10
                keepAliveInterval = 20
            }

            mqttClient?.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    Log.w(TAG, "Conexión perdida: ${cause?.message}")
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    if (topic != null && message != null) {
                        val payload = String(message.payload)
                        Log.d(TAG, "Mensaje recibido en $topic: $payload")
                        onMessageReceived(topic, payload)
                    }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {}
            })

            mqttClient?.connect(options)
            Log.i(TAG, "Conectado al broker MQTT: $BROKER_URL")

            // Suscribirse a los tópicos
            mqttClient?.subscribe(TOPIC_FC)
            mqttClient?.subscribe(TOPIC_PASOS)
            mqttClient?.subscribe(TOPIC_ALERTAS)

        } catch (e: Exception) {
            Log.e(TAG, "Error conectando a MQTT", e)
        }
    }

    fun publishFC(bpm: Int) {
        publish(TOPIC_FC, bpm.toString())
    }

    fun publishPasos(pasos: Int) {
        publish(TOPIC_PASOS, pasos.toString())
    }

    fun publishAlerta(mensaje: String) {
        publish(TOPIC_ALERTAS, mensaje)
    }

    private fun publish(topic: String, payload: String) {
        try {
            if (mqttClient?.isConnected == true) {
                val message = MqttMessage(payload.toByteArray())
                message.qos = 1 // At least once
                mqttClient?.publish(topic, message)
                Log.d(TAG, "Publicado en $topic: $payload")
            } else {
                Log.w(TAG, "No se pudo publicar, cliente no conectado.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error publicando en $topic: ${e.message}")
        }
    }

    fun disconnect() {
        try {
            mqttClient?.disconnect()
            mqttClient = null
            Log.i(TAG, "Desconectado del broker MQTT")
        } catch (e: Exception) {
            Log.e(TAG, "Error desconectando: ${e.message}")
        }
    }
}
