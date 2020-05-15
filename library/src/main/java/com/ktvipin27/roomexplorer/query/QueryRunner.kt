package com.ktvipin27.roomexplorer.query

import android.content.Context
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

    internal fun query(
        query: String
    ): QueryResult<RowsAndColumns> = try {
        val c = supportSQLiteDatabase().query(query, null)
        if (null == c)
            QueryResult.Error(java.lang.Exception())
        else {
            c.moveToFirst()
            val columnNames = arrayListOf<String>()
            for (i in 0 until c.columnCount) columnNames.add(c.getColumnName(i))
            val rows = mutableListOf<ArrayList<String>>()
            do {
                val rowValues = arrayListOf<String>()
                for (i in 0 until c.columnCount) rowValues.add(c.getString(i))
                rows.add(rowValues)
            } while (c.moveToNext())
            c.close()
            QueryResult.Success(RowsAndColumns(columnNames, rows))
        }
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