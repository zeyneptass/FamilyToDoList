package com.example.familytodolistapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.familytodolistapp.databinding.ActivityProfileBinding
import com.firebase.ui.auth.AuthUI
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.core.View
import com.google.firebase.firestore.firestore

class ProfileActivity : AppCompatActivity() {
    val db = Firebase.firestore
    private lateinit var auth : FirebaseAuth
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = Firebase.auth

        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val userType = intent.getStringExtra("userType")
        loadMyInfo(userType)
    }
    fun loadMyInfo(userType: String?) {
        val user = Firebase.auth.currentUser
        user?.let {
            val displayName = it.displayName
            val email = it.email

            // Update the TextViews with the user's data
            binding.nameTv.text = displayName
            binding.emailTv.text = email
            binding.userTypeTv.text = userType
        }
    }
    fun goToFamilyToDoList(view: View) {}
    fun btnDeleteAccount(view: View) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Delete Account")
        alertDialogBuilder.setMessage("Do you really want to delete your account? This action cannot be undone.")

        alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->

            AuthUI.getInstance().delete(this).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Account deletion failed..", Toast.LENGTH_SHORT).show()
                }
            }
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
    fun btnLogout(view: View) {
        auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    fun goToMyToDoList(view: android.view.View) {
        val intent = Intent(this,MyToDoListActivity::class.java)
        startActivity(intent)
    }

    fun btnComposeToDoList(view: android.view.View) {
        val intent = Intent(this,UserToDoListActivity::class.java)
        startActivity(intent)
    }

}