package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.RecyclerItems.Group
import com.example.myapplication.ServerConnection.UserRequests
import com.example.myapplication.databinding.ActivityGroupsBinding
import kotlinx.android.synthetic.main.activity_groups.*
import kotlin.reflect.typeOf

class GroupsActivity: AppCompatActivity() {
    private lateinit var binding: ActivityGroupsBinding
    private lateinit var groups: List<Group>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


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
        val groupData = UserRequests.getUserGroupsMember(applicationContext,
            functionCorrect = { response ->
                run {
                    Toast.makeText(
                        this,
                        "Successfully got groups",
                        Toast.LENGTH_LONG
                    ).show()
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

//            for(item in groupInfo)
    }
}