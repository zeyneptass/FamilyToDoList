package com.example.familytodolistapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.familytodolistapp.databinding.ActivityUserRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class UserRegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityUserRegisterBinding
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
    }
    fun signInButtonForUser(view: View) {
        val email = binding.registerEmailText.text.toString()
        val username = binding.usernameText.text.toString()
        val password = binding.passwordText.text.toString()
        val confirmPassword = binding.passwordAgainText.text.toString()
        val familyName = intent.getStringExtra("familyName")

        if (password == confirmPassword) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    val profileUpdateRequest = userProfileChangeRequest { displayName = username }

                    currentUser?.updateProfile(profileUpdateRequest)?.addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            if (familyName != null) {
                                saveUserToRealtimeDatabase(currentUser?.uid, familyName, email, username)
                                Toast.makeText(applicationContext, "User is added.", Toast.LENGTH_LONG).show()
                                val intent = Intent(this, ProfileActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(applicationContext, "Family name is null.", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(applicationContext, "Failed to update username", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(applicationContext, "Your passwords do not match.", Toast.LENGTH_LONG).show()
        }
    }


    private fun saveUserToRealtimeDatabase(userId: String?, familyName: String, userEmail: String, username: String) {
        val usersReference = database.reference.child("families").child(familyName).child("users").child(userId ?: "")

        usersReference.setValue(
            mapOf(
                "email" to userEmail,
                "username" to username
            )
        )
    }
}
