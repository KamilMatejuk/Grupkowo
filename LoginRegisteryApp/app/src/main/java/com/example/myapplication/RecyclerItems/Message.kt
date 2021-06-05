package com.example.myapplication.RecyclerItems

data class Message (
    var message_id: Int,
    var created: Int,
    var text: String,
    var author_id: String,
    var author_username: String,
    var likes: Int,
    var author_liked: Boolean,
)