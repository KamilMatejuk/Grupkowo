package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.ServerConnection.UserRequests
import com.example.myapplication.databinding.ActivityCreateGroupBinding
import kotlinx.android.synthetic.main.activity_create_group.*

class CreateGroupActivity: AppCompatActivity() {
    private lateinit var binding: ActivityCreateGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateGroupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun createGroup(view: View) {
        UserRequests.createGroup(applicationContext, binding.myGroupName.text.toString(), "C:/Users/micha/AndroidStudioProjects/Grupkowo/LoginRegisteryApp/app/src/main/res/drawable/ok.png",
            functionCorrect = { response ->
                run {
                    Toast.makeText(this, "Dodano Grupke!", Toast.LENGTH_LONG).show()
                }
            },
            functionError = { errorMessage ->
                run {
                    Log.d(
                        AccountActivity::class.java.name,
                        "Couldn't add group: $errorMessage"
                    )
                }
            })

        val intent = Intent(this, WallActivity::class.java)
        startActivity(intent)
    }
}