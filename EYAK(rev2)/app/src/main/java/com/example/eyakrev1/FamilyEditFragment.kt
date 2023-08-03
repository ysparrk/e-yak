package com.example.eyakrev1

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class FamilyEditFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_family_edit, container, false)

        layout.findViewById<Button>(R.id.addFamily).setOnClickListener {
            mainActivity!!.gotoAddFamily()
        }

        return layout
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }

}