package com.example.myapplication.RecyclerAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.RecyclerItems.User
import com.example.myapplication.ServerConnection.ChatRequests
import java.text.SimpleDateFormat
import java.util.*


class UsersAdapter(
    private var context: Context,
    private var usersAll: List<User>,
    private var usersBelong: List<User>
) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {
    var userToAdd: ArrayList<Int> = arrayListOf()
    var userToDelete: ArrayList<Int> = arrayListOf()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var usernameTV: TextView = itemView.findViewById(R.id.username)
        var checkbox: CheckBox = itemView.findViewById(R.id.checkBox)

        init {
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    userToAdd.add(usersAll[adapterPosition].user_id)
                } else {
                    userToDelete.add(usersAll[adapterPosition].user_id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.sub_user, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return usersAll.size
    }

    override fun onBindViewHolder(holder: UsersAdapter.ViewHolder, position: Int) {
        holder.usernameTV.text = usersAll[position].username
        for (user in usersBelong) {
            if (user.user_id == usersAll[position].user_id){
                holder.checkbox.isChecked = true
            }
        }
    }
}