package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.ServerConnection.UserRequests
import com.example.myapplication.databinding.ActivityAccountBinding

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

    fun changePhoto(view: View) {
        // TODO add popup for choosing image of taking a photo
        UserRequests.editProfile(applicationContext,
            avatarPath = "path/to/image",
            functionCorrect = { response ->
                run {
                    Toast.makeText(
                        this,
                        "Successfully changed avatar",
                        Toast.LENGTH_LONG
                    ).show()
                    // TODO update avatar image src
                    // binding.avatar.setImageResource(...)
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
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), 10
            )
        }

        val intent = Intent(this, CameraActivity::class.java)
        startActivityForResult(intent, 69)
    }

    private fun allPermissionsGranted() = arrayOf(Manifest.permission.CAMERA).all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)
        if (requestCode == 69 && resultCode == RESULT_OK){
            orientationChanged(DataIO.getAll())
        }
    }

    private fun orientationChanged(list: ArrayList<Image>){
        val orientation = resources.configuration.orientation
        val numberofColumns = if (orientation == Configuration.ORIENTATION_LANDSCAPE) 6 else 4
        val gridLayoutManager = GridLayoutManager(applicationContext, numberofColumns)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (DataIO.getAll()[position].type == "title") numberofColumns else 1
            }
        }
    }

}