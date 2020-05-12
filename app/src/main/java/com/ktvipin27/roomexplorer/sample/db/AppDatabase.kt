package com.ktvipin27.roomexplorer.sample.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ktvipin27.roomexplorer.sample.db.dao.PersonDAO
import com.ktvipin27.roomexplorer.sample.db.entity.Person

/**
 * Created by Vipin KT on 08/05/20
 */
@Database(entities = [Person::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun personDAO(): PersonDAO

    companion object {
        const val DB_NAME = "app-db"
        private val lock = Any()
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(lock) {
                if (INSTANCE == null)
                    INSTANCE = createDB(context)
                return INSTANCE as AppDatabase
            }
        }

        private fun createDB(context: Context): AppDatabase {
            val database: Builder<AppDatabase> =
                Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
            return database
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}