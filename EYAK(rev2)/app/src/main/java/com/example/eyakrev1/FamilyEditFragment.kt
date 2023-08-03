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

    var familyList = arrayListOf<Family>(
        Family(familyId = 1, familyIcon = "baseline_person_24", familyName = "이름 1", familyNickname = "닉네임 1"),
        Family(familyId = 1, familyIcon = "baseline_person_24", familyName = "이름 1", familyNickname = "닉네임 1"),
        Family(familyId = 1, familyIcon = "baseline_person_24", familyName = "이름 1", familyNickname = "닉네임 1"),
        Family(familyId = 1, familyIcon = "baseline_person_24", familyName = "이름 1", familyNickname = "닉네임 1"),
        Family(familyId = 1, familyIcon = "baseline_person_24", familyName = "이름 1", familyNickname = "닉네임 1"),
        Family(familyId = 1, familyIcon = "baseline_person_24", familyName = "이름 1", familyNickname = "닉네임 1"),
        Family(familyId = 1, familyIcon = "baseline_person_24", familyName = "이름 1", familyNickname = "닉네임 1"),
        Family(familyId = 1, familyIcon = "baseline_person_24", familyName = "이름 1", familyNickname = "닉네임 1"),
        Family(familyId = 1, familyIcon = "baseline_person_24", familyName = "이름 1", familyNickname = "닉네임 1"),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_family_edit, container, false)

        layout.findViewById<Button>(R.id.addFamily).setOnClickListener {
            mainActivity!!.gotoAddFamily()
        }

        familyList.add(Family(familyId = -1, familyIcon = "baseline_person_24", familyName = "빈 공간", familyNickname = "빈 공간"))



        return layout
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }

}