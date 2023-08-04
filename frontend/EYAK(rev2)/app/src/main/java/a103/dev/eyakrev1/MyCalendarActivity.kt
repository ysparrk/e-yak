package a103.dev.eyakrev1

import a103.dev.eyakrev1.databinding.ActivityMyCalendarBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MyCalendarActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMyCalendarBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


    }
}