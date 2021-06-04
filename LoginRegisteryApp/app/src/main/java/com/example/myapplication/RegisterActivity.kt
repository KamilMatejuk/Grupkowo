package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.ServerConnection.UserRequests
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONObject
import java.util.*

class RegisterActivity : AppCompatActivity() {
    companion object {
        const val TAG: String = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // set up server connection
        Server.setUp(this, this)

        // check if logged in
        if (Server.getToken(this) != null) {
            Log.d(TAG, "User logged in, open main activity")
            // launch the main activity
            val intent = Intent(this, GroupsActivity::class.java)
            startActivity(intent)
            finish()
        }

        register_button_register.setOnClickListener {
            performRegister()
        }

        already_have_account_text_view.setOnClickListener {
            Log.d(TAG, "Try to show login activity")
            // launch the login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        testButton.setOnClickListener {
            val intent = Intent(this, GroupsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun performRegister() {
        val username = username_edittext_register.text.toString()
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "Attempting to register user with email: $email")

        // Firebase Authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    return@addOnCompleteListener
                }
                // save to firebase
                saveUserToFirebaseDatabase(it.toString())
                Log.d(
                    TAG,
                    "Successfully registered user in firebase (uid: ${it.result?.user?.uid})"
                )
                // connect do our server
                UserRequests.register(username, email, password, applicationContext,
                    functionCorrect = { response ->
                        run {
                            // save token
                            val token = response.getString("access_token")
                            Server.saveToken(this, token)

                            // log status
                            Log.d(TAG, "Successfully registered user in out server")

                            // open main activity
                            val intent = Intent(this, GroupsActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    },
                    functionError = { errorMessage ->
                        run {
                            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                            Log.d(TAG, "Couldn't register user in out server: $errorMessage")
                        }
                    })
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to register user: ${it.message}")
                Toast.makeText(this, "Failed to register user: ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username_edittext_register.text.toString())

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Finally we saved the user to Firebase Database")
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to set value to database: ${it.message}")
            }
    }

    override fun onStop() {
        super.onStop()
        Server.cancelRequests()
    }
}

class User(val uid: String, val username: String)