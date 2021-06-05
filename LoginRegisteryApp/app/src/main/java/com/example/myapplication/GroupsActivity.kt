package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.RecyclerAdapters.GroupAdapter
import com.example.myapplication.RecyclerItems.Group
import com.example.myapplication.RecyclerItems.Post
import com.example.myapplication.ServerConnection.UserRequests
import com.example.myapplication.databinding.ActivityGroupsBinding
import kotlinx.android.synthetic.main.activity_groups.*
import kotlinx.android.synthetic.main.activity_wall.*
//import kotlinx.serialization.decodeFromString
//import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.reflect.typeOf

class GroupsActivity: AppCompatActivity() {
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
        groups = mutableListOf()
        UserRequests.getUserGroupsMember(applicationContext,
            functionCorrect = { response ->
                run {
                    Toast.makeText(
                        this,
                        "Successfully got groups",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d(GroupsActivity::class.java.name, "MAKER\n\n\n\n")
                    Log.d(GroupsActivity::class.java.name, "DATA: ${response.toString()}")

                    jsonToObjects(response)

                    UserRequests.getUserGroupsAdmin(applicationContext,
                        functionCorrect = { response2 ->
                            run {
                                Toast.makeText(
                                    this,
                                    "Successfully got groups",
                                    Toast.LENGTH_LONG
                                ).show()
                                Log.d(GroupsActivity::class.java.name, "MAKER\n\n\n\n")
                                Log.d(GroupsActivity::class.java.name, "DATA: ${response2.toString()}")

                                jsonToObjects(response2)

                                binding.groupList.adapter = GroupAdapter(this, groups)
                                binding.groupList.layoutManager = LinearLayoutManager(this)
                                binding.groupList.setHasFixedSize(false)
                            }
                        },
                        functionError = { errorMessage ->
                            run {
                                Toast.makeText(
                                    this,
                                    "Couldn't get groups",
                                    Toast.LENGTH_LONG
                                ).show()
                                Log.d(
                                    GroupsActivity::class.java.name,
                                    "Couldn't get groups: $errorMessage"
                                )
                            }
                        })
                }
            },
            functionError = { errorMessage ->
                run {
                    Toast.makeText(
                        this,
                        "Couldn't get groups",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d(
                        GroupsActivity::class.java.name,
                        "Couldn't get groups: $errorMessage"
                    )
                }
            })
    }

    fun jsonToObjects(response: JSONObject) {
        val jsonarray: JSONArray
        var jobject: JSONObject
        val jsonobject: JSONObject = JSONObject(response.toString())
        jsonarray = jsonobject.getJSONArray("groups")

        for (i in 0 until jsonarray.length()) {
            jobject = jsonarray.getJSONObject(i)
            val name = jobject.getString("name")
            val id = jobject.getString("group_id").toInt()
            val photo = R.mipmap.ic_launcher

            groups.add(Group(id = id, photo = photo, name = name))
        }

    }
}