package com.cristiangonzalez.assistcontrol

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
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
        //Mostrar progressBar al frente
        binding.loginProgressBar.progressBar.bringToFront()

        retrofitInstance = RetrofitInstance
            .getRetrofitInstance()
            .create(LoginService::class.java)

        binding.buttonLogIn.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            //Validar credenciales
            if (isValidEmail(email) && isValidPassword(password)) {
                logIn(email, password)
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

//    override fun onStart() {
//        super.onStart()
//        val user = FirebaseAuth.getInstance().currentUser
//        if (user != null) {
//            startActivity(HomeActivity.getLaunchIntent(this))
//            finish()
//        }
//    }

    private fun logIn(email: String, password: String) {
        showProgressBar()
        //Acceder a DB de forma asincrona
        CoroutineScope(Dispatchers.Main).launch {
            try {

                login()
                val user = userViewModel.findUser(email)
//                if (user != null) {
//                    if (user.password == password) {
//                        toast(R.string.login_success)
//                        //Ir a MainActivity
//                        goToActivity<MainActivity> {
//                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        }
//                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//                        hideProgressBar()
//                    } else {
//                        toast(R.string.login_incorrect_password)
//                        hideProgressBar()
//                    }
//                } else {
//                    toast(R.string.login_incorrect_user)
//                    hideProgressBar()
//                }
            } catch (e: SQLiteException) {
                toast(R.string.error_unexpected)
                hideProgressBar()
            }
        }
    }

    private fun login() {
        val user = User("cgonzalezf@miumg.edu.gt", "admin")
        val postResponse : LiveData<Response<Login>> = liveData {
            val response = retrofitInstance.login(user)
            emit(response)
        }

        postResponse.observe(this, Observer {
            if (it.body() != null) {
                val login = it.body()
                login?.message?.let { it1 -> toast(it1) }
            } else {
                try {
                    val jObjError = it.errorBody()?.string()?.let { it1 -> JSONObject(it1) }
                    jObjError?.getString("message")?.let { it1 -> toast(it1) }
                } catch (e: Exception) {
                    e.message?.let { it1 -> toast(it1) }
                }
                hideProgressBar()
            }
        })



//        //Consumir api asincrona
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//
//
//
//                val call = getRetrofit().create(DonutsAPI::class.java).getDonuts("0e91b8f1-5790-423a-868a-b249224ce1bb")
//                val donutsResponse = call.body()
//                runOnUiThread {
//
//                }
//            } catch (e: Exception) {
//                runOnUiThread {
//                    showError(R.string.error_internet)
//                }
//            }
//        }
    }

    private fun showError(text: Int) {
        toast(text)
    }

    private fun showProgressBar() {
        binding.loginProgressBar.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.loginProgressBar.progressBar.visibility = View.GONE
    }
}