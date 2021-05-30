package com.example.myapplication.ServerConnection

import android.content.Context
import com.android.volley.Request
import com.example.myapplication.Server
import org.json.JSONObject

/**
 * List of functions in module:
 *     getPosts()
 *     addPost()
 *     editPost()
 *     deletePost()
 *     likePost()
 *     dislikePost()
 */
object PostRequests {

    /**
     * Get list of posst from group, from specified time (timestampStart < timestampEnd),
     * or if timestamps not specified, list of latest 25 posts
     *
     * @param context applicationContext
     * @param groupId id number of group
     * @param timestampStart data początkowa zakresu (DD.MM.YYYY)
     * @param timestampEnd data końcowa zakresu (DD.MM.YYYY)
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun getPosts(
        context: Context,
        groupId: Int,
        timestampStart: String = "",
        timestampEnd: String = "",
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = if (timestampStart != "" && timestampEnd != "")  {
            Server.url + "group/$groupId/posts/$timestampStart-$timestampEnd/"
        } else {
            Server.url + "group/$groupId/posts/"
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
     * Create a new post in this group
     *
     * @param context applicationContext
     * @param groupId id number of group
     * @param postText text of the post
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun addPost(
        context: Context,
        groupId: Int,
        postText: String,
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = Server.url + "group/$groupId/posts/"
        val body = JSONObject(
            """
            {
                "text": "$postText"
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
            functionError,
            token = true
        )
    }

    /**
     * Edit your post in this group
     *
     * @param context applicationContext
     * @param groupId id number of group
     * @param postId id number of post
     * @param newPostText text of the post
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun editPost(
        context: Context,
        groupId: Int,
        postId: Int,
        newPostText: String,
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = Server.url + "group/$groupId/posts/$postId/"
        val body = JSONObject(
            """
            {
                "text": "$newPostText"
            }
            """.replace(" ", "")
                .replace("\n", "")
        )
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
     * Remove your post in this group from database
     *
     * @param context applicationContext
     * @param groupId id number of group
     * @param postId id number of post
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun deletePost(
        context: Context,
        groupId: Int,
        postId: Int,
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = Server.url + "group/$groupId/posts/$postId/"
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
     * Add reaction to post in this group (like)
     *
     * @param context applicationContext
     * @param groupId id number of group
     * @param postId id number of post
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun likePost(
        context: Context,
        groupId: Int,
        postId: Int,
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = Server.url + "group/$groupId/posts/$postId/reactions/"
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
     * Remove reaction to post in this group (dislike)
     *
     * @param context applicationContext
     * @param groupId id number of group
     * @param postId id number of post
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun dislikePost(
        context: Context,
        groupId: Int,
        postId: Int,
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = Server.url + "group/$groupId/posts/$postId/reactions/"
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