package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_create_group.*

class CreateGroupActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        make_group_button.setOnClickListener {
            val intent = Intent(this, WallActivity::class.java)
            startActivity(intent)
        }
    }
}