package com.example.familytodolistapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class FamilyRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_family_register)
    }
    fun goToLogin(view: View) {
        var intent = Intent(this, FamilyRegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
}