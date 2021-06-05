package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.ServerConnection.UserRequests
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        println("Token ${Server.getToken(this)}")
        UserRequests.getCurrentUser(applicationContext,
            functionCorrect = { response ->
                run {
                    binding.id.text = "id: ${response?.getString("user_id") ?: ""}"
                    binding.username.text = "username: ${response?.getString("username") ?: ""}"
                    binding.email.text = "email: ${response?.getString("email") ?: ""}"
                    binding.avatar.text = "avatar: ${response?.getString("avatar") ?: "null"}"
                }
            },
            functionError = { errorMessage ->
                run {
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    Log.d(
                        RegisterActivity.TAG,
                        "Couldn't register user in out server: $errorMessage"
                    )
                }
            })

        Thread {
            DataIO.initializeDatabase(this)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }.start()
    }

    fun logout(view: View) {
        Server.deleteToken(this)
        // get back to login activity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}