package com.example.myapplication.RecyclerItems

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Post (
    @PrimaryKey var post_id: Int,
    @ColumnInfo(name = "created") var created: Int,
    @ColumnInfo(name = "text") var text: String,
    @ColumnInfo(name = "author_id") var author_id: String,
    @ColumnInfo(name = "author_username") var author_username: String,
    @ColumnInfo(name = "likes") var likes: Int,
    @ColumnInfo(name = "author_liked") var author_liked: Boolean,
    @ColumnInfo(name = "author_avatar") var author_avatar: String?
)
