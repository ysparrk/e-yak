package com.example.eyakrev1

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.eyakrev1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        initPage()

        binding.accountSetting.setOnClickListener {
            accountSettingClick()
        }

        binding.alarmTab.setOnClickListener {
            alarmTabClick()
        }

        binding.medicineTab.setOnClickListener {
            medicineTabClick()
        }

        binding.familyTab.setOnClickListener {
            familyTabClick()
        }

        binding.deviceTab.setOnClickListener {
            deviceTabClick()
        }

        binding.eyakLogo.setOnClickListener {
            alarmTabClick()
        }
    }

    private fun initPage() {
        changeTabColor(0)

    }
    private fun accountSettingClick() {
        val intent = Intent(this, EditMemberActivity::class.java)
        startActivity(intent)
    }

    private fun alarmTabClick() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragment, AlarmFragment())
            .commit()
        changeTabColor(0)
    }

    private fun medicineTabClick() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragment, MedicineFragment())
            .commit()
        changeTabColor(1)
    }

    private fun familyTabClick() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragment, FamilyFragment())
            .commit()
        changeTabColor(2)
    }

    private fun deviceTabClick() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragment, DeviceFragment())
            .commit()
        changeTabColor(3)
    }

    private fun changeTabColor(index: Int) {
        val colorOn = "#FF757863"
        val colorOff = "#FFC9DBB2"

        binding.alarmTab.setColorFilter(Color.parseColor(if(index == 0) colorOn else colorOff))
        binding.medicineTab.setColorFilter(Color.parseColor(if(index == 1) colorOn else colorOff))
        binding.familyTab.setColorFilter(Color.parseColor(if(index == 2) colorOn else colorOff))
        binding.deviceTab.setColorFilter(Color.parseColor(if(index == 3) colorOn else colorOff))
    }


}