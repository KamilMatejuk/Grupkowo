package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(private var titles: List<String>, private var details: List<String>,
                  private var images: List<Int>):
                  RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    // deleting recyclerview comments from constructor for now
    //   , private var comments: RecyclerView


   inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

       val postTitle: TextView = itemView.findViewById(R.id.textView)
       val postDetails: TextView = itemView.findViewById(R.id.textView3)
       val postImage: ImageView = itemView.findViewById(R.id.imageView)
       var postComments: RecyclerView = itemView.findViewById(R.id.commentRecycler)

       init{
           itemView.setOnClickListener { v: View ->
               val position: Int = adapterPosition
               Toast.makeText(
                   itemView.context,
                   "You clicked on item: ${position + 1}",
                   Toast.LENGTH_SHORT
               ).show()
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
        //holder.postComments = comments
    }


}