package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.ServerConnection.UserRequests
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity(), ServerLisener {
    companion object {
        const val TAG: String = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button.setOnClickListener {
            performLogin()
        }

        back_to_register_textview.setOnClickListener {
            Log.d(RegisterActivity.TAG, "Try to show register activity")
            // launch the login activity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun performLogin() {
        val email = email_edittext_login.text.toString()
        val password = password_edittext_login.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(RegisterActivity.TAG, "Attempting log in user with email: $email")

        // Firebase Authentication to create a user with email and password
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    return@addOnCompleteListener
                }
                Log.d(RegisterActivity.TAG, "Successfully logged in user in firebase")
                // connect to our server
                UserRequests.login(email, password, 1, this)
            }
            .addOnFailureListener {
                Log.d(RegisterActivity.TAG, "Failed to log in user: ${it.message}")
                Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResponseArrived(requestId: Int, error: String?, response: JSONObject?) {
        if (requestId == 1) {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                Log.d(RegisterActivity.TAG, "Couldn't register user in out server: $error")
            } else {
                // save token
                val token = response?.getString("access_token") ?: return
                Server.saveToken(this, token)

                // log status
                Log.d(RegisterActivity.TAG, "Successfully registered user in out server")

                // open main activity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        Server.cancelRequests()
    }

}