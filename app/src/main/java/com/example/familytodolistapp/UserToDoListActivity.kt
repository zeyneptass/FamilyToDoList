package com.example.familytodolistapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class UserToDoListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerAdapterToDoList
    private var toDoList: MutableList<ToDoListData> = mutableListOf()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_to_do_list)
        auth = Firebase.auth
    }
    fun showDateTimePicker(view: View) {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        // DatePickerDialog oluştur
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                // Seçilen tarihi kullan
                val selectedDate = "$dayOfMonth-${month + 1}-$year"

                // TimePickerDialog'u göster
                val timePickerDialog = TimePickerDialog(
                    this,
                    TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                        // Seçilen saati kullan
                        val selectedTime = "$hourOfDay:$minute"

                        // Tarih ve saat bilgisini EditText'e yaz
                        val dateTime = "$selectedDate $selectedTime"

                        // dateTimePickerButton'a erişim sağla
                        val dateTimePickerEditText = findViewById<EditText>(R.id.dateTimePickerButton)
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
            val dueTextView: EditText = dialogView.findViewById(R.id.dateTimePickerButton)
            val checkbox: CheckBox = dialogView.findViewById(R.id.checkbox)

            val title = titleTextView.text.toString()
            val dueDate = dueTextView.text.toString()
            val isCompleted = checkbox.isChecked

            val currentUser = auth.currentUser
            val userId = currentUser?.uid

            userId?.let {
                val db = FirebaseFirestore.getInstance()
                db.collection("users").document(it).collection("ToDoList")
                    .add(hashMapOf(
                        "title" to title,
                        "dueDate" to dueDate,
                        "completed" to isCompleted
                    ))
                    .addOnSuccessListener { documentReference ->
                        // Yeni görev eklendiğinde yapılacak işlemler
                    }
                    .addOnFailureListener { e ->
                        // Hata durumunda yapılacak işlemler
                    }
            }
        }

        builder.setNegativeButton("İptal") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }
    fun addToDoList(view: View) {
        val addToDoListItem = findViewById<ShapeableImageView>(R.id.addToDoListItem)
        addToDoListItem.setOnClickListener {
            showAddToDoDialog()
        }
        recyclerView = findViewById(R.id.toDoRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
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

    fun getToDoList(): MutableList<ToDoListData> {
        // toDoList'i döndür
        return toDoList
    }
}
