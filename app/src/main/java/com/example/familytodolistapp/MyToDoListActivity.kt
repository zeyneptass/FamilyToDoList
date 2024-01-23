package com.example.familytodolistapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyToDoListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerAdapterToDoList
    private var toDoList: MutableList<ToDoListData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_to_do_list)

        recyclerView = findViewById(R.id.myToDoListsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val userToDoListActivity = UserToDoListActivity()
        toDoList = userToDoListActivity.getToDoList()

        // RecyclerView adapter'ını oluşturun ve bağlayın
        adapter = RecyclerAdapterToDoList(toDoList)
        recyclerView.adapter = adapter
    }
}