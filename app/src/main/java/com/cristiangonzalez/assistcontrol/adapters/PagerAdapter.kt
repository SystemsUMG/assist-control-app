package com.cristiangonzalez.assistcontrol.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(fa: FragmentActivity): FragmentStateAdapter(fa) {

    private val fragmentList = ArrayList<Fragment>()

    override fun createFragment(position: Int) = fragmentList[position]

    override fun getItemCount() = fragmentList.size

    fun addFragment(fragment: Fragment) = fragmentList.add(fragment)

}