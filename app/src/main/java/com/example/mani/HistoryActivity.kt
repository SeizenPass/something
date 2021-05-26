package com.example.mani

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mani.adapters.HistoryAdapter
import com.example.mani.models.HistoryModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null
    private var history: ArrayList<HistoryModel> = ArrayList()
    private var income: Int = 0
    private var expense: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth!!.currentUser
        getListFromFirebase()
    }

    private fun getListFromFirebase() {
        var firebaseDatabase = FirebaseDatabase.getInstance()
        var databaseRef = firebaseDatabase.getReference("users")
            .child(currentUser!!.email!!.toString().replace('.', ',')).child("history")
        val childEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                history.clear()
                //keys = ArrayList()
                for (node in snapshot.children) {
                    val type = node.child("type").value.toString()
                    val amount = node.child("amount").value.toString().toInt()
                    val date = node.child("date").value.toString()
                    val item = HistoryModel(
                        type, amount, date
                    )
                    history.add(item)
                    if (type == "expense") expense += amount
                    else if (type == "income") income += amount
                }
                history.sortByDescending { it.date }
                if (history.size > 0) {
                    history_list.visibility = View.VISIBLE
                    setupHistoryListRecyclerView()
                } else {
                    history_list.visibility = View.GONE
                }
                total_expense.text = "Total expense: $expense"
                total_income.text = "Total income: $income"
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        databaseRef.addValueEventListener(childEventListener)
    }

    private fun setupHistoryListRecyclerView() {
        history_list.layoutManager = LinearLayoutManager(this)
        history_list.setHasFixedSize(true)

        val adapter = HistoryAdapter(this, history)
        history_list.adapter = adapter
    }
}