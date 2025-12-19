package com.example.pm_ud3.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import com.example.pm_ud3.viewmodel.UserViewModel

class ShakeSyncCoordinator(
    context: Context,
    viewModel: UserViewModel
) {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometer =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val detector = ShakeDetector {
        viewModel.sync()
    }

    fun start() {
        sensorManager.registerListener(
            detector,
            accelerometer,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    fun stop() {
        sensorManager.unregisterListener(detector)
    }
}