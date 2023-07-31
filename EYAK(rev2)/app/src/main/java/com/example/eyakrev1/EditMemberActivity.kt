package com.example.eyakrev1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager
import com.example.eyakrev1.databinding.ActivityEditMemberBinding

class EditMemberActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityEditMemberBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        loadData()

        binding.editCancel.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadData() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val nickName = pref.getString("KEY_NICKNAME", "")
        val wakeTimeH = pref.getString("KEY_WAKE_TIME_H", "")
        val wakeTimeM = pref.getString("KEY_WAKE_TIME_M", "")
        val breakfastTimeH = pref.getString("KEY_BREAKFAST_TIME_H", "")
        val breakfastTimeM = pref.getString("KEY_BREAKFAST_TIME_M", "")
        val lunchTimeH = pref.getString("KEY_LUNCH_TIME_H", "")
        val lunchTimeM = pref.getString("KEY_LUNCH_TIME_M", "")
        val dinnerTimeH = pref.getString("KEY_DINNER_TIME_H", "")
        val dinnerTimeM = pref.getString("KEY_DINNER_TIME_M", "")
        val bedTimeH = pref.getString("KEY_BED_TIME_H", "")
        val bedTimeM = pref.getString("KEY_BED_TIME_M", "")
        val eatingTimeH = pref.getString("KEY_EATING_TIME_H", "")
        val eatingTimeM = pref.getString("KEY_EATING_TIME_M", "")

        if(nickName != ""){
            binding.nickNameEdit.setText(nickName)
        }

        binding.wakeTimeHEdit.setText(wakeTimeH)
        binding.wakeTimeMEdit.setText(wakeTimeM)
        binding.breakfastTimeHEdit.setText(breakfastTimeH)
        binding.breakfastTimeMEdit.setText(breakfastTimeM)
        binding.lunchTimeHEdit.setText(lunchTimeH)
        binding.lunchTimeMEdit.setText(lunchTimeM)
        binding.dinnerTimeHEdit.setText(dinnerTimeH)
        binding.dinnerTimeMEdit.setText(dinnerTimeM)
        binding.bedTimeHEdit.setText(bedTimeH)
        binding.bedTimeMEdit.setText(bedTimeM)
        binding.eatingTimeHEdit.setText(eatingTimeH)
        binding.eatingTimeMEdit.setText(eatingTimeM)
    }

    private fun saveData(nickName: String, wakeTimeH: String, wakeTimeM: String, breakfastTimeH: String, breakfastTimeM: String,
                         lunchTimeH: String, lunchTimeM: String, dinnerTimeH: String, dinnerTimeM: String,
                         bedTimeH: String, bedTimeM: String, eatingTimeH: String, eatingTimeM: String){
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = pref.edit()

        editor.putString("KEY_NICKNAME", nickName)
            .putString("KEY_WAKE_TIME_H", if(wakeTimeH != "") wakeTimeH else "06")
            .putString("KEY_WAKE_TIME_M", if(wakeTimeM != "") wakeTimeM else "50")
            .putString("KEY_BREAKFAST_TIME_H", if(breakfastTimeH != "") breakfastTimeH else "07")
            .putString("KEY_BREAKFAST_TIME_M", if(breakfastTimeM != "") breakfastTimeM else "20")
            .putString("KEY_LUNCH_TIME_H", if(lunchTimeH != "") lunchTimeH else "11")
            .putString("KEY_LUNCH_TIME_M", if(lunchTimeM != "") lunchTimeM else "10")
            .putString("KEY_DINNER_TIME_H", if(dinnerTimeH != "") dinnerTimeH else "19")
            .putString("KEY_DINNER_TIME_M", if(dinnerTimeM != "") dinnerTimeM else "20")
            .putString("KEY_BED_TIME_H", if(bedTimeH != "") bedTimeH else "01")
            .putString("KEY_BED_TIME_M", if(bedTimeM != "") bedTimeM else "00")
            .putString("KEY_EATING_TIME_H", if(eatingTimeH != "") eatingTimeH else "00")
            .putString("KEY_EATING_TIME_M", if(eatingTimeM != "") eatingTimeM else "20")
            .apply()
    }
}