package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.RecyclerAdapters.CommentAdapter
import com.example.myapplication.RecyclerAdapters.PostAdapter
import com.example.myapplication.ServerConnection.CommentRequests
import com.example.myapplication.ServerConnection.PostRequests
import com.example.myapplication.databinding.ActivityCommentsBinding
import com.example.myapplication.databinding.ActivityGroupsBinding
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.activity_wall.*
import org.json.JSONArray
import org.json.JSONObject

class CommentActivity:AppCompatActivity() {
    private lateinit var binding: ActivityCommentsBinding
    var groupId: Int = 0
    var postId: Int = 0
    var author: String = ""
    var text: String = ""
    private var usernameList = mutableListOf<String>()
    private var commentList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val commentAdapter = CommentAdapter(applicationContext, usernameList, commentList)
        //Log.d("sizes",usernameList.size.toString())
        //Log.d("sizes",commentList.size.toString())
        commentsList.layoutManager = LinearLayoutManager(applicationContext)
        commentsList.adapter = commentAdapter
        commentsList.setHasFixedSize(false)

        wall_button.setOnClickListener {
            finish()
        }

        make_comment_button.setOnClickListener {
            CommentRequests.addComment(this, groupId,postId, binding.newCommentText.text.toString(),
                functionCorrect = {
                    run {
                        Log.d("OK", "DODANO KOMENTARZ")
                        CommentRequests.getComments(applicationContext, groupId, postId,
                            functionCorrect = { response ->
                                run {
                                    // your code here if successful
                                    comments(response,commentAdapter)

                                }
                            },
                            functionError = { errorMessage ->
                                run {
                                    Log.d("blad", errorMessage)
                                }
                            })
                    }
                },
                functionError = {
                    run {
                        Log.d("ERROR", "BŁĄD PRZY DODAWANIU KOMENTARZA")
                    }
                }
            )
            binding.newCommentText.setText("")

        }

        groupId = intent.getIntExtra("groupId", 0)
        postId = intent.getIntExtra("postId", 0)

        CommentRequests.getComments(applicationContext, groupId, postId,
            functionCorrect = { response ->
                run {
                    // your code here if successful
                    comments(response,commentAdapter)

                }
            },
            functionError = { errorMessage ->
                run {
                    Log.d("blad", errorMessage)
                }
            })


    }


    fun comments(response: JSONObject, commentAdapter: CommentAdapter){

        var jsonarray: JSONArray
        var jobject: JSONObject
        var jsonobject: JSONObject = JSONObject(response.toString())
        jsonarray = jsonobject.getJSONArray("comments")
        usernameList.clear()
        commentList.clear()

        for (i in 0 until jsonarray.length()) {
            jobject = jsonarray.getJSONObject(i)
            author = jobject.getString("author_username")
            text = jobject.getString("text")
            usernameList.add(author)
            commentList.add(text)

        }
        commentAdapter.notifyDataSetChanged()





        //postsList.adapter = PostAdapter(this, titleList, detailList, imageList, idList, groupId)
        //postsList.layoutManager = LinearLayoutManager(this)
        //postsList.setHasFixedSize(false)



    }


}