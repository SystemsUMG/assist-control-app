package com.cristiangonzalez.assistcontrol

import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.cristiangonzalez.assistcontrol.adapters.PagerAdapter
import com.cristiangonzalez.assistcontrol.databinding.ActivityMainBinding
import com.cristiangonzalez.assistcontrol.interfaces.LoginService
import com.cristiangonzalez.assistcontrol.ui.UserViewModel
import com.cristiangonzalez.assistcontrol.utils.goToActivity
import com.cristiangonzalez.assistcontrol.utils.toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var retrofitInstance: LoginService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.mainToolbar)
        setUpViewPager(getPagerAdapter())
        setUpTabLayout()

        retrofitInstance = RetrofitInstance
            .getRetrofitInstance()
            .create(LoginService::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                showDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (binding.viewPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            binding.viewPager.currentItem = binding.viewPager.currentItem - 1
        }
    }

    private fun getPagerAdapter(): PagerAdapter {
        val adapter = PagerAdapter(this)
        adapter.addFragment(CoursesFragment())
        adapter.addFragment(StatisticFragment())

        return adapter
    }

    private fun setUpViewPager(adapter: PagerAdapter?) {
        binding.viewPager.adapter = adapter
    }

    private fun setUpTabLayout() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.tab_item_courses)
                }
                1 -> {
                    tab.text = getString(R.string.tab_item_statistics)
                }
            }
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
//                if (tab!!.position == 0) menuItem.itemId().show() else fab.hide()
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                //if (tab!!.position == 0) recyclerView.smoothScrollToPosition(0)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun showDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.logout_dialog_title))
            .setMessage(resources.getString(R.string.logout_dialog_message))
            .setNegativeButton(resources.getString(R.string.logout_dialog_negative)) { _, _ -> }
            .setPositiveButton(resources.getString(R.string.logout_dialog_positive)) { _, _ ->
                signOut()
                goToActivity<LoginActivity> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
            .show()
    }

    private fun signOut() {
        //Acceso a db asincrona
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val currentUser = userViewModel.getUser()
                if (currentUser != null) {
                    userViewModel.deleteUser(currentUser)
                    toast("Cerrar sesion " + currentUser.name)
                }
            } catch (e: SQLiteException) {
                toast(e.toString())
            }
        }
    }
}