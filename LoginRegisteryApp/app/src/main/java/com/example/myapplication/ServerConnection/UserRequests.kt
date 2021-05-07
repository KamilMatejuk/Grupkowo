package com.example.myapplication.ServerConnection

import com.android.volley.Request
import com.example.myapplication.Server
import com.example.myapplication.ServerLisener
import org.json.JSONObject

object UserRequests {

    /**
     * Register a new user
     *
     * @param username username
     * @param email email address
     * @param password password
     * @param requestId number related to request, used for finding response correlating
     *           with request later, when response arrives
     * @param cls class sending the request, implementing ServerLisener
     */
    fun register(
        username: String,
        email: String,
        password: String,
        requestId: Int,
        cls: ServerLisener
    ) {
        val url = Server.url + "user/me/"
        val body = JSONObject(
            """
            {
                "username": "$username",
                "email": "$email",
                "password": "$password"
            }
            """.replace(" ", "")
                .replace("\n", "")
        )
        Server.sendRequest(url, Request.Method.POST, body, requestId, cls)
    }

    /**
     * Login existing user
     *
     * @param email username or email address
     * @param password password
     * @param requestId number related to request, used for finding response correlating
     *           with request later, when response arrives
     * @param cls class sending the request, implementing ServerLisener
     */
    fun login(
        email: String,
        password: String,
        requestId: Int,
        cls: ServerLisener
    ) {
        val url = Server.url + "user/login/"
        val body = JSONObject(
            """
            {
                "name": "$email",
                "password": "$password"
            }
            """.replace(" ", "")
                .replace("\n", "")
        )
        Server.sendRequest(url, Request.Method.POST, body, requestId, cls)
    }

    /**
     * Get currently logged in user
     *
     * @param requestId number related to request, used for finding response correlating
     *           with request later, when response arrives
     * @param cls class sending the request, implementing ServerLisener
     */
    fun getCurrentUser(
        requestId: Int,
        cls: ServerLisener
    ) {
        val url = Server.url + "user/me/"
        val body = null
        Server.sendRequest(url, Request.Method.GET, body, requestId, cls, token=true)
    }
}