package com.example.myapplication.Notifications

import android.content.Context
import androidx.room.*
import com.example.myapplication.RecyclerItems.Message
import com.example.myapplication.RecyclerItems.Post


/**
 * Database access object with defined queries for posts table
 */
@Dao
interface PostDAO {
    @Query("SELECT * FROM post WHERE post_id = :id")
    fun getById(id: Int): List<Post>

    @Insert
    fun insert(item: Post)
}

/**
 * Database access object with defined queries for messages table
 */
@Dao
interface MessageDAO {
    @Query("SELECT * FROM message WHERE message_id = :id")
    fun getById(id: Int): List<Message>

    @Insert
    fun insert(item: Message)
}

/**
 * Datbase storing all posts and messages
 */
@Database(entities = [Post::class, Message::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDAO
    abstract fun messageDao(): MessageDAO
}

object DataIO {
    private lateinit var postsDB: PostDAO
    private lateinit var messagesDB: MessageDAO

    /**
     * Create database instance at the beginning of app lifecycle
     */
    fun initializeDatabase(context: Context) {
        val builder = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "todo-app-database"
        ).build()
        postsDB = builder.postDao()
        messagesDB = builder.messageDao()
    }

    /**
     * If post already exists: return true,
     * if not: insert into db and return false
     */
    fun checkIfPostExists(post: Post): Boolean {
        val samePosts = postsDB.getById(post.post_id)
        if (samePosts.isEmpty()) {
            postsDB.insert(post)
            return false
        }
        return true
    }

    /**
     * If post already exists: return true,
     * if not: insert into db and return false
     */
    fun checkIfMessageExists(message: Message): Boolean {
        val sameMessage = messagesDB.getById(message.message_id)
        if (sameMessage.isEmpty()) {
            messagesDB.insert(message)
            return false
        }
        return true
    }
}