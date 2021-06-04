package com.example.myapplication.RecyclerItems

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
 class Post {

    var p_id: Int = 0
    var a_id: Int = 0
    var g_id: Int = 0
    var creat: String = ""
    var txt: String = ""


    constructor(post_id: Int, author_id: Int, group_id: Int, created: String, text: String) {
        this@Post.p_id = post_id
        this@Post.a_id = author_id
        this@Post.g_id = group_id
        this@Post.creat = created
        this@Post.txt = text

    }

}