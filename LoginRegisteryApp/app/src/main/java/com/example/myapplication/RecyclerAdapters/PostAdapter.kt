package com.example.myapplication.RecyclerAdapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.CommentActivity
import com.example.myapplication.R
import com.example.myapplication.RecyclerItems.Message
import com.example.myapplication.RecyclerItems.Post
import com.example.myapplication.Server
import com.example.myapplication.ServerConnection.ChatRequests
import com.example.myapplication.ServerConnection.PostRequests
import kotlinx.android.synthetic.main.sub_post.view.*

class PostAdapter(
    private var context: Context, private var group_id: Int, private var posts: List<Post>
) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var postTitle: TextView = itemView.findViewById(R.id.textView)
        var postDetails: TextView = itemView.findViewById(R.id.textView3)
        var postImage: ImageView = itemView.findViewById(R.id.avatar_image)

        var likesIMG: ImageView = itemView.findViewById(R.id.likeButton)
        var likesTV: TextView = itemView.findViewById(R.id.likeCounter)

        init {

            likesIMG.setOnClickListener {
                if (posts[position].author_liked) {
                    PostRequests.dislikePost(context, group_id, posts[position].post_id,
                        functionCorrect = {
                            run {
                                likesTV.text =
                                    "${likesTV.text.toString().split(" ")[0].toInt() - 1} likes"
                                likesIMG.setImageResource(R.drawable.heart_off)
                                likesTV.setTextColor(context.resources.getColor(R.color.black))
                            }
                        }, functionError = {})
                } else {
                    PostRequests.likePost(context, group_id, posts[position].post_id,
                        functionCorrect = {
                            run {
                                likesTV.text =
                                    "${likesTV.text.toString().split(" ")[0].toInt() + 1} likes"
                                likesIMG.setImageResource(R.drawable.heart)
                                likesTV.setTextColor(context.resources.getColor(R.color.purple_500))
                            }
                        }, functionError = {})
                }
                posts[position].author_liked = !posts[position].author_liked
            }

            itemView.commentButton.setOnClickListener { v: View ->
                val position: Int = this.adapterPosition
                val postId = posts[position].post_id
                val intent = Intent(context, CommentActivity::class.java)
                intent.putExtra("postId", postId)
                intent.putExtra("groupId", group_id)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.sub_post, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.postTitle.text = posts[position].author_username
        holder.postDetails.text = posts[position].text

        if (posts[position].author_avatar != null && posts[position].author_avatar != "") {
            val bitmap = Server.convertBytesToImg(posts[position].author_avatar!!)
            if (bitmap != null) {
                holder.postImage.setImageBitmap(bitmap)
            }
        }

        holder.likesTV.text = "${posts[position].likes} likes"
        if (posts[position].author_liked) {
            holder.likesIMG.setImageResource(R.drawable.heart)
            holder.likesTV.setTextColor(context.resources.getColor(R.color.purple_500))
        } else {
            holder.likesIMG.setImageResource(R.drawable.heart_off)
            holder.likesTV.setTextColor(context.resources.getColor(R.color.black))
        }
    }
}