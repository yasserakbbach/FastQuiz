package com.ressay.fastquiz.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.ressay.fastquiz.R
import com.ressay.fastquiz.data.models.User
import com.ressay.fastquiz.databinding.ActivityLoginBinding
import com.ressay.fastquiz.ui.main.MainActivity

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.apply {
            login.setOnClickListener(this@LoginActivity)
            signup.setOnClickListener(this@LoginActivity)
        }
    }

    override fun onClick(v: View) {

        when(v.id) {

            R.id.login -> login()
            R.id.signup -> signup()
        }
    }

    private fun login() {

        val user = validateCredentials() ?: return

        mAuth.signInWithEmailAndPassword(user.email, user.password)
            .addOnSuccessListener {

                startActivity(
                    Intent(applicationContext, MainActivity::class.java)
                )
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, getString(R.string.invalid_credentials), Toast.LENGTH_LONG).show()
            }
    }

    private fun validateCredentials() : User? {

        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        var error = ""

        if(email.isEmpty()) {
            error = getString(R.string.email_required)
        }

        if(password.isEmpty()) {
            error = error.plus("\n")
                         .plus(getString(R.string.password_required))
        }

        return if(error.isNotEmpty()) {
            Toast.makeText(applicationContext, error, Toast.LENGTH_LONG).show()
            null
        }else {
            User(email, password)
        }

    }

    private fun signup() {

        val user = validateCredentials() ?: return

        mAuth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, getString(R.string.registred_successfully), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }
}