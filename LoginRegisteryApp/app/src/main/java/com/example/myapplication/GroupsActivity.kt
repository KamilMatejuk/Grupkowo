package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.RecyclerAdapters.GroupAdapter
import com.example.myapplication.RecyclerAdapters.MessageAdapter
import com.example.myapplication.RecyclerItems.Group
import com.example.myapplication.RecyclerItems.Message
import com.example.myapplication.RecyclerItems.Post
import com.example.myapplication.ServerConnection.UserRequests
import com.example.myapplication.databinding.ActivityGroupsBinding
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_groups.*
import kotlinx.android.synthetic.main.activity_wall.*
//import kotlinx.serialization.decodeFromString
//import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.reflect.typeOf

class GroupsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroupsBinding
    private lateinit var groups: MutableList<Group>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        generateGroupRecycler()
    }

    fun goToAccount(view: View) {
        val intent = Intent(this, AccountActivity::class.java)
        startActivity(intent)
    }

    fun goToCreateGroup(view: View) {
        val intent = Intent(this, CreateGroupActivity::class.java)
        startActivity(intent)
    }

    fun generateGroupRecycler() {
        loadGroupsAdmin()
        // detect end of recycler and reload
        findViewById<RecyclerView>(R.id.groupListAdmin).addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    loadGroupsAdmin()
                }
            }
        })

        loadGroupsMember()
        // detect end of recycler and reload
        findViewById<RecyclerView>(R.id.groupListMember).addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    loadGroupsMember()
                }
            }
        })
    }

    fun loadGroupsAdmin() {
        UserRequests.getUserGroupsAdmin(applicationContext,
            functionCorrect = { response ->
                run {
                    // convert into a list of objects
                    val list = response.get("groups").toString()
                    val objects: List<Group> =
                        GsonBuilder().create().fromJson(list, Array<Group>::class.java).toList()
                    // set recycler
                    val recycler = findViewById<RecyclerView>(R.id.groupListAdmin)
                    recycler.adapter = GroupAdapter(this, objects, true)
                    recycler.layoutManager = LinearLayoutManager(this)
                }
            },
            functionError = { errorMessage ->
                run {
                    Log.d(
                        GroupsActivity::class.java.name,
                        "Couldn't get groups: $errorMessage"
                    )
                }
            })
    }

    fun loadGroupsMember() {
        UserRequests.getUserGroupsMember(applicationContext,
            functionCorrect = { response ->
                run {
                    // convert into a list of objects
                    val list = response.get("groups").toString()
                    val objects: List<Group> =
                        GsonBuilder().create().fromJson(list, Array<Group>::class.java).toList()
                    // set recycler
                    val recycler = findViewById<RecyclerView>(R.id.groupListMember)
                    recycler.adapter = GroupAdapter(this, objects, false)
                    recycler.layoutManager = LinearLayoutManager(this)
                }
            },
            functionError = { errorMessage ->
                run {
                    Log.d(
                        GroupsActivity::class.java.name,
                        "Couldn't get groups: $errorMessage"
                    )
                }
            })
    }
}