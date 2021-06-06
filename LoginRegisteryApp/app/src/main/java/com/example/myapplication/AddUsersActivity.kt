package com.example.myapplication

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.RecyclerAdapters.UsersAdapter
import com.example.myapplication.RecyclerItems.User
import com.example.myapplication.ServerConnection.GroupRequests
import com.example.myapplication.ServerConnection.UserRequests
import com.google.gson.GsonBuilder

class AddUsersActivity : AppCompatActivity() {
    private var groupId: Int = 0
    private lateinit var adapter: UsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_users)

        var usersAll: ArrayList<User>
        var usersBelonging: ArrayList<User>

        groupId = intent.getIntExtra("groupId", 0)
        GroupRequests.getGroupUsers(applicationContext, groupId,
            functionCorrect = { response ->
                run {
                    // convert into a list of objects
                    var list = response.get("users").toString()
                    var userList = GsonBuilder().create().fromJson(list, Array<User>::class.java).toList()
                    usersBelonging = if (userList.isNotEmpty()) {
                        userList as ArrayList<User>
                    } else {
                        arrayListOf()
                    }

                    UserRequests.getAllUsers(applicationContext,
                        functionCorrect = { response ->
                            run {
                                // convert into a list of objects
                                list = response.get("users").toString()
                                userList = GsonBuilder().create().fromJson(list, Array<User>::class.java).toList()
                                usersAll = if (userList.isNotEmpty()) {
                                    userList as ArrayList<User>
                                } else {
                                    arrayListOf()
                                }

                                val recycler = findViewById<RecyclerView>(R.id.usersList)
                                recycler.layoutManager = LinearLayoutManager(this)
                                adapter = UsersAdapter(this, usersAll, usersBelonging)
                                recycler.adapter = adapter
                            }
                        },
                        functionError = {})
                }
            },
            functionError = {})
    }

    fun save(view: View) {
        for (user in adapter.userToAdd) {
            GroupRequests.addUserToGroup(applicationContext, groupId, user, functionCorrect = {}, functionError = {})
        }
        for (user in adapter.userToDelete) {
            GroupRequests.removeUserFromGroup(applicationContext, groupId, user, functionCorrect = {}, functionError = {})
        }
        finish()
    }
}