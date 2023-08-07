package com.a103.eyakrev1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class MedicineDetailFragment : Fragment() {

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layout = inflater.inflate(R.layout.fragment_medicine_detail, container, false)

        return layout
    }
}