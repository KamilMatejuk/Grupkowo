package com.example.myapplication.RecyclerAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class CommentAdapter(private var context: Context, private var usernames: List<String>, private var comments: List<String>):
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val commentUsername: TextView = itemView.findViewById(R.id.textView4)
        val commentText: TextView = itemView.findViewById(R.id.textView5)

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
        val v = LayoutInflater.from(parent.context).inflate(R.layout.sub_comment, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return usernames.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.commentUsername.text = usernames[position]
        holder.commentText.text = comments[position]


    }


}