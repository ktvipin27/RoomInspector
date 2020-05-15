package com.ktvipin27.roomexplorer.query

import com.ktvipin27.roomexplorer.util.forEachIndexed

/**
 * Created by Vipin KT on 12/05/20
 */
object QueryBuilder {
    const val GET_TABLE_NAMES = "SELECT name _id FROM sqlite_master WHERE type ='table'"

    infix fun getAllValues(tableName: String) = "SELECT * FROM $tableName"
    infix fun dropTable(tableName: String) = "DROP TABLE $tableName"
    infix fun deleteTable(tableName: String) = "DELETE FROM $tableName"
    infix fun getColumnNames(tableName: String) = "PRAGMA table_info($tableName)"

    fun insert(tableName: String, values: List<String>): String {
        var insertQuery = "INSERT INTO $tableName VALUES("
        values.forEachIndexed { index, value ->
            insertQuery += "'$value'"
            if (index != values.size - 1)
                insertQuery += ","
        }
        insertQuery += ")"
        return insertQuery
    }

    fun updateTable(
        tableName: String,
        columnNames: List<String>,
        oldValues: List<String>,
        newValues: List<String>
    ): String {
        var query = "Update $tableName set "
        Pair(columnNames, newValues).forEachIndexed { index, columnName, value ->
            query += "$columnName = '$value'"
            if (index != columnNames.size - 1)
                query += ", "
        }
        query += " where "
        Pair(columnNames, oldValues).forEachIndexed { index, columnName, value ->
            query += "$columnName = '$value'"
            if (index != columnNames.size - 1)
                query += " AND "
        }
        return query
    }
}