package com.a103.eyakrev1

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView

class FamilyEditFragment : Fragment() {

    lateinit var mainActivity: MainActivity

    var familyList = arrayListOf<Family>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_family_edit, container, false)

        familyList.add(Family())
        val familyEditListAdapter = FamilyEditListAdapter(mainActivity, familyList)
        val familyEditListView = layout.findViewById<ListView>(R.id.familyEditListView)
        familyEditListView?.adapter = familyEditListAdapter

        return layout
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }

}