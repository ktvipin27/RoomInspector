package com.ktvipin27.roomexplorer.query

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ktvipin27.roomexplorer.util.RowsAndColumns

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
        query: String,
        onSuccess: (RowsAndColumns) -> Unit,
        onError: (e: Exception) -> Unit
    ) = try {
        val c = supportSQLiteDatabase().query(query, null)
        if (null == c)
            onError(java.lang.Exception())
        else {
            val columnNames = arrayListOf<String>()
            for (i in 0 until c.columnCount) columnNames.add(c.getColumnName(i))
            val rows = mutableListOf<ArrayList<String>>()
            c.moveToFirst()
            do {
                val rowValues = arrayListOf<String>()
                for (i in 0 until c.columnCount)
                    try {
                        val value = c.getString(i)
                        rowValues.add(value)
                    } catch (e: Exception) {
                    }
                rows.add(rowValues)
            } while (c.moveToNext())
            c.close()
            onSuccess(
                RowsAndColumns(
                    columnNames,
                    rows
                )
            )
        }
    } catch (ex: Exception) {
        onError(ex).also { ex.printStackTrace() }
    }

    internal fun execute(
        query: String,
        onSuccess: () -> Unit,
        onError: (e: Exception) -> Unit
    ) = try {
        supportSQLiteDatabase().execSQL(query).also { onSuccess() }
    } catch (ex: Exception) {
        onError(ex).also { ex.printStackTrace() }
    }

    private fun supportSQLiteDatabase(): SupportSQLiteDatabase {
        val roomDatabase = Room.databaseBuilder(context, databaseClass, databaseName).build()
        return roomDatabase.openHelper.writableDatabase
    }
}