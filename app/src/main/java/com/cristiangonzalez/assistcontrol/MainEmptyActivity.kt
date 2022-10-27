package com.cristiangonzalez.assistcontrol

import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cristiangonzalez.assistcontrol.interfaces.LoginService
import com.cristiangonzalez.assistcontrol.ui.UserViewModel
import com.cristiangonzalez.assistcontrol.utils.goToActivity
import com.cristiangonzalez.assistcontrol.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainEmptyActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()
    private lateinit var retrofitInstance: LoginService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retrofitInstance = RetrofitInstance
            .getRetrofitInstance()
            .create(LoginService::class.java)

        //Acceso a db asincrona
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val currentUser = userViewModel.getUser()
                if (currentUser == null) {
                    goToActivity<LoginActivity> {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                } else {
                    goToActivity<MainActivity> {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                }
            } catch (e: SQLiteException) {
                toast(e.toString())
            }
        }

        finish()
    }

}