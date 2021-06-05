package com.example.myapplication

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.ServerConnection.*
import com.example.myapplication.databinding.ActivityAccountBinding
import com.example.myapplication.databinding.ActivityMainBinding
import android.net.Uri

class AddPhoto : AppCompatActivity() {

    lateinit var photoUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_photo)
        val bundle: Bundle? = intent.extras
        photoUri = bundle!!.getParcelable<Uri>("imgUri")
        photo.setImageURI(photoUri)

        actionButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            val description = newDescription.text
            intent.apply {
                putExtra("imgUri", photoUri)
                putExtra("description", description.toString())
            }
            setResult(Activity.RESULT_OK,intent)
            finish()
        }
    }

}