package com.example.myapplication.RecyclerItems

import com.example.myapplication.R
import kotlinx.serialization.Serializable

data class Group(
    var group_id: Int,
    var name: String,
    var image: String?
)
//
//@Serializable
//class Group {
//    var groupId: Int = 0
//    var groupPhoto: Int = R.mipmap.ic_launcher
//    var groupName: String = ""
//
//    constructor(id: Int, photo: Int, name: String) {
//        this@Group.groupId = id
//        this@Group.groupPhoto = photo
//        this@Group.groupName = name
//    }
//}