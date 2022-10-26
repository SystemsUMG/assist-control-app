package com.cristiangonzalez.assistcontrol

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cristiangonzalez.assistcontrol.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupUI()

    }

    private fun setupUI() {
        binding.signOutButton.setOnClickListener {
            signOut()
        }
    }

    private fun signOut() {
        startActivity(LoginActivity.getLaunchIntent(this))
        //FirebaseAuth.getInstance().signOut()
    }
}