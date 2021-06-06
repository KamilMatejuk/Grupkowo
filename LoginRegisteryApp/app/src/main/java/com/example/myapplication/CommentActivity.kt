package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityCommentsBinding
import com.example.myapplication.databinding.ActivityGroupsBinding

class CommentActivity:AppCompatActivity() {
    private lateinit var binding: ActivityCommentsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}