package com.example.myapplication

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.RecyclerAdapters.MessageAdapter
import com.example.myapplication.RecyclerItems.Message
import com.example.myapplication.ServerConnection.ChatRequests
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.GsonBuilder

class MessagesActivity : AppCompatActivity() {
    var groupId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messeges)
        groupId = intent.getIntExtra("groupId", 0)

        // move to wall activity
        findViewById<FloatingActionButton>(R.id.wall_button).setOnClickListener {
            finish()
        }

        // send a new message
        findViewById<FloatingActionButton>(R.id.make_post_button).setOnClickListener {
            sendMessage()
        }

        // detect end of recycler and reload
        findViewById<RecyclerView>(R.id.postsList).addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    getMessages()
                }
            }
        })

        getMessages()
    }

    private fun getMessages() {
        ChatRequests.getMessages(applicationContext, groupId,
            functionCorrect = { response ->
                run {
                    // convert into a list of objects
                    val list = response.get("messages").toString()
                    val objects: List<Message> = GsonBuilder().create().fromJson(list, Array<Message>::class.java).toList()
                    // set recycler
                    val recycler = findViewById<RecyclerView>(R.id.postsList)
                    recycler.adapter = MessageAdapter(this, groupId, objects)
                    recycler.layoutManager = LinearLayoutManager(this)
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
        val messText = text.text.toString().trim()
        if (messText.isNotEmpty()) {
            ChatRequests.sendMessage(applicationContext, groupId, messText,
                functionCorrect = {
                    run {
                        text.text = ""
                        getMessages()
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