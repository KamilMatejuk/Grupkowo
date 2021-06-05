package com.example.myapplication

import android.R
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.ServerConnection.UserRequests
import com.example.myapplication.databinding.ActivityAccountBinding
import java.io.File


class AccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // show current username
        UserRequests.getCurrentUser(applicationContext,
            functionCorrect = { response ->
                run {
                    binding.username.text = response.getString("username")
                    val bitmap = Server.convertBytesToImg(response.getString("avatar"))
                    binding.avatarImage.setImageBitmap(bitmap)
                }
            },
            functionError = { errorMessage ->
                run {
                    Log.d(
                        AccountActivity::class.java.name,
                        "Couldn't get data from server: $errorMessage"
                    )
                }
            })
    }

    fun logout(view: View) {
        Server.deleteToken(this)
        // get back to login activity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun delete(view: View) {
        UserRequests.deleteAccount(applicationContext,
            functionCorrect = { response ->
                run {
                    Toast.makeText(
                        this,
                        "Successfully deleted account",
                        Toast.LENGTH_LONG
                    ).show()
                    Server.deleteToken(this)
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            },
            functionError = { errorMessage ->
                run {
                    Toast.makeText(
                        this,
                        "Couldn't delete account",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d(
                        AccountActivity::class.java.name,
                        "Couldn't delete account: $errorMessage"
                    )
                }
            })
    }

    fun changePassword(view: View) {
        // TODO add popup asking for new password
        UserRequests.editProfile(applicationContext,
            password = "password",
            functionCorrect = { response ->
                run {
                    Toast.makeText(
                        this,
                        "Successfully changed password",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            functionError = { errorMessage ->
                run {
                    Toast.makeText(
                        this,
                        "Couldn't change password",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d(
                        AccountActivity::class.java.name,
                        "Couldn't change password: $errorMessage"
                    )
                }
            })
    }

    fun changeUsername(view: View) {
        // TODO add popup asking for new username
        val newUsername = "new username"
        UserRequests.editProfile(applicationContext,
            username = newUsername,
            functionCorrect = { response ->
                run {
                    Toast.makeText(
                        this,
                        "Successfully changed username",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.username.text = newUsername
                }
            },
            functionError = { errorMessage ->
                run {
                    Toast.makeText(
                        this,
                        "Couldn't change username",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d(
                        AccountActivity::class.java.name,
                        "Couldn't change username: $errorMessage"
                    )
                }
            })
    }

    private fun changePhoto(uri: String) {
        UserRequests.editProfile(applicationContext,
            avatarPath = uri,
            functionCorrect = { response ->
                run {
                    Toast.makeText(
                        this,
                        "Successfully changed avatar",
                        Toast.LENGTH_LONG
                    ).show()
                    val imgFile = File(uri)
                    if (imgFile.exists()) {
                        val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        val myImage: ImageView = binding.avatarImage
                        myImage.setImageBitmap(myBitmap)
                    }
                }
            },
            functionError = { errorMessage ->
                run {
                    Toast.makeText(
                        this,
                        "Couldn't change avatar",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d(
                        AccountActivity::class.java.name,
                        "Couldn't change avatar: $errorMessage"
                    )
                }
            })
    }

    fun addPhoto(view: View?) {
        val intent = Intent(this, CameraActivity::class.java)
        startActivityForResult(intent, 69)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 69 && resultCode == RESULT_OK) {
            val uri = data?.getStringExtra("EXTRA_PATH") ?: ""
            changePhoto(uri)
        }
    }
}