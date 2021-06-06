package com.example.myapplication.ServerConnection

import android.content.Context
import com.android.volley.Request
import com.example.myapplication.Server
import org.json.JSONObject

/**
 * List of functions in module:
 *     getMessages()
 *     sendMessage()
 *     likeMessage()
 *     dislikeMessage()
 */
object ChatRequests {

    /**
     * Get list of messages from chat associated with this group,
     * from specified time (timestampStart < timestampEnd),
     * or if timestamps not specified, list of latest 25 messages
     *
     * @param context applicationContext
     * @param groupId id number of group
     * @param timestampStart data początkowa zakresu (DD.MM.YYYY)
     * @param timestampEnd data końcowa zakresu (DD.MM.YYYY)
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun getMessages(
        context: Context,
        groupId: Int,
        timestampStart: String = "",
        timestampEnd: String = "",
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = if (timestampStart != "" && timestampEnd != "") {
            Server.url + "group/$groupId/chats/$timestampStart-$timestampEnd/"
        } else {
            Server.url + "group/$groupId/chats/"
        }
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
     * Send a new message to chat
     *
     * @param context applicationContext
     * @param groupId id number of group
     * @param messageText text of the message
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun sendMessage(
        context: Context,
        groupId: Int,
        messageText: String,
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = Server.url + "group/$groupId/chats/"
        val body = JSONObject(
            """
            {
                "text": "$messageText"
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
     * Add reaction to message in this chat (like)
     *
     * @param context applicationContext
     * @param groupId id number of group
     * @param messageId id number of message
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun likeMessage(
        context: Context,
        groupId: Int,
        messageId: Int,
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = Server.url + "group/$groupId/chats/$messageId/reactions/"
        val body = null
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
     * Remove reaction to message in this chat (dislike)
     *
     * @param context applicationContext
     * @param groupId id number of group
     * @param messageId id number of message
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun dislikeMessage(
        context: Context,
        groupId: Int,
        messageId: Int,
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = Server.url + "group/$groupId/chats/$messageId/reactions/"
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