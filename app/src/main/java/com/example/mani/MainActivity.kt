package com.example.mani

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlin.reflect.KClass

class MainActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        val sharedPref = getSharedPreferences("auth", Context.MODE_PRIVATE)
        val email = sharedPref.getString("email", "DEFAULT")
        val password = sharedPref.getString("password", "DEFAULT")
        if (email != "DEFAULT") {
            mAuth!!.signInWithEmailAndPassword(email.toString(), password.toString())
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        currentUser = mAuth!!.currentUser
                    }
                }
        }
        if (currentUser == null && email == "DEFAULT") startAuthActivity()
        val incomeBtn = findViewById<Button>(R.id.button_plus)
        val expenseBtn = findViewById<Button>(R.id.button_minus)
        val historyBtn = findViewById<Button>(R.id.button_history)
        val logoutBtn = findViewById<Button>(R.id.button_logout)
        incomeBtn.setOnClickListener {
            val incomeIntent = Intent(this, Income::class.java)
            startActivityForResult(incomeIntent, INCOME_CODE)
        }
        expenseBtn.setOnClickListener {
            val expenseIntent = Intent(this, Expense::class.java)
            startActivityForResult(expenseIntent, EXPENSE_CODE)
        }
        historyBtn.setOnClickListener {
            val historyIntent = Intent(this, HistoryActivity::class.java)
            startActivity(historyIntent)
        }

        logoutBtn.setOnClickListener{
            currentUser = null
            mAuth!!.signOut()
            val ed = sharedPref.edit()
            ed.remove("email")
            ed.remove("password")
            ed.apply()
            startAuthActivity()
        }
        
    }

    private fun startAuthActivity() {
        val authIntent = Intent(this@MainActivity, AuthActivity::class.java)
        startActivityForResult(authIntent, LOGIN)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == INCOME_CODE) {
            val incomeFromActivity =  data?.getStringExtra("income")
            //findViewById<TextView>(R.id.view_in).text = "Incomes: $incomeFromActivity"
            //TODO SAVE IT TO DATABASE
        } else if (requestCode == EXPENSE_CODE) {
            val expenseFromActivity =  data?.getStringExtra("expense")
            //findViewById<TextView>(R.id.view_ex).text = "Expenses: $expenseFromActivity"
            //TODO SAVE IT TO DATABASE
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        val INCOME_CODE = 1
        val EXPENSE_CODE = 2
        val LOGIN = 3
    }
}