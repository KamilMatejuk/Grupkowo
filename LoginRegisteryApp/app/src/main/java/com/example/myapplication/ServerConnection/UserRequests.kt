package com.example.myapplication.ServerConnection

import android.content.Context
import com.android.volley.Request
import com.example.myapplication.Server
import org.json.JSONObject

/**
 * List of functions in module:
 *     register()
 *     login()
 *     getCurrentUser()
 *     getOtherUser()
 *     editProfile()
 *     deleteAccount()
 *     getUserGroupsMember()
 *     getUserGroupsAdmin()
 *     createGroup()
 */
object UserRequests {

    /**
     * Register a new user
     *
     * @param username username
     * @param email email address
     * @param password password
     * @param context applicationContext
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun register(
        username: String,
        email: String,
        password: String,
        context: Context,
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
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
        Server.sendRequest(
            url,
            Request.Method.POST,
            body,
            context,
            functionCorrect,
            functionError
        )
    }

    /**
     * Login existing user
     *
     * @param email username or email address
     * @param password password
     * @param context applicationContext
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun login(
        email: String,
        password: String,
        context: Context,
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
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
        Server.sendRequest(
            url,
            Request.Method.POST,
            body,
            context,
            functionCorrect,
            functionError
        )
    }

    /**
     * Get currently logged in user
     *
     * @param context applicationContext
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun getCurrentUser(
        context: Context,
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = Server.url + "user/me/"
        val body = null
        Server.sendRequest(
            url,
            Request.Method.GET,
            body,
            context,
            functionCorrect,
            functionError,
            token = true
        )
    }

    /**
     * Get info about other user
     *
     * @param context applicationContext
     * @param userId id number of user to check
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun getOtherUser(
        context: Context,
        userId: Int,
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = Server.url + "user/$userId/"
        val body = null
        Server.sendRequest(
            url,
            Request.Method.GET,
            body,
            context,
            functionCorrect,
            functionError,
            token = true
        )
    }

    /**
     * Get a list of all registered users
     *
     * @param context applicationContext
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun getAllUsers(
        context: Context,
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = Server.url + "users-all/"
        val body = null
        Server.sendRequest(
            url,
            Request.Method.GET,
            body,
            context,
            functionCorrect,
            functionError,
            token = true
        )
    }

    /**
     * Edit user's profile (choose args you want to edit)
     *
     * @param context applicationContext
     * @param username (optional) username
     * @param email (optional) email address
     * @param password (optional) password
     * @param avatarPath (optional) path into avatar image
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun editProfile(
        context: Context,
        username: String = "",
        email: String = "",
        password: String = "",
        avatarPath: String = "",
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = Server.url + "user/me/"
        var bodyStr = "{"
        if (username != "") {
            bodyStr += "\"username\": \"$username\","
        }
        if (email != "") {
            bodyStr += "\"email\": \"$email\","
        }
        if (password != "") {
            bodyStr += "\"password\": \"$password\","
        }
        if (avatarPath != "") {
            // TODO convert image into string of bytes
            val bytes = ""
            bodyStr += "\"avatar\": \"$bytes\","
        }
        if (bodyStr.endsWith(",")) {
            bodyStr = bodyStr.dropLast(1)
        }
        bodyStr = bodyStr
            .replace(" ", "")
            .replace("\n", "")
        bodyStr += "}"
        val body = JSONObject(bodyStr)
        Server.sendRequest(
            url,
            Request.Method.PUT,
            body,
            context,
            functionCorrect,
            functionError,
            token = true
        )
    }

    /**
     * Remove user'a account from database
     *
     * @param context applicationContext
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun deleteAccount(
        context: Context,
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = Server.url + "user/me/"
        val body = null
        Server.sendRequest(
            url,
            Request.Method.DELETE,
            body,
            context,
            functionCorrect,
            functionError,
            token = true
        )
    }

    /**
     * Get groups, where currently logged in user is a member
     *
     * @param context applicationContext
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun getUserGroupsMember(
        context: Context,
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = Server.url + "user/groups-user/"
        val body = null
        Server.sendRequest(
            url,
            Request.Method.GET,
            body,
            context,
            functionCorrect,
            functionError,
            token = true
        )
    }

    /**
     * Get groups, where currently logged in user is an admin
     *
     * @param context applicationContext
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun getUserGroupsAdmin(
        context: Context,
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = Server.url + "user/groups-admin/"
        val body = null
        Server.sendRequest(
            url,
            Request.Method.GET,
            body,
            context,
            functionCorrect,
            functionError,
            token = true
        )
    }

    /**
     * Create a new group
     *
     * @param context applicationContext
     * @param groupName name of new group
     * @param imagePath (optional) path into new group image
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun createGroup(
        context: Context,
        groupName: String,
        imagePath: String = "",
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = Server.url + "user/groups-admin/"

        var bodyStr = "{\"name\": \"$groupName\","
        if (imagePath != "") {
            // TODO convert image into string of bytes
            val bytes = ""
            bodyStr += "\"image\": \"$bytes\""
        }
        if (bodyStr.endsWith(",")) {
            bodyStr = bodyStr.dropLast(1)
        }
        bodyStr = bodyStr
            .replace(" ", "")
            .replace("\n", "")
        bodyStr += "}"
        val body = JSONObject(bodyStr)
        Server.sendRequest(
            url,
            Request.Method.POST,
            body,
            context,
            functionCorrect,
            functionError,
            token = true
        )
    }


}