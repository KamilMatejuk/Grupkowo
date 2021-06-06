package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.myapplication.Notifications.DownloadWorker
import com.example.myapplication.RecyclerAdapters.GroupAdapter
import com.example.myapplication.RecyclerItems.Group
import com.example.myapplication.ServerConnection.UserRequests
import com.example.myapplication.databinding.ActivityGroupsBinding
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_groups.*
import kotlinx.android.synthetic.main.activity_wall.*
//import kotlinx.serialization.decodeFromString
//import kotlinx.serialization.json.Json
import java.util.*

class GroupsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroupsBinding
    private lateinit var groups: MutableList<Group>
    private lateinit var resultLauncher: ActivityResultLauncher<Intent?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            loadGroupsAdmin()
            loadGroupsMember()
        }

        generateGroupRecycler()

        // start checking for updates
        val data = Data.Builder()
        val compressionWork = OneTimeWorkRequest
            .Builder(DownloadWorker::class.java)
            .setInputData(data.build())
            .build()
        WorkManager.getInstance().enqueue(compressionWork)
    }

    fun goToAccount(view: View) {
        val intent = Intent(this, AccountActivity::class.java)
        resultLauncher.launch(intent)
    }

    fun goToCreateGroup(view: View) {
        val intent = Intent(this, CreateGroupActivity::class.java)
        resultLauncher.launch(intent)
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
                    recycler.adapter = GroupAdapter(this, objects, true, resultLauncher)
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
                    recycler.adapter = GroupAdapter(this, objects, false, resultLauncher)
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