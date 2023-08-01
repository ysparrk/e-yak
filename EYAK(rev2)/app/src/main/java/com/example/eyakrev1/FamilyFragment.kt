package com.example.eyakrev1

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import androidx.fragment.app.Fragment

class FamilyFragment : Fragment() {

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
        Family(familyId = -1, familyIcon = "", familyName = "", familyNickname = ""),
        )

    // https://curryyou.tistory.com/386
    // 1. Context를 할당할 변수를 프로퍼티로 선언(어디서든 사용할 수 있게)
    lateinit var mainActivity: MainActivity
    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.family_tab_main, container, false)

        val familyListAdapter = FamilyListAdapter(mainActivity, familyList)
        val familyListView = layout.findViewById<ListView>(R.id.familyListView)
        familyListView?.adapter = familyListAdapter

        val editFamilyBtn = layout.findViewById<ImageView>(R.id.editFamily)

        editFamilyBtn.setOnClickListener {
            mainActivity!!.gotoEditFamily()
        }

        return layout
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // 2. Context를 액티비티로 형변환해서 할당
        mainActivity = context as MainActivity
    }
}