package com.cristiangonzalez.assistcontrol

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.cristiangonzalez.assistcontrol.database.entities.UserEntity
import com.cristiangonzalez.assistcontrol.databinding.ActivityLoginBinding
import com.cristiangonzalez.assistcontrol.interfaces.LoginService
import com.cristiangonzalez.assistcontrol.models.Login
import com.cristiangonzalez.assistcontrol.models.User
import com.cristiangonzalez.assistcontrol.ui.UserViewModel
import com.cristiangonzalez.assistcontrol.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var retrofitInstance: LoginService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.loginProgressBar.progressBar.bringToFront()//Mostrar progressBar al frente

        retrofitInstance = RetrofitInstance
            .getRetrofitInstance()
            .create(LoginService::class.java)

        binding.buttonLogIn.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            //Validar credenciales
            if (isValidEmail(email) && isValidPassword(password)) {
                login(email, password)
            } else {
                toast(R.string.login_incorrect_data)
            }
        }

        //Validar campo email
        binding.editTextEmail.validate {
            binding.textInputEmail.error = if (isValidEmail(it)) null else getString(R.string.login_invalid_email)
        }
    }

    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    private fun login(email: String, password: String) {
        showProgressBar()
        val user = User(email, password)
        val postResponse : LiveData<Response<Login>> = liveData {
            val response = retrofitInstance.login(user)
            emit(response)
        }

        postResponse.observe(this) {
            if (it.body() != null) {
                storeUser(it.body()!!)
            } else {
                try {
                    val responseError = it.errorBody()?.string()?.let { it1 -> JSONObject(it1) }
                    responseError?.getString("message")?.let { it1 -> toast(it1) }
                } catch (e: Exception) {
                    e.message?.let { it1 -> toast(it1) }
                }
                hideProgressBar()
            }
        }
    }

    private fun storeUser(response: Login) {
        //Acceso a db asincrona
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val currentUser = userViewModel.getUser()
                //Registrar usuario
                val user: UserEntity? = response.records
                if (user != null) {
                    if (user.type == "student") {
                        if (currentUser != null) {
                            userViewModel.deleteUser(currentUser)
                        }
                        userViewModel.insertUser(user)
                        toast(response.message + " " + user.name)
                        //Ir a MainActivity
                        goToActivity<MainActivity> {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    } else {
                        toast(R.string.login_invalid_user)
                    }

                }
                hideProgressBar()
            } catch (e: SQLiteException) {
                toast(R.string.error_unexpected)
                hideProgressBar()
            }
        }
    }

    private fun showProgressBar() {
        binding.loginProgressBar.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.loginProgressBar.progressBar.visibility = View.GONE
    }
}