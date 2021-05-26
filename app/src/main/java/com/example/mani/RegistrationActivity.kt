package com.example.mani

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegistrationActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private lateinit var registerButton: Button
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        mAuth = FirebaseAuth.getInstance()
        registerButton = findViewById(R.id.register_button)
        emailText = findViewById(R.id.editTextTextEmailAddress)
        passwordText = findViewById(R.id.editTextTextPassword)
        registerButton.setOnClickListener {
            var email = emailText.text.toString().trim()
            var password = passwordText.text.toString().trim()
            mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener{
                    if (it.isSuccessful) {
                        var user: FirebaseUser = mAuth!!.currentUser!!
                        Toast.makeText(this, "Account was created.", Toast.LENGTH_LONG)
                            .show()
                        val data = Intent()
                        data.putExtra("email", email)
                        data.putExtra("password", password)
                        setResult(RESULT_OK, data)
                        finish()
                    } else {
                        Log.d("Error: ", it.toString())
                    }

                }
        }
    }
}