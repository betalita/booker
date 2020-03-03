package cn.deepink.booker.model

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.room.*
import cn.deepink.booker.http.SOURCE
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Book(val source: Int, val name: String, val author: String = "", val summary: String = "", val cover: String = "", val link: String = "", var category: String = "", var state: Int = 0, @ColumnInfo(name = "last_chapter_name") var lastChapterName: String = "", @ColumnInfo(name = "last_update_time") var lastUpdateTime: Long = 0L, @ColumnInfo(name = "chapter_total") var chapterTotal: Int = 0, @ColumnInfo(name = "words_total") var wordsTotal: String = "", @ColumnInfo(name = "monthly_ticket") var monthlyTicket: Int = 0, @ColumnInfo(name = "recommended_ticket") var recommendedTicket: Int = 0, @ColumnInfo(name = "read_chapter") var readChapter: Int = 0, var notification: Int = 0, @ColumnInfo(name = "create_time") val createTime: Long = System.currentTimeMillis(), @PrimaryKey(autoGenerate = true) val id: Long = 0L) : Parcelable {

    @IgnoredOnParcel
    val sourceType: SOURCE
        get() = SOURCE.values().first { it.code == source }

    fun areContentTheSame(book: Book) = state == book.state && lastChapterName == book.lastChapterName && lastUpdateTime == book.lastUpdateTime && chapterTotal == book.chapterTotal && wordsTotal == book.wordsTotal && readChapter == book.readChapter && notification == book.notification

}

@Dao
interface BookDao {

    @Query("SELECT * FROM book ORDER BY last_update_time DESC")
    fun getAll(): LiveData<List<Book>>

    @Query("SELECT * FROM book WHERE state = 1 ORDER BY last_update_time DESC")
    fun getNeedCheckUpdate(): List<Book>

    @Query("SELECT COUNT(link) FROM book WHERE link=:link LIMIT 1")
    fun isExist(link: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg books: Book)

    @Update
    fun update(vararg books: Book)

    @Query("DELETE FROM book WHERE link=:link")
    fun deleteByLink(link: String)

    @Delete
    fun delete(vararg books: Book)

}