package com.example.myapplication.ServerConnection

import android.content.Context
import com.android.volley.Request
import com.example.myapplication.Server
import org.json.JSONObject

/**
 * List of functions in module:
 *     getGroupInfo()
 *     deleteGroup()
 *     getGroupUsers()
 *     addUserToGroup()
 *     removeUserFromGroup()
 */
object GroupRequests {

    /**
     * Get information about group
     *
     * @param context applicationContext
     * @param groupId id number of group
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun getGroupInfo(
        context: Context,
        groupId: Int,
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = Server.url + "group/$groupId/"
        val body = null
        Server.sendRequest(
            url,
            Request.Method.GET,
            body,
            context,
            functionCorrect,
            functionError
        )
    }

    /**
     * Remove group from database
     *
     * @param context applicationContext
     * @param groupId id number of group
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun deleteGroup(
        context: Context,
        groupId: Int,
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = Server.url + "group/$groupId/"
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
     * Get list of members of this group
     *
     * @param context applicationContext
     * @param groupId id number of group
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun getGroupUsers(
        context: Context,
        groupId: Int,
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = Server.url + "group/$groupId/users"
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
     * Add user to this group
     *
     * @param context applicationContext
     * @param groupId id number of group
     * @param userId id number of user
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun addUserToGroup(
        context: Context,
        groupId: Int,
        userId: Int,
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = Server.url + "group/$groupId/users/"
        val body = JSONObject(
            """
            {
                "user_id": "$userId"
            }
            """.replace("\n", "")
        )
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

    /**
     * Remove user from this group
     *
     * @param context applicationContext
     * @param groupId id number of group
     * @param userId id number of user
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun removeUserFromGroup(
        context: Context,
        groupId: Int,
        userId: Int,
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = Server.url + "group/$groupId/users/$userId/"
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
}