package com.example.myapplication.Notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.example.myapplication.GroupsActivity
import com.example.myapplication.R
import com.example.myapplication.RecyclerItems.Group
import com.example.myapplication.RecyclerItems.Message
import com.example.myapplication.RecyclerItems.Post
import com.example.myapplication.ServerConnection.ChatRequests
import com.example.myapplication.ServerConnection.PostRequests
import com.example.myapplication.ServerConnection.UserRequests
import com.google.gson.GsonBuilder
import kotlinx.coroutines.delay

class DownloadWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {
    private var active: Boolean = true

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

    override suspend fun doWork(): Result {
        while (active) {
            checkChanges()
            delay(5000)
        }
        return Result.success()
    }

    private fun checkChanges() {
        // get data from groups admin
        UserRequests.getUserGroupsAdmin(applicationContext,
            functionCorrect = { response ->
                run {
                    // convert into a list of objects
                    var list = response.get("groups").toString()
                    val groups1: List<Group> =
                        GsonBuilder().create().fromJson(list, Array<Group>::class.java).toList()
                    // get data from groups member
                    UserRequests.getUserGroupsMember(applicationContext,
                        functionCorrect = { response ->
                            run {
                                // convert into a list of objects
                                list = response.get("groups").toString()
                                val groups2: List<Group> =
                                    GsonBuilder().create().fromJson(list, Array<Group>::class.java)
                                        .toList()
                                // check changes
                                getGroupData(groups1 + groups2)
                            }
                        },
                        functionError = {})
                }
            },
            functionError = {})
    }

    private fun getGroupData(groups: List<Group>) {
        for (group in groups) {
            PostRequests.getPosts(applicationContext, group.group_id,
                functionCorrect = { response ->
                    run {
                        // convert into a list of objects
                        val list = response.get("posts").toString()
                        val posts: List<Post> =
                            GsonBuilder().create().fromJson(list, Array<Post>::class.java).toList()
                        // check changes
                        Thread {
                            for (post in posts) {
                                if (!DataIO.checkIfPostExists(post)) {
                                    addNotification(
                                        "New activity",
                                        "There is a new post in group \"${group.name}\""
                                    )
                                }
                            }
                        }.start()
                    }
                },
                functionError = {})

            ChatRequests.getMessages(applicationContext, group.group_id,
                functionCorrect = { response ->
                    run {
                        // convert into a list of objects
                        val list = response.get("messages").toString()
                        val messages: List<Message> =
                            GsonBuilder().create().fromJson(list, Array<Message>::class.java).toList()
                        // check changes
                        Thread {
                            for (message in messages) {
                                if (!DataIO.checkIfMessageExists(message)) {
                                    addNotification(
                                        "New activity",
                                        "There is a new message in group \"${group.name}\""
                                    )
                                }
                            }
                        }.start()
                    }
                },
                functionError = {})
        }
    }

    fun addNotification(title: String, text: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val intent = Intent(applicationContext, GroupsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(text)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(0, builder.build())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val channelId = "CHANNEL_ID"
        private const val name = "CHANNEL_NAME"
        private const val descriptionText = "CHANNEL_DESCRIPTION"
    }
}