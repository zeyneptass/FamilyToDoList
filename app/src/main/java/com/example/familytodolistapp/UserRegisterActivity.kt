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
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class UserRegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityUserRegisterBinding


    private lateinit var usersCollection: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        val familiesCollection = intent.getSerializableExtra("families") as? CollectionReference
            ?: FirebaseFirestore.getInstance().collection("families") // Default olarak bir koleksiyon atanabilir


        // familyCollection'ın null olup olmadığını kontrol et
        if (familiesCollection == null) {
            Toast.makeText(applicationContext, "Families collection is null.", Toast.LENGTH_LONG).show()
            finish()
        } else {
            // familiesCollection varsa, usersCollection'ı bu koleksiyon altında oluştur
            usersCollection = familiesCollection.document("users").collection("users")
        }


    }

    fun signInButtonForUser(view: View) {
        val email = binding.registerEmailText.text.toString()
        val username = binding.usernameText.text.toString()
        val password = binding.passwordText.text.toString()
        val confirmPassword = binding.passwordAgainText.text.toString()

        if (password == confirmPassword) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentUser = auth.currentUser
                        val profileUpdateRequest = userProfileChangeRequest { displayName = username }

                        currentUser?.updateProfile(profileUpdateRequest)?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                // userType'ı önceki sayfadan al
                                val userType = intent.getStringExtra("userType") ?: UserType.child.name
                                saveUserTypeToFirestore(currentUser?.uid, email, username, userType)

                                Toast.makeText(applicationContext, "User is added.", Toast.LENGTH_LONG).show()
                                startActivity(Intent(this, ProfileActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(applicationContext, "Failed to update username", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(applicationContext, task.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                }
        } else {
            Toast.makeText(applicationContext, "Your passwords do not match.", Toast.LENGTH_LONG).show()
        }
    }


    private fun saveUserTypeToFirestore(userId: String?, userEmail: String, username: String, userType: String) {
        userId?.let {
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
