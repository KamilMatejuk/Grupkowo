package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.ServerConnection.UserRequests
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity(), ServerLisener {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        println("Token ${Server.getToken(this)}")
        UserRequests.getCurrentUser(1, this)

    }

    fun logout(view: View) {
        Server.deleteToken(this)
        // get back to login activity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onResponseArrived(requestId: Int, error: String?, response: JSONObject?) {
        if (requestId == 1) {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                Log.d(RegisterActivity.TAG, "Couldn't register user in out server: $error")
            } else {
                binding.id.text =       "id: ${response?.getString("user_id") ?: ""}"
                binding.username.text = "username: ${response?.getString("username") ?: ""}"
                binding.email.text =    "email: ${response?.getString("email") ?: ""}"
                binding.avatar.text =   "avatar: ${response?.getString("avatar") ?: "null"}"
            }
        }
    }
}