package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.RecyclerAdapters.PostAdapter
import com.example.myapplication.RecyclerItems.Message
import com.example.myapplication.RecyclerItems.Post
import com.example.myapplication.ServerConnection.GroupRequests
import com.example.myapplication.ServerConnection.PostRequests.getPosts
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_wall.*
//import kotlinx.serialization.decodeFromString
//import kotlinx.serialization.json.Json
import org.json.JSONObject
//import kotlinx.serialization.decodeFromString
import org.json.JSONArray


class WallActivity : AppCompatActivity() {
    companion object {
        const val TAG: String = "WallActivity"
    }


    var groupId: Int = 0
    var admin: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wall)
        groupId = intent.getIntExtra("groupId", 0)
        admin = intent.getBooleanExtra("admin", false)

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            println("HEEERERREREREERRE")
            reloadPosts()
        }

        postsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    reloadPosts()
                }
            }
        })

        // move to chat
        messenger_button.setOnClickListener {
            val intent = Intent(this, MessagesActivity::class.java)
            intent.putExtra("groupId", groupId)
            startActivity(intent)
        }

        // add post
        make_post_button.setOnClickListener {
            val intent = Intent(this, PostCreationActivity::class.java)
            intent.putExtra("groupId", groupId)
            resultLauncher.launch(intent)
        }

        if (admin) {
            // add / delete users
            users.setOnClickListener {
                val intent = Intent(this, AddUsersActivity::class.java)
                intent.putExtra("groupId", groupId)
                startActivity(intent)
            }

            // delete group
            delete.setOnClickListener {
                GroupRequests.deleteGroup(applicationContext, groupId,
                    functionCorrect = {
                        run {
                            Toast.makeText(this, "Usunięto grupę", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }, functionError = {
                        run {
                            Toast.makeText(this, "Nie można usunąć grupy", Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
            }
        } else {
            users.visibility = View.GONE
            users.isClickable = false
            delete.visibility = View.GONE
            delete.isClickable = false
        }
        reloadPosts()
    }

    private fun reloadPosts() {
        getPosts(applicationContext, groupId,
            functionCorrect = { response ->
                run {
                    // your code here if successful
                    val list = response.get("posts").toString()
                    val objects: List<Post> = GsonBuilder().create().fromJson(list, Array<Post>::class.java).toList()
                    postsList.adapter = PostAdapter(this, groupId, objects)
                    postsList.layoutManager = LinearLayoutManager(this)
                    postsList.setHasFixedSize(false)
                }
            },
            functionError = { errorMessage ->
                run {
                    Log.d("blad", errorMessage)
                }
            })

    }
}