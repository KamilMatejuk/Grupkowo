package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.RecyclerAdapters.PostAdapter
import com.example.myapplication.RecyclerItems.Post
import com.example.myapplication.ServerConnection.PostRequests.getPosts
import com.example.myapplication.ServerConnection.PostRequests.addPost
import com.example.myapplication.ServerConnection.GroupRequests.addUserToGroup
import com.example.myapplication.ServerConnection.UserRequests.createGroup
import kotlinx.android.synthetic.main.activity_wall.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import kotlinx.serialization.decodeFromString
import org.json.JSONArray
import java.util.*
import java.util.Collections.synchronizedList


class WallActivity : AppCompatActivity() {
    companion object {
        const val TAG: String = "WallActivity"
    }

    private var titleList = mutableListOf<String>()
    private var titleList2 = synchronizedList(titleList)
    private var detailList = mutableListOf<String>()
    private var detailList2 = synchronizedList(detailList)
    private var imageList = mutableListOf<Int>()
    private var imageList2 = synchronizedList(imageList)
    private var usernames = mutableListOf<String>()
    private var comments = mutableListOf<String>()
    private var idList = mutableListOf<String>()

    private var title: String = ""
    private var detail: String = ""
    private var id: String = ""

    var groupId: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wall)
        groupId = intent.getIntExtra("groupId", 0)

        messenger_button.setOnClickListener {
            val intent = Intent(this, MessagesActivity::class.java)
            startActivity(intent)
        }

        make_post_button.setOnClickListener {
            val intent = Intent(this, PostCreationActivity::class.java)
            intent.putExtra("groupId", groupId)
            startActivity(intent)
        }

        //recyclerView adapter

       /* addPost(applicationContext,14,"PIERWSZY POST W MOJEJ GRUPIE",
            functionCorrect = { response ->
                run {
                    // your code here if successful
                    //Log.d("abbsbsbsbs", response.toString())
                    //val data = Json.decodeFromString<Post>(response.toString())
                    //Log.d("abbsbsbsbasasass", data.toString())
                    Log.d("super", "dodano post")

                }
            },
            functionError = { errorMessage ->
                run {
                    Log.d("bladdodania", errorMessage)
                }
            })


        */


        // NA SZTYWNO WPISANY GROUPID 14 ZEBY MOC PRZETESTOWAC CZY DZIALA POBIERANIE POSTOW
        // WYSTARCZY ZALOGOWAC SIE: LOGIN: damian@gmail.com  HASLO: damian
        // I WEJSC W DODANIE GRUPY POKI NIE MOZNA WYBRAC GRUP DO KTORYCH SIE NALEZY

//        val groupId = intent.getIntExtra("groupId", 0)
        getPosts(applicationContext, groupId,"","",
            functionCorrect = { response ->
                run {
                    // your code here if successful

                    test(response)



                }
            },
            functionError = { errorMessage ->
                run {
                    Log.d("blad", errorMessage)
                }
            })




    }

    fun test(response: JSONObject){
        var jsonarray: JSONArray
        var jobject: JSONObject
        var jsonobject: JSONObject = JSONObject(response.toString())
        jsonarray = jsonobject.getJSONArray("posts")

        for (i in 0 until jsonarray.length()) {
            jobject = jsonarray.getJSONObject(i)
            title = jobject.getString("author_username")
            detail = jobject.getString("text")
            id = jobject.getString("author_id")
            idList.add(id)
            addToList(title,detail,R.mipmap.ic_launcher)

        }

        //postToList()
        addComments("bla", "andjghajkdhgjasdhgjahsfg")
        addComments("bla2", "cndjghaasfsdhgjahsfg")
        addComments("bla3", "vnddfhdfhaaaasdhgjahsfg")

        postsList.adapter = PostAdapter(this, titleList2, detailList2, imageList2, usernames, comments)
        postsList.layoutManager = LinearLayoutManager(this)
        postsList.setHasFixedSize(false)

    }

    private fun addToList(title: String, detail: String, image: Int ){
        titleList2.add(title)
        detailList2.add(detail)
        imageList2.add(image)
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