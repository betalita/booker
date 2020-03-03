package cn.deepink.booker.common

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.deepink.booker.model.Book
import cn.deepink.booker.model.BookDao
import cn.deepink.framework.provider.ContextProvider

object Room {

    private val database by lazy { Room.databaseBuilder(ContextProvider.context, AppDatabase::class.java, "booker").build() }

    fun book() = database.bookDao()

}

@Database(version = 1, entities = [Book::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
}