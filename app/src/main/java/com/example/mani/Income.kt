package com.example.mani

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.icu.lang.UCharacter
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.mani.models.HistoryModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import java.util.*


class Income : AppCompatActivity() {
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    private var chosenDate: String = ""
    private var currentUser: FirebaseUser? = null
    private var mAuth: FirebaseAuth? = null
    @SuppressLint("CutPasteId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_income)
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth!!.currentUser
        val datePickBtn = findViewById<Button>(R.id.income_date1)
        datePickBtn.setOnClickListener {
            val c: Calendar = Calendar.getInstance()
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)


            val datePickerDialog = DatePickerDialog(
                this,
                OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val monthOfYearFormatted = monthOfYear.toString().padStart(2, '0')
                    val dayOfMonthFormatted = dayOfMonth.toString().padStart(2, '0')
                    chosenDate = "$year/$monthOfYearFormatted/$dayOfMonthFormatted"
                    findViewById<TextView>(R.id.income_date1).text = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                }, mYear, mMonth, mDay
            )
            datePickerDialog.show()
        }

        val incomeButton = findViewById<Button>(R.id.but_in)

        incomeButton.setOnClickListener {
            val intent = Intent()
            val incomeText = findViewById<EditText>(R.id.income_w).text.toString()
            intent.putExtra("income", incomeText)
            setResult(0, intent)
            var firebaseDatabase = FirebaseDatabase.getInstance()
            var databaseRef = firebaseDatabase.getReference("users")
                .child(currentUser!!.email!!.toString().replace('.', ',')).child("history")
            val historyModel = HistoryModel("income", incomeText.toInt(), chosenDate)
            databaseRef.push().setValue(historyModel) { error, ref ->
                if (error == null) {
                    setResult(RESULT_OK);
                    finish()//finishing activity
                }
            }
        }
    }
}