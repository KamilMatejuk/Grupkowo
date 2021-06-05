package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.net.Uri
import com.example.myapplication.databinding.AddPhotoBinding
import kotlinx.android.synthetic.main.add_photo.*

class AddPhoto : AppCompatActivity() {
    private lateinit var binding: AddPhotoBinding

    lateinit var photoUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_photo)
        val bundle: Bundle? = intent.extras
        photoUri = bundle!!.getParcelable("imgUri")!!
        binding.photo.setImageURI(photoUri)

        binding.actionButton.setOnClickListener {
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