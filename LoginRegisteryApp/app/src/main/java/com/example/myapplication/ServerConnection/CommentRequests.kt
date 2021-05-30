package com.example.myapplication.ServerConnection

import android.content.Context
import com.android.volley.Request
import com.example.myapplication.Server
import org.json.JSONObject

/**
 * List of functions in module:
 *     getComments()
 *     addComment()
 *     editComment()
 *     deleteComment()
 */
object CommentRequests {

    /**
     * Get list of comments to specified post
     *
     * @param context applicationContext
     * @param groupId id number of group
     * @param postId id number of post
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun getComments(
        context: Context,
        groupId: Int,
        postId: Int,
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = Server.url + "group/$groupId/posts/$postId/comments/"
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
     * Add comment to post in this group
     *
     * @param context applicationContext
     * @param groupId id number of group
     * @param postId id number of post
     * @param commentText text of the new comment
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun addComment(
        context: Context,
        groupId: Int,
        postId: Int,
        commentText: String,
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = Server.url + "group/$groupId/posts/$postId/comments/"
        val body = JSONObject(
            """
            {
                "text": "$commentText"
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
     * Edit your comment to post in this group
     *
     * @param context applicationContext
     * @param groupId id number of group
     * @param postId id number of post
     * @param commentId id number of comment
     * @param newCommentText text of the comment
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun editComment(
        context: Context,
        groupId: Int,
        postId: Int,
        commentId: Int,
        newCommentText: String,
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = Server.url + "group/$groupId/posts/$postId/comments/$commentId/"
        val body = JSONObject(
            """
            {
                "text": "$newCommentText"
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
     * @param commentId id number of comment
     * @param functionCorrect function to be run, if request is successfull
     * @param functionError function to be run, if request return error or isn't successfull
     */
    fun deleteComment(
        context: Context,
        groupId: Int,
        postId: Int,
        commentId: Int,
        functionCorrect: (JSONObject) -> Unit,
        functionError: (String) -> Unit
    ) {
        val url = Server.url + "group/$groupId/posts/$postId/comments/$commentId/"
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