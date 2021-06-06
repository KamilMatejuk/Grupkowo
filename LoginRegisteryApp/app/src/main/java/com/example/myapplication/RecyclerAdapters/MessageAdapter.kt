package com.example.myapplication.RecyclerAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.RecyclerItems.Message
import com.example.myapplication.ServerConnection.ChatRequests
import com.example.myapplication.ServerConnection.PostRequests
import java.text.SimpleDateFormat
import java.util.*


class MessageAdapter(
    private var context: Context,
    private var groupId: Int,
    private var messages: List<Message>
) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var authorTV: TextView = itemView.findViewById(R.id.author)
        var textTV: TextView = itemView.findViewById(R.id.text)
        var dateTV: TextView = itemView.findViewById(R.id.date)
        var likesIMG: ImageView = itemView.findViewById(R.id.likes_img)
        var likesTV: TextView = itemView.findViewById(R.id.likes_tv)

        init {
            likesIMG.setOnClickListener {
                if (messages[position].author_liked) {
                    ChatRequests.dislikeMessage(context, groupId, messages[position].message_id,
                        functionCorrect = {
                            run {
                                likesTV.text =
                                    "${likesTV.text.toString().split(" ")[0].toInt() - 1} likes"
                                likesIMG.setImageResource(R.drawable.like_off)
                                likesTV.setTextColor(context.resources.getColor(R.color.black))
                            }
                        }, functionError = {})
                } else {
                    ChatRequests.likeMessage(context, groupId, messages[position].message_id,
                        functionCorrect = {
                            run {
                                likesTV.text =
                                    "${likesTV.text.toString().split(" ")[0].toInt() + 1} likes"
                                likesIMG.setImageResource(R.drawable.like_on)
                                likesTV.setTextColor(context.resources.getColor(R.color.purple_500))
                            }
                        }, functionError = {})
                }
                messages[position].author_liked = !messages[position].author_liked
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.sub_mess, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: MessageAdapter.ViewHolder, position: Int) {
        holder.authorTV.text = messages[position].author_username
        holder.textTV.text = messages[position].text
        holder.dateTV.text = SimpleDateFormat("(dd.MM.yyyy)")
            .format(Date(messages[position].created * 1000L))
        holder.likesTV.text = "${messages[position].likes} likes"
        if (messages[position].author_liked) {
            holder.likesIMG.setImageResource(R.drawable.like_on)
            holder.likesTV.setTextColor(context.resources.getColor(R.color.purple_500))
        } else {
            holder.likesIMG.setImageResource(R.drawable.like_off)
            holder.likesTV.setTextColor(context.resources.getColor(R.color.black))
        }
    }
}