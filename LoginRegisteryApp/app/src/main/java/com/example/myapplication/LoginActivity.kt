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

class LoginActivity : AppCompatActivity() {
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
        UserRequests.login(email, password, applicationContext,
            functionCorrect = { response ->
                run {
                    // save token
                    val token = response.getString("access_token")
                    Server.saveToken(this, token)
                    // log status
                    Log.d(
                        RegisterActivity.TAG,
                        "Successfully registered user in out server"
                    )
                    // open main activity
                    val intent = Intent(this, GroupsActivity::class.java)
                    startActivity(intent)
                    finish()
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
    }

    override fun onStop() {
        super.onStop()
        Server.cancelRequests()
    }

}