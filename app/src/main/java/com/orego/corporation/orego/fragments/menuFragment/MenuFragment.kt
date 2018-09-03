package com.orego.corporation.orego.fragments.menuFragment

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orego.corporation.orego.R
import com.orego.corporation.orego.fragments.MainActivity
import com.orego.corporation.orego.views.circleMenu.customview.CircleMenuView

class MenuFragment : Fragment() {

    lateinit var mainActivity: MainActivity


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val constraintLayout = inflater.inflate(R.layout.fragment_menu, container, false) as ConstraintLayout
        val circleMenu = constraintLayout.findViewById<CircleMenuView>(R.id.circle_menu)
        mainActivity = activity as MainActivity
        initListeners(circleMenu)
        return constraintLayout
    }


    private fun initListeners(menu: CircleMenuView) {
        menu.eventListener = object : CircleMenuView.EventListener() {

            override fun onMenuOpenAnimationStart(view: CircleMenuView) {
                Log.d("D", "onMenuOpenAnimationStart")
            }

            override fun onMenuOpenAnimationEnd(view: CircleMenuView) {
                Log.d("D", "onMenuOpenAnimationEnd")
            }

            override fun onMenuCloseAnimationStart(view: CircleMenuView) {
                Log.d("D", "onMenuCloseAnimationStart")
            }

            override fun onMenuCloseAnimationEnd(view: CircleMenuView) {
                Log.d("D", "onMenuCloseAnimationEnd")
            }

            override fun onButtonClickAnimationStart(view: CircleMenuView, index: Int) {
                Log.d("D", "onButtonClickAnimationStart| index: $index")
            }

            override fun onButtonClickAnimationEnd(view: CircleMenuView, index: Int) {
                Log.d("D", "onButtonClickAnimationEnd| index: $index")
                mainActivity.replaceFragment(index)
            }

            override fun onButtonLongClick(view: CircleMenuView, index: Int): Boolean {
                Log.d("D", "onButtonLongClick| index: $index")
                return true
            }

            override fun onButtonLongClickAnimationStart(view: CircleMenuView, index: Int) {
                Log.d("D", "onButtonLongClickAnimationStart| index: $index")
            }

            override fun onButtonLongClickAnimationEnd(view: CircleMenuView, index: Int) {
                Log.d("D", "onButtonLongClickAnimationEnd| index: $index")
            }
        }
    }

}