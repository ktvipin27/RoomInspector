package com.ktvipin27.roomexplorer

/**
 * Created by Vipin KT on 12/05/20
 */
object Queries {
    const val GET_TABLE_NAMES = "SELECT name _id FROM sqlite_master WHERE type ='table'"
    infix fun GET_TABLE_DATA(tableName: String) = "SELECT * FROM $tableName"
    infix fun DROP_TABLE(tableName: String) = "DROP TABLE $tableName"
    infix fun DELETE_TABLE(tableName: String) = "DELETE FROM $tableName"
    infix fun GET_COLUMN_NAMES(tableName: String) = "PRAGMA table_info($tableName)"
    infix fun INSERT(tableNameAndValues: Pair<String, List<String>>): String {
        var insertQuery = "INSERT INTO ${tableNameAndValues.first} VALUES("
        tableNameAndValues.second.forEachIndexed { index, value ->
            insertQuery += "'$value'"
            if (index != tableNameAndValues.second.size - 1)
                insertQuery += ","
        }
        insertQuery += ")"
        return insertQuery
    }

    fun getUpdateQuery(
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