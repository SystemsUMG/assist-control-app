package com.cristiangonzalez.assistcontrol

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.cristiangonzalez.assistcontrol.database.entities.UserEntity
import com.cristiangonzalez.assistcontrol.databinding.ActivityHomeBinding
import com.cristiangonzalez.assistcontrol.interfaces.LoginService
import com.cristiangonzalez.assistcontrol.ui.UserViewModel
import com.cristiangonzalez.assistcontrol.utils.goToActivity
import com.cristiangonzalez.assistcontrol.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var retrofitInstance: LoginService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        retrofitInstance = RetrofitInstance
            .getRetrofitInstance()
            .create(LoginService::class.java)

        setupUI()

    }

    private fun setupUI() {
        binding.signOutButton.setOnClickListener {
            signOut()
        }
    }

    private fun signOut() {
        startActivity(LoginActivity.getLaunchIntent(this))
        //Acceso a db asincrona
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val currentUser = userViewModel.getUser()
                if (currentUser != null) {
                    userViewModel.deleteUser(currentUser)
                    toast("Cerrar sesion " + currentUser.name)
                    //Ir a MainActivity
                    goToActivity<LoginActivity> {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
            } catch (e: SQLiteException) {
                toast(e.toString())
            }
        }
    }
}