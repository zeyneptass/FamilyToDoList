package com.example.familytodolistapp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.familytodolistapp.databinding.ActivityFamilyLoginBinding
import com.google.firebase.firestore.FirebaseFirestore

class FamilyLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFamilyLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_family_login)

        binding = ActivityFamilyLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun goToRegisterTv(view: View) {
        var intent = Intent(this, FamilyRegisterActivity::class.java)
        startActivity(intent)
    }
    fun LoginForFamilyAccount(view: View) {
        val familyName = binding.familyNameText.text.toString()
        val password = binding.familyPasswordText.text.toString()

        if (familyName.isNotEmpty() && password.isNotEmpty()) {
            val db = FirebaseFirestore.getInstance()
            val familyCollection = db.collection("families")

            familyCollection
                .whereEqualTo("familyName", familyName)
                .whereEqualTo("familyPassword", password)
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        // Kullanıcı giriş başarılı
                        Toast.makeText(this, "The family account has been successfully logged in.", Toast.LENGTH_SHORT).show()
                        var intent = Intent(this,AddFamilyUserActivity::class.java)
                        startActivity(intent)

                        // Giriş başarılıysa, istediğiniz ek işlemleri burada yapabilirsiniz.
                    } else {
                        // Kullanıcı adı veya şifre eşleşmedi
                        Toast.makeText(this, "Invalid familyName or password", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents", exception)
                    // Hata durumunda kullanıcıya geri bildirim vermek için gerekli işlemleri yapabilirsiniz.
                    Toast.makeText(this, "Error getting family data", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please enter familyName and password", Toast.LENGTH_SHORT).show()
        }
    }

}