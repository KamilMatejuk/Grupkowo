package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.ServerConnection.UserRequests
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_wall.*
import org.json.JSONObject

class WallActivity : AppCompatActivity() {
    companion object {
        const val TAG: String = "WallActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wall)
    }



//    override fun onResponseArrived(requestId: Int, error: String?, response: JSONObject?) {
////        finish()
//    }
}