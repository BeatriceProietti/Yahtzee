package it.codesmell.yahtzee

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.widget.Toast
import kotlin.math.abs


//cerchiamo di capire come cazzo si fa sta roba

class motionManager(private val context: Context) : SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null

    fun start() {
        sensorManager = context.getSystemService(SensorManager::class.java)//boh??
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer != null) {
            sensorManager?.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            Log.d("MotionManager", "Accelerometer listener registered")
        } else {
            Log.w("MotionManager", "Accelerometer not available")
        }
    }

    //sburzo
    fun stop() {
        sensorManager?.unregisterListener(this)
        Log.d("MotionManager", "Accelerometer listener unregistered")
    }

    override fun onSensorChanged(event: SensorEvent?) {// da modificare poi quando facciamo il lancio dei dadi
        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]
            Log.d("MotionManager", "Accel: x=$x, y=$y, z=$z")//per ora mi sputa fuori la posizione
            if(abs(x).toInt() > 2 && abs(y).toInt() > 2){

                Log.d("vibratore", "vibrotutto");
                Toast.makeText(gthis, "VIBRO TUTTO",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Sbirgma
    }
}