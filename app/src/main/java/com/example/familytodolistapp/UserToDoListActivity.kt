package com.example.familytodolistapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class UserToDoListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerAdapterToDoList
    private var toDoList: MutableList<ToDoListData> = mutableListOf()
    private lateinit var auth: FirebaseAuth
    private lateinit var dateTimePickerEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_to_do_list)

        auth = Firebase.auth
        recyclerView = findViewById(R.id.toDoRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecyclerAdapterToDoList(toDoList)
        recyclerView.adapter = adapter



        val addToDoListItem = findViewById<ShapeableImageView>(R.id.addToDoListItem)
        addToDoListItem.setOnClickListener {
            showAddToDoDialog()
        }
    }

    fun showDateTimePicker(view: View) {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth-${month + 1}-$year"

                val timePickerDialog = TimePickerDialog(
                    this,
                    TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                        val selectedTime = "$hourOfDay:$minute"
                        val dateTime = "$selectedDate $selectedTime"

                        val dateTimePickerEditText = findViewById<EditText>(R.id.dateTimePickerEditText)
                        dateTimePickerEditText.setText(dateTime)
                    },
                    currentHour,
                    currentMinute,
                    true
                )
                timePickerDialog.show()
            },
            currentYear,
            currentMonth,
            currentDay
        )
        datePickerDialog.show()
    }

    private fun showAddToDoDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Yeni To Do Ekle")

        val inflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.activity_each_task, null)
        builder.setView(dialogView)

        builder.setPositiveButton("Tamam") { _, _ ->
            val titleTextView: EditText = dialogView.findViewById(R.id.titleTextView)
            val dueTextView: EditText = dialogView.findViewById(R.id.dateTimePickerEditText)
            val checkbox: CheckBox = dialogView.findViewById(R.id.checkbox)

            val title = titleTextView.text.toString()
            val dueDate = dueTextView.text.toString()
            val isCompleted = checkbox.isChecked

            addToDoList(title, dueDate, isCompleted)
        }

        builder.setNegativeButton("İptal") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    fun addToDoList(title: String, dueDate: String, isCompleted: Boolean) {
        val currentUser = auth.currentUser
        val userId = currentUser?.uid

        userId?.let {
            val db = FirebaseFirestore.getInstance()
            val toDoListCollection = db.collection("users").document(it).collection("ToDoList")

            val newTask = hashMapOf(
                "complated" to isCompleted,
                "dueDate" to dueDate,
                "title" to title
            )

            toDoListCollection.add(newTask)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(applicationContext, "Yeni görev eklendi.", Toast.LENGTH_SHORT)
                        .show()
                    // RecyclerView'ı güncelle
                    getToDoList()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        applicationContext,
                        "Hata: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    fun addToDoList(view: View) {
        showAddToDoDialog()
    }

    fun getToDoList() {
        val currentUser = auth.currentUser
        val userId = currentUser?.uid

        userId?.let {
            val db = FirebaseFirestore.getInstance()

            db.collection("users").document(it).collection("ToDoList").get()
                .addOnSuccessListener { documents ->
                    toDoList = mutableListOf()

                    for (document in documents) {
                        val completed = document.getBoolean("completed") ?: false
                        val dueDate = document.getString("dueDate") ?: ""
                        val title = document.getString("title") ?: ""

                        toDoList.add(ToDoListData(completed, dueDate, title))
                    }

                    adapter = RecyclerAdapterToDoList(toDoList)
                    recyclerView.adapter = adapter
                }
                .addOnFailureListener { exception ->
                    // Hata durumunda yapılacak işlemler
                }
        }
    }
}
