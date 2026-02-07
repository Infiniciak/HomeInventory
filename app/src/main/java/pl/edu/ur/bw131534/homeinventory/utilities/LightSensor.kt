package pl.edu.ur.bw131534.homeinventory.utilities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


class LightSensor(context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val lightSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)


    fun hasLightSensor(): Boolean {
        return lightSensor != null
    }


    fun getLightIntensity(): Flow<Float> = callbackFlow {


        val sensor = lightSensor
        if (sensor == null) {
            close(IllegalStateException("Czujnik światła nie jest dostępny."))
            return@callbackFlow
        }


        val sensorListener = object : SensorEventListener {

            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
                    trySend(event.values[0])
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }
        }


        sensorManager.registerListener(
            sensorListener,
            sensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        awaitClose {
            sensorManager.unregisterListener(sensorListener)
        }
    }
}
