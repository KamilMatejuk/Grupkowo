package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ServerConnection.ChatRequests

class MessagesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messeges)

        // TODO get group id

        // move to wall activity
        findViewById<RecyclerView>(R.id.wall_button).setOnClickListener {
            val intent = Intent(this, WallActivity::class.java)
            startActivity(intent)
            finish()
        }

        // send a new message
        findViewById<RecyclerView>(R.id.wall_button).setOnClickListener {
            sendMessage()
        }

        getMessages()
    }

    private fun getMessages() {
        ChatRequests.getMessages(applicationContext, 0,
            functionCorrect = { response ->
                run {
                    println(response)
                }
            },
            functionError = {
                run {
                    Toast.makeText(this, "Couldn't load messages", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun sendMessage() {
        val text = findViewById<TextView>(R.id.new_message_text)
        if (text.text.trim().isNotEmpty()) {
            ChatRequests.sendMessage(applicationContext, 0, text.text.trim() as String,
                functionCorrect = {
                    run {
                        text.text = ""
                    }
                },
                functionError = {
                    run {
                        Toast.makeText(this, "Couldn't send message", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

}