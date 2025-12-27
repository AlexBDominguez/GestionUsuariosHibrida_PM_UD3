package com.example.pm_ud3.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import com.example.pm_ud3.viewmodel.UserViewModel

class ShakeSyncCoordinator(
    private val context: Context,
    private val viewModel: UserViewModel
) {

    private val detector = ShakeDetector(context) {
        viewModel.sync()
    }

    fun start() {
        detector.start()
    }

    fun stop() {
        detector.stop()
    }
}