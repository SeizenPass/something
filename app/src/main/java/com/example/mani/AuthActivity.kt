package com.example.mani

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthActivity : AppCompatActivity() {
    private lateinit var registerButton: Button
    private lateinit var loginButton: Button
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private var mAuth: FirebaseAuth? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        registerButton = findViewById(R.id.toRegisterButton)
        loginButton = findViewById(R.id.login_button)
        emailText = findViewById(R.id.editTextTextEmailAddress)
        passwordText = findViewById(R.id.editTextTextPassword)
        mAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        loginButton.setOnClickListener {
            if (emailText.text.toString().trim() != "" && passwordText.text.toString().trim() != "") {
                login(emailText.text.toString().trim(), passwordText.text.toString().trim())
            }
        }

        registerButton.setOnClickListener {
            val regIntent = Intent(this@AuthActivity, RegistrationActivity::class.java)
            startActivityForResult(regIntent, REGISTER)
        }
    }

    private fun login(email: String, password: String) {
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                    task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    val pf = getSharedPreferences("auth", Context.MODE_PRIVATE)
                    val ed = pf.edit()
                    ed.putString("email", email)
                    ed.putString("password", password)
                    ed.apply()
                    Toast.makeText(this, "Signed in.", Toast.LENGTH_LONG).show()
                    val regUser = Intent()
                    regUser.putExtra("user", mAuth!!.currentUser)
                    setResult(RESULT_OK, regUser)
                    finish()
                } else {
                    Toast.makeText(this, "Couldn't sign in.", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REGISTER) {
                login(data!!.getStringExtra("email").toString(),
                    data.getStringExtra("password").toString())
            }
        }
    }

    companion object {
        const val REGISTER = 0
    }
}