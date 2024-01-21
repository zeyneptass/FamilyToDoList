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
import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.ktx.Firebase
class UserRegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityUserRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_register)

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
        if (password == confirmPassword) {
            if (password == confirmPassword) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentUser = auth.currentUser
                        val profileUpdateRequest = userProfileChangeRequest { displayName = username }

                        currentUser?.updateProfile(profileUpdateRequest)?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                // Kullanıcı oluşturulduktan sonra userType'ı Firestore'a kaydet
                                val userType = intent.getStringExtra("userType") ?: UserType.child.name
                                saveUserTypeToFirestore(currentUser?.uid, email, username, userType)

                                Toast.makeText(applicationContext, "User is added.", Toast.LENGTH_LONG).show()
                                val intent = Intent(this, ProfileActivity::class.java)
                                startActivity(intent)
                                finish()
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
                val intent = Intent(this,UserRegisterActivity::class.java)
                startActivity(intent)
            }
            val userName = binding.usernameText.text.toString()

            val intent = Intent(applicationContext,ProfileActivity::class.java)
            intent.putExtra("sendedUserName",userName)
            startActivity(intent)
        }

    }

    private fun saveUserTypeToFirestore(userId: String?, userEmail: String, username: String, userType: String) {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")

        userId?.let {
            // Kullanıcıyı kullanıcılar koleksiyonuna ekleyin
            usersCollection.document(it).set(
                mapOf(
                    "email" to userEmail,
                    "username" to username,
                    "userType" to userType
                )
            )
        }
    }
}