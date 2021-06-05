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
import com.example.myapplication.WallActivity

class GroupAdapter(private var context: Context, private var groups: List<Group>): RecyclerView.Adapter<GroupAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var groupName: TextView = itemView.findViewById(R.id.groupName)
        var groupImage: ImageView = itemView.findViewById(R.id.groupPhoto)

        init{
            itemView.setOnClickListener { v: View ->
                val position: Int = adapterPosition
                Toast.makeText(
                    itemView.context,
                    "You clicked on item: ${position + 1}, group pos: ${groups[position].groupId}",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(context, WallActivity::class.java)
                intent.putExtra("groupId", groups[position].groupId)
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
        holder.groupImage.setImageResource(R.mipmap.ic_launcher)
        holder.groupName.text = groups[position].groupName
    }
}