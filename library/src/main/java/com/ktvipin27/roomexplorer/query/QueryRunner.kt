package com.ktvipin27.roomexplorer.query

import android.content.Context
import android.database.Cursor
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Created by Vipin KT on 14/05/20
 */
internal object QueryRunner {
    private lateinit var context: Context
    private lateinit var databaseClass: Class<out RoomDatabase>
    private lateinit var databaseName: String

    fun init(
        context: Context,
        databaseClass: Class<out RoomDatabase>,
        databaseName: String
    ) {
        this.context = context
        this.databaseClass = databaseClass
        this.databaseName = databaseName
    }

    internal fun getData(query: String, bindArgs: Array<Any>? = null): QueryResult<Cursor> = try {
        val c = supportSQLiteDatabase().query(query, bindArgs)
        if (null != c) QueryResult.Success(c) else QueryResult.Error(java.lang.Exception())
    } catch (ex: Exception) {
        ex.printStackTrace()
        QueryResult.Error(ex)
    }

    internal fun execute(query: String): QueryResult<Any> = try {
        supportSQLiteDatabase().execSQL(query)
        QueryResult.Success("")
    } catch (ex: Exception) {
        ex.printStackTrace()
        QueryResult.Error(ex)
    }

    private fun supportSQLiteDatabase(): SupportSQLiteDatabase {
        val roomDatabase = Room.databaseBuilder(context, databaseClass, databaseName).build()
        return roomDatabase.openHelper.writableDatabase
    }
}