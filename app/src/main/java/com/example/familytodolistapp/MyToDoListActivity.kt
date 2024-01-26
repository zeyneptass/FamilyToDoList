package com.example.familytodolistapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class MyToDoListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerAdapterToDoList
    private var toDoList: MutableList<ToDoListData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_to_do_list)

        recyclerView = findViewById(R.id.myToDoListsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Doğrudan getToDoList fonksiyonunu çağırın
        toDoList = getToDoList()

        // RecyclerView adapter'ını oluşturun ve bağlayın
        adapter = RecyclerAdapterToDoList(toDoList)
        recyclerView.adapter = adapter
    }

    // getToDoList fonksiyonunu doğrudan bu sınıf içinde kullanabilirsiniz
    private fun getToDoList(): MutableList<ToDoListData> {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        if (userId != null) {
            // UserToDoListActivity sınıfından ayrı bir örnek oluşturmanıza gerek yok
            // Sınıf içinde olduğunuz için doğrudan çağırabilirsiniz
            return getToDoList()
        } else {
            // Kullanıcı kimliği null ise, boş bir liste döndürebilir veya gerekirse başka bir işlem yapabilirsiniz.
            return mutableListOf()
        }
    }
}
