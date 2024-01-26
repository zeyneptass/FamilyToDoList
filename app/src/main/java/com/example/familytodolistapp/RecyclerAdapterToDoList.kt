package com.example.familytodolistapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapterToDoList(private val toDoList: List<ToDoListData>) : RecyclerView.Adapter<RecyclerAdapterToDoList.ToDoListVH>() {
    class ToDoListVH(itemView: View) : RecyclerView.ViewHolder(itemView){
        val dateTimePickerEditText: EditText = itemView.findViewById(R.id.dateTimePickerEditText)
        val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
        val titleText: EditText = itemView.findViewById(R.id.titleTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoListVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_each_task, parent, false)
        return ToDoListVH(itemView)
    }

    override fun getItemCount(): Int {
        return toDoList.size
    }

    override fun onBindViewHolder(holder: ToDoListVH, position: Int) {
        val currentItem = toDoList[position]

        holder.dateTimePickerEditText.setText(currentItem.dueDate)
        holder.titleText.setText(currentItem.title)

        holder.checkbox.isChecked = currentItem.complated

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, UserToDoListActivity::class.java)
            holder.itemView.context.startActivity(intent)
        }
    }
}
