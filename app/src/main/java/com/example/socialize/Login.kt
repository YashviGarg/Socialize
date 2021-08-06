package com.example.socialize

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

class Login : AppCompatActivity() {
    var TAG="LoginActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun loginOnClick(view:View){

    }
    fun registerOnClick(view:View){
        Log.d(TAG, "registerOnClick: Clicked Register")
        val intent = Intent(this,Register::class.java)
        startActivity(intent)
    }
}