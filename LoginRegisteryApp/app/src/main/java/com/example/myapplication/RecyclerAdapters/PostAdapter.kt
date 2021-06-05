package com.example.myapplication.RecyclerAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import kotlinx.android.synthetic.main.sub_post.view.*

class PostAdapter(private var context: Context, private var titles: List<String>, private var details: List<String>,
                  private var images: List<Int>, private var usernames: List<String>, private var comments: List<String>):
                  RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    // deleting recyclerview comments from constructor for now
    //   , private var comments: RecyclerView


   inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

       var postTitle: TextView = itemView.findViewById(R.id.textView)
       var postDetails: TextView = itemView.findViewById(R.id.textView3)
       var postImage: ImageView = itemView.findViewById(R.id.imageView)
       var postComments: RecyclerView = itemView.findViewById(R.id.commentRecycler)

       init{

          // postTitle = itemView.findViewById(R.id.textView)
          // val postDetails: TextView = itemView.findViewById(R.id.textView3)
          // val postImage: ImageView = itemView.findViewById(R.id.imageView)
          // var postComments: RecyclerView = itemView.findViewById(R.id.commentRecycler)


           itemView.setOnClickListener { v: View ->
               val position: Int = adapterPosition
               Toast.makeText(
                   itemView.context,
                   "You clicked on item: ${position + 1}",
                   Toast.LENGTH_SHORT
               ).show()
           }

           itemView.likeButton.setOnClickListener {

           }


       }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.sub_post, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return titles.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.postTitle.text = titles[position]
        holder.postDetails.text = details[position]
        holder.postImage.setImageResource(images[position])
        setCommentRecycler(holder.postComments, usernames, comments)
    }

    private fun setCommentRecycler(commentRecyclerView: RecyclerView, usernames: List<String>, comments: List<String>){

        val commentAdapter = CommentAdapter(context, usernames, comments)
        commentRecyclerView.layoutManager = LinearLayoutManager(context)
        commentRecyclerView.adapter = commentAdapter
        commentRecyclerView.setHasFixedSize(false)

    }


}