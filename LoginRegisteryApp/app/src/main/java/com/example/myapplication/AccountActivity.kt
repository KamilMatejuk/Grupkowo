package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.myapplication.ServerConnection.UserRequests
import com.example.myapplication.databinding.ActivityAccountBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
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

    @SuppressLint("CutPasteId")
    fun changePassword(view: View) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_edittext, null)
        val popupWindow = PopupWindow(
            view,
            LinearLayout.LayoutParams.MATCH_PARENT, // width
            LinearLayout.LayoutParams.WRAP_CONTENT, // height
            true
        )
        popupWindow.elevation = 10.0F

        val et = view.findViewById<EditText>(R.id.edit_text)
        val btn = view.findViewById<FloatingActionButton>(R.id.btn)
        et.hint = "New password"
        btn.setOnClickListener {
            popupWindow.dismiss()
        }

        // close popup
        popupWindow.setOnDismissListener {
            val pass = et.text.toString().trim()
            if (pass.isNotEmpty()) {
                UserRequests.editProfile(applicationContext,
                    password = pass,
                    functionCorrect = {
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
        }

        // show the popup window on app
        val rootLayout = view.findViewById<ConstraintLayout>(R.id.root_layout)
        TransitionManager.beginDelayedTransition(rootLayout)
        popupWindow.showAtLocation(rootLayout, Gravity.CENTER, 0, 0)

    }

    fun changeUsername(view: View) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_edittext, null)
        val popupWindow = PopupWindow(
            view,
            LinearLayout.LayoutParams.MATCH_PARENT, // width
            LinearLayout.LayoutParams.WRAP_CONTENT, // height
            true
        )
        popupWindow.elevation = 10.0F

        val et = view.findViewById<EditText>(R.id.edit_text)
        val btn = view.findViewById<FloatingActionButton>(R.id.btn)
        et.hint = "New username"
        btn.setOnClickListener {
            popupWindow.dismiss()
        }

        // close popup
        popupWindow.setOnDismissListener {
            val name = et.text.toString().trim()
            if (name.isNotEmpty()) {
                UserRequests.editProfile(applicationContext,
                    username = name,
                    functionCorrect = { response ->
                        run {
                            Toast.makeText(
                                this,
                                "Successfully changed username",
                                Toast.LENGTH_LONG
                            ).show()
                            binding.username.text = name
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
        }

        // show the popup window on app
        val rootLayout = view.findViewById<ConstraintLayout>(R.id.root_layout)
        TransitionManager.beginDelayedTransition(rootLayout)
        popupWindow.showAtLocation(rootLayout, Gravity.CENTER, 0, 0)
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
                        binding.avatarImage.setImageBitmap(myBitmap)
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