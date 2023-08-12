package com.a103.eyakrev1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

class AlarmClickedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_clicked)

        val iotLocations = intent.getIntegerArrayListExtra("IOT_LOCATIONS")
        Toast.makeText(this, iotLocations.toString(), Toast.LENGTH_LONG).show()


    }
}