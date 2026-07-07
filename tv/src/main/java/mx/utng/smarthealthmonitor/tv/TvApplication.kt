// tv/src/main/java/mx/utng/smarthealthmonitor/tv/TvApplication.kt
package mx.utng.smarthealthmonitor.tv

import android.app.Application
import mx.utng.smarthealthmonitor.maai.data.SmartHealthRepository

/**
 * Application class del módulo TV.
 *
 * Inicializa [SmartHealthRepository] con el Context de la aplicación para
 * que el DAO de Room esté disponible antes de que cualquier Fragment intente
 * acceder al historial de lecturas.
 *
 * Registrada en AndroidManifest.xml con android:name=".TvApplication"
 */
class TvApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SmartHealthRepository.init(this)
    }
}
