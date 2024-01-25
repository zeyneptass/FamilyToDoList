package com.example.familytodolistapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.familytodolistapp.databinding.ActivityFamilyRegisterBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class FamilyRegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFamilyRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFamilyRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }

    fun goToLogin(view: View) {
        val intent = Intent(this, FamilyLoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun addFamilyAccount(view: View) {
        val familyName = binding.familyNameText.text.toString()
        val password = binding.familyPasswordText.text.toString()
        val passwordAgain = binding.familyPasswordAgainText.text.toString()

        if (password == passwordAgain) {
            val db = FirebaseFirestore.getInstance()
            val familyCollection = db.collection("families")

            val userData = mapOf(
                "familyName" to familyName,
                "familyPassword" to password
            )
            // ...

// familyName'i kullanarak sorgu yap
            familyCollection.whereEqualTo("familyName", familyName)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        // familyName kullanılmıyorsa, aynı işlemleri devam ettir
                        // ...

                        // Veriyi Firestore'a ekle
                        familyCollection.add(userData)
                            .addOnSuccessListener { documentReference ->
                                Log.d(
                                    TAG,
                                    "DocumentSnapshot added with ID: ${documentReference.id}"
                                )
                                // Burada başarıyla eklendiğini işaretlemek veya kullanıcıya geri bildirim vermek için gerekli işlemleri yapabilirsiniz.
                                Toast.makeText(
                                    this,
                                    "Family account created successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(this, AddFamilyUserActivity::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                                // Hata durumunda kullanıcıya geri bildirim vermek için gerekli işlemleri yapabilirsiniz.
                                Toast.makeText(
                                    this,
                                    "Error creating family account",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        // familyName kullanılıyorsa, kullanıcıya uygun bir isim seçmesini söyle veya başka bir işlem yap
                        Toast.makeText(
                            this,
                            "This family name is already taken. Please choose another one.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                    // Hata durumunda kullanıcıya geri bildirim vermek için gerekli işlemleri yapabilirsiniz.
                    Toast.makeText(
                        this,
                        "Error checking family name availability",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

    }
}