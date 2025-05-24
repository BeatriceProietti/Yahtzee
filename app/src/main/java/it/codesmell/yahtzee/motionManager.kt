package it.codesmell.yahtzee

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.widget.Toast
import kotlin.math.abs

class motionManager(private val context: Context) : SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var gyroscope: Sensor? = null

    private var lastEventTime = 0L
    private val rotationThreshold = 1.0f // soglia di rotazione (rad/s)
    private val cooldown = 1000L // 1 secondo tra due trigger

    fun start() {
        sensorManager = context.getSystemService(SensorManager::class.java)
        gyroscope = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        if (gyroscope != null) {
            sensorManager?.registerListener(
                this,
                gyroscope,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            Log.d("MotionManager", "Gyroscope listener registered")
        } else {
            Log.w("MotionManager", "Gyroscope not available")
        }
    }

    fun stop() {
        sensorManager?.unregisterListener(this)
        Log.d("MotionManager", "Gyroscope listener unregistered")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values[0] // rotazione attorno all'asse X
            val y = it.values[1] // rotazione attorno all'asse Y
            val z = it.values[2] // rotazione attorno all'asse Z

            Log.d("MotionManager", "Gyro: x=$x, y=$y, z=$z")

            val now = System.currentTimeMillis()
            if (abs(y) > rotationThreshold && now - lastEventTime > cooldown) {
                lastEventTime = now
                Log.d("MotionManager", "💥 ROTAZIONE DETECTED")
                Toast.makeText(context, "ROTATION DETECTED", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // non usato in questo caso
    }
}
