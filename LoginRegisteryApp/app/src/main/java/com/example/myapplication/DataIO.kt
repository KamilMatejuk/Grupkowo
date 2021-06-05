package com.example.myapplication

import android.content.Context
import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime
import kotlin.random.Random

/**
 * Data class containing all informations about each task item
 *
 * @param id number of item on a list
 * @param path system path to image file
 * @param description description of an image
 * @param rating in stars in range 0 - 5
 */
@Entity
@Parcelize
data class Image(
    @PrimaryKey var id: Int,
    @ColumnInfo(name = "path") var path: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "rating") var rating: Int,
    @ColumnInfo(name = "date") var date: LocalDateTime?,
    @ColumnInfo(name = "type") var type: String, // "title" if this item will be the title, other if its a regular image
) : Parcelable

/**
 * Database access object with defined queries
 */
@Dao
interface ImageDAO {
    @Query("SELECT * FROM Image")
    fun getAll(): List<Image>

    @Insert
    fun insert(item: Image)

    @Delete
    fun delete(item: Image)

    @Query("DELETE FROM Image")
    fun clear()
}

/**
 * Datbase storing all images
 */
@Database(entities = [Image::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ImageDao(): ImageDAO
}

/**
 * Converters used to store LocalDateTime, Priority and Category
 */
object Converters {
    /** String -> LocalDateTime */
    @TypeConverter
    @JvmStatic
    fun toDate(dateString: String?): LocalDateTime? {
        return if (dateString == null) {
            null
        } else {
            LocalDateTime.parse(dateString)
        }
    }

    /** LocalDateTime -> String */
    @TypeConverter
    @JvmStatic
    fun toDateString(date: LocalDateTime?): String? {
        return date?.toString()
    }
}


/**
 * Singleton object used for connection, between app and database
 */
object  DataIO {
    private lateinit var itemList: ArrayList<Image>
    private lateinit var db: ImageDAO

    /**
     * Create database instance at the beginning of app lifecycle
     */
    fun initializeDatabase(context: Context) {
        db = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "l4z3-database"
        ).build().ImageDao()
        this.itemList = db.getAll() as ArrayList<Image>
    }

    /**
     * Getter
     */
    fun getAll(): ArrayList<Image> {
        return if (this::itemList.isInitialized) {
            arrayListOf(Image(-1, "", "", 5, null, "title")) +
                    itemList.filter { it.rating == 5 }.sortedWith(compareBy { it.date }) +
                    arrayListOf(Image(-1, "", "", 4, null, "title")) +
                    itemList.filter { it.rating == 4 }.sortedWith(compareBy { it.date }) +
                    arrayListOf(Image(-1, "", "", 3, null, "title")) +
                    itemList.filter { it.rating == 3 }.sortedWith(compareBy { it.date }) +
                    arrayListOf(Image(-1, "", "", 2, null, "title")) +
                    itemList.filter { it.rating == 2 }.sortedWith(compareBy { it.date }) +
                    arrayListOf(Image(-1, "", "", 1, null, "title")) +
                    itemList.filter { it.rating == 1 }.sortedWith(compareBy { it.date }) +
                    arrayListOf(Image(-1, "", "", 0, null, "title")) +
                    itemList.filter { it.rating == 0 }.sortedWith(compareBy { it.date })
        } else {
            arrayListOf()
        } as ArrayList<Image>
    }

    /**
     * Operations on values
     */
    fun add(path: String, description: String) {
        val item = Image(
            itemList.size + 1,
            path,
            description,
            0,
            LocalDateTime.now(),
            "img"
        )
        itemList.add(item)
        this.db.insert(item)
    }

    fun edit(
        id: Int, rating: Int
    ) {
        for (i in itemList) {
            if (i.id == id) {
                this.db.delete(i)
                i.rating = rating
                this.db.insert(i)
            }
        }
    }
}