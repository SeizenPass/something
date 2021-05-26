package com.example.mani

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.mani.models.HistoryModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.math.exp

class Expense : AppCompatActivity() {


    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    private var chosenDate: String = ""
    private var currentUser: FirebaseUser? = null
    private var mAuth: FirebaseAuth? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth!!.currentUser
        val datePickBtn = findViewById<Button>(R.id.pickdate1)
        datePickBtn.setOnClickListener {
            val c: Calendar = Calendar.getInstance()
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)


            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    val monthOfYearFormatted = monthOfYear.toString().padStart(2, '0')
                    val dayOfMonthFormatted = dayOfMonth.toString().padStart(2, '0')
                    chosenDate = "$year/$monthOfYearFormatted/$dayOfMonthFormatted"
                    findViewById<TextView>(R.id.pickdate1).setText(
                        dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                    )
                }, mYear, mMonth, mDay
            )
            datePickerDialog.show()
        }

        val expenseButton = findViewById<Button>(R.id.but_ex)

        expenseButton.setOnClickListener {
            val intent = Intent()
            val expenseString = findViewById<EditText>(R.id.expence_w).text.toString()
            intent.putExtra("expense", expenseString)
            setResult(0, intent)
            var firebaseDatabase = FirebaseDatabase.getInstance()
            var databaseRef = firebaseDatabase.getReference("users")
                .child(currentUser!!.email!!.toString().replace('.', ',')).child("history")
            val historyModel = HistoryModel("expense", expenseString.toInt(), chosenDate)
            databaseRef.push().setValue(historyModel) { error, ref ->
                if (error == null) {
                    setResult(RESULT_OK);
                    finish()//finishing activity
                }
            }
        }

    }
}