package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class SetActivity : AppCompatActivity() {
    private lateinit var path: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set)

        path = intent.getStringExtra("EXTRA_PATH").toString().replace("file://", "")
    }

    fun save(view: View) {
        val desc = findViewById<EditText>(R.id.description_et).text.toString()
        Thread {
            DataIO.add(path, desc)
        }.start()
        setResult(RESULT_OK)
        finish()
    }
}