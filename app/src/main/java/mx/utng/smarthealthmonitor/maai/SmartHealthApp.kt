package mx.utng.smarthealthmonitor.maai

import android.app.Application
import mx.utng.smarthealthmonitor.maai.data.SmartHealthRepository

class SmartHealthApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SmartHealthRepository.init(this)
    }
}