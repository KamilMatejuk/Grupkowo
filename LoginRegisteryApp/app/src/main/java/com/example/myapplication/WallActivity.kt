package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.ServerConnection.UserRequests
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_wall.*
import org.json.JSONObject

class WallActivity : AppCompatActivity(), ServerLisener {
    companion object {
        const val TAG: String = "WallActivity"
    }

    private var titleList = mutableListOf<String>()
    private var detailList = mutableListOf<String>()
    private var imageList = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wall)

        //recyclerView adapter

        postToList()
        postsList.adapter = PostAdapter(titleList, detailList, imageList)
        postsList.layoutManager = LinearLayoutManager(this)
        postsList.setHasFixedSize(false)
    }



    override fun onResponseArrived(requestId: Int, error: String?, response: JSONObject?) {
        finish()
    }

    private fun addToList(title: String, detail: String, image: Int ){

        titleList.add(title)
        detailList.add(detail)
        imageList.add(image)

    }

    private fun postToList(){

        for (i in 1..20) {
            addToList("Title $i", "Text $i", R.mipmap.ic_launcher)
        }

    }
}