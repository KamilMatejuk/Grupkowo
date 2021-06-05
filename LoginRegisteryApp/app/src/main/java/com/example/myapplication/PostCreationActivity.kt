package com.example.myapplication

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.post_creation.*
import com.example.myapplication.ServerConnection.PostRequests.addPost

class PostCreationActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_creation)

        val text = findViewById<TextView>(R.id.postText)

        okButton.setOnClickListener {
            makePost(text.text.toString())
            finish()
        }
    }

    fun makePost(postText: String) {
        val groupId = intent.getIntExtra("groupId", 0)
        Toast.makeText(this, groupId.toString(), Toast.LENGTH_LONG).show()

        addPost(this, groupId, postText,
            functionCorrect = {
                run {
                    Log.d("OK", "DODANO POSTA")
                }
            },
            functionError = {
                run {
                    Log.d("ERROR", "BŁĄD PRZY DODAWANIU POSTA")
                }
            }
        )
    }
}