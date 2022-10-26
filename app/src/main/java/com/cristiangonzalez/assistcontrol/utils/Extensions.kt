package com.cristiangonzalez.assistcontrol.utils

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.cristiangonzalez.assistcontrol.R
import com.google.android.material.textfield.TextInputEditText
import java.util.regex.Pattern

fun Activity.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, message, duration).show()

fun Activity.toast(resourceId: Int, duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, resourceId, duration).show()

fun ViewGroup.inflate(layoutId: Int) = LayoutInflater.from(context).inflate(layoutId, this, false)!!

inline fun <reified T : Activity> Activity.goToActivity(noinline init: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    intent.init()
    startActivity(intent)
}

fun TextInputEditText.validate(validation: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable) {
            validation(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    })
}

fun Activity.isValidEmail(email: String): Boolean {
    val pattern = Patterns.EMAIL_ADDRESS

    return pattern.matcher(email).matches() && email.endsWith(getString(R.string.login_email_domain))
}

//Validar contraseña con regex
fun Activity.isValidPassword(password: String): Boolean {
    val passwordPattern = "^.{4,}\$"
    val pattern = Pattern.compile(passwordPattern)
    return pattern.matcher(password).matches()
}