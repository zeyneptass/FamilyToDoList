package com.example.familytodolistapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioGroup

class AddFamilyUserActivity : AppCompatActivity() {
    private lateinit var selectedUserType: String // Seçilen kullanıcı türünü saklamak için değişken
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_family_user)

        val radioGroup: RadioGroup = findViewById(R.id.radioGroup)
        val btnSelectUserType: Button = findViewById(R.id.btnSelectUserType)

        // RadioGroup'ta bir RadioButton seçildiğinde çağrılacak olan listener
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioFather -> selectedUserType = "Father"
                R.id.radioMother -> selectedUserType = "Mother"
                R.id.radioChild -> selectedUserType = "Child"
            }
            // Butonun etkinlik durumunu güncelle
            btnSelectUserType.isEnabled = true
            val intent = Intent(this, UserRegisterActivity::class.java)
            intent.putExtra("userType", selectedUserType)
            startActivity(intent)
        }
        // Butona tıklanınca çalışacak olan listener
        btnSelectUserType.setOnClickListener {
            // Seçilen userType'ı bir sonraki sayfaya taşıyarak yönlendirme yapabilirsiniz
            val intent = Intent(this, UserRegisterActivity::class.java)
            intent.putExtra("userType", selectedUserType)
            startActivity(intent)
        }
    }
}