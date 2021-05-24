package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.RecyclerAdapters.PostAdapter
import kotlinx.android.synthetic.main.activity_wall.*
import org.json.JSONObject

class WallActivity : AppCompatActivity(), ServerLisener {
    companion object {
        const val TAG: String = "WallActivity"
    }

    private var titleList = mutableListOf<String>()
    private var detailList = mutableListOf<String>()
    private var imageList = mutableListOf<Int>()
    private var usernames = mutableListOf<String>()
    private var comments = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wall)

        messenger_button.setOnClickListener {
            val intent = Intent(this, MessagesActivity::class.java)
            startActivity(intent)
        }

        make_post_button.setOnClickListener {
            val intent = Intent(this, PostCreationActivity::class.java)
            startActivity(intent)
        }

        //recyclerView adapter

        postToList()
        addComments("bla", "andjghajkdhgjasdhgjahsfg")
        addComments("bla2", "cndjghaasfsdhgjahsfg")
        addComments("bla3", "vnddfhdfhaaaasdhgjahsfg")
//        Log.d("b",usernames[0])
//        Log.d("b",comments[0])
//        Log.d("b",usernames[1])
//        Log.d("b",comments[1])
//        Log.d("b",usernames[2])
//        Log.d("b",comments[2])
        postsList.adapter = PostAdapter(this, titleList, detailList, imageList, usernames, comments)
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

    private fun addComments(username: String, comment: String){
        usernames.add(username)
        comments.add(comment)
    }

    private fun postToList(){
        for (i in 1..20) {
            addToList("Title $i", "\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\"", R.mipmap.ic_launcher)
        }
    }
}