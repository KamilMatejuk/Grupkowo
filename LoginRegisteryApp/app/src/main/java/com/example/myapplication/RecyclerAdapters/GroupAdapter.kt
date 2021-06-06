package com.example.myapplication.RecyclerAdapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.RecyclerItems.Group
import com.example.myapplication.Server
import com.example.myapplication.WallActivity

class GroupAdapter(private var context: Context, private var groups: List<Group>, private var admin: Boolean): RecyclerView.Adapter<GroupAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var groupName: TextView = itemView.findViewById(R.id.groupName)
        var groupImage: ImageView = itemView.findViewById(R.id.groupPhoto)

        init{
            itemView.setOnClickListener { v: View ->
                val position: Int = adapterPosition
                val intent = Intent(context, WallActivity::class.java)
                intent.putExtra("groupId", groups[position].group_id)
                intent.putExtra("admin", admin)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.sub_group, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    override fun onBindViewHolder(holder: GroupAdapter.ViewHolder, position: Int) {
        if (groups[position].image != null && groups[position].image != "") {
            val bitmap = Server.convertBytesToImg(groups[position].image!!)
            if (bitmap != null) {
                holder.groupImage.setImageBitmap(bitmap)
            }
        }
        holder.groupName.text = groups[position].name
    }
}