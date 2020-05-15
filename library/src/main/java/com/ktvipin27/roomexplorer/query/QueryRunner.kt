package com.ktvipin27.roomexplorer.query

import android.content.Context
import android.content.ContextWrapper
import android.database.Cursor
import android.database.SQLException
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Created by Vipin KT on 14/05/20
 */
internal class QueryRunner internal constructor(
    context: Context,
    private val databaseClass: Class<out RoomDatabase>,
    private val databaseName: String
) : ContextWrapper(context) {

    internal fun getData(query: String, bindArgs: Array<Any>? = null): QueryResult<Cursor> = try {
        val c = supportSQLiteDatabase().query(query, bindArgs)
        if (null != c) QueryResult.Success(c) else QueryResult.Error(java.lang.Exception())
    } catch (ex: SQLException) {
        ex.printStackTrace()
        QueryResult.Error(ex)
    } catch (ex: Exception) {
        ex.printStackTrace()
        QueryResult.Error(ex)
    }

    internal fun execute(query: String): QueryResult<Any> = try {
        supportSQLiteDatabase().execSQL(query)
        QueryResult.Success("")
    } catch (ex: SQLException) {
        ex.printStackTrace()
        QueryResult.Error(ex)
    } catch (ex: Exception) {
        ex.printStackTrace()
        QueryResult.Error(ex)
    }

    private fun supportSQLiteDatabase(): SupportSQLiteDatabase {
        val roomDatabase = Room.databaseBuilder(this, databaseClass, databaseName).build()
        return roomDatabase.openHelper.writableDatabase
    }
}