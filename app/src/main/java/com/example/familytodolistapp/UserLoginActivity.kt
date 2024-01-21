package com.example.familytodolistapp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.familytodolistapp.databinding.ActivityUserLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class UserLoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityUserLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)

        binding = ActivityUserLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
    }

    fun userLoginButton(view: View) {
        val email = binding.loginEmailText.text.toString()
        val password = binding.loginPasswordText.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Giriş başarılı
                        Toast.makeText(applicationContext, "Login successful", Toast.LENGTH_SHORT).show()

                        // Kullanıcı bilgilerini Firestore'dan al ve diğer sayfaya yönlendir
                        getUserInfoAndNavigateToProfile(auth.currentUser?.uid)
                        val intent = Intent(this,ProfileActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Giriş başarısız
                        Toast.makeText(applicationContext, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            // Eksik bilgi varsa kullanıcıya uyarı ver
            Toast.makeText(applicationContext, "Please enter email and password", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getUserInfoAndNavigateToProfile(userId: String?) {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")

        userId?.let {
            usersCollection.document(it).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userType = document.getString("userType")
                        val intent = Intent(this, ProfileActivity::class.java)
                        intent.putExtra("userType", userType)
                        startActivity(intent)
                        finish()
                    } else {
                        // Firestore'da kullanıcı bilgisi bulunamadı
                        Toast.makeText(applicationContext, "User information not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    // Firestore'a erişim hatası
                    Log.w(TAG, "Error getting user information", exception)
                    Toast.makeText(applicationContext, "Error getting user information", Toast.LENGTH_SHORT).show()
                }
        }
    }
    fun goToRegisterTv(view: View) {
        var intent = Intent(this, UserRegisterActivity::class.java)
        startActivity(intent)
    }
}