package com.example.myapplication

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.ServerConnection.UserRequests
import com.example.myapplication.databinding.ActivityCreateGroupBinding
import kotlinx.android.synthetic.main.activity_create_group.*
import java.io.File

class CreateGroupActivity: AppCompatActivity() {
    private lateinit var binding: ActivityCreateGroupBinding
    private var photoUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateGroupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun createGroup(view: View) {
        if (photoUri != null) {
            UserRequests.createGroup(applicationContext,
                binding.myGroupName.text.toString(),
                imagePath = photoUri!!,
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
        } else {
            UserRequests.createGroup(applicationContext,
                binding.myGroupName.text.toString(),
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
        }

        val intent = Intent(this, WallActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun changePhoto(view: View) {
        val intent = Intent(this, CameraActivity::class.java)
        startActivityForResult(intent, 69)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 69 && resultCode == RESULT_OK) {
            val uri = data?.getStringExtra("EXTRA_PATH") ?: ""
            photoUri = uri
            val imgFile = File(uri)
            if (imgFile.exists()) {
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                binding.groupImage.setImageBitmap(myBitmap)
            }
        }
    }
}