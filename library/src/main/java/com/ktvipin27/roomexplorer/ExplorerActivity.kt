package com.ktvipin27.roomexplorer

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.android.synthetic.main.activity_explorer.*


/**
 * Created by Vipin KT on 08/05/20
 */
class ExplorerActivity : AppCompatActivity() {

    private lateinit var databaseClass: Class<out RoomDatabase>
    private lateinit var databaseName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explorer)

        intent.extras?.let {
            if (it.containsKey(RoomExplorer.KEY_DATABASE_CLASS)) {
                databaseClass = it.get(RoomExplorer.KEY_DATABASE_CLASS) as Class<out RoomDatabase>
            } else {
                throw java.lang.RuntimeException("No database class passed in the launch Intent.")
            }
            if (it.containsKey(RoomExplorer.KEY_DATABASE_NAME)) {
                databaseName = it.getString(RoomExplorer.KEY_DATABASE_NAME, "")
            } else {
                throw java.lang.RuntimeException("No database name passed in the launch Intent.")
            }
        } ?: throw java.lang.RuntimeException("No data passed in the launch Intent.")


        getTableNames()
    }

    private fun getTableNames() {
        // a query which returns a cursor with the list of tables in the database.
        // We use this cursor to populate spinner in the first row
        val queryResult: QueryResult =
            getData(Queries.GET_TABLE_NAMES)

        when (queryResult) {
            is QueryResult.Success -> {
                val tableNames = arrayListOf<String>()
                val cursor = queryResult.cursor
                cursor.moveToFirst()
                do {
                    //add names of the table to table names array list
                    tableNames.add(cursor.getString(0))
                } while (cursor.moveToNext())
                initSpinner(tableNames)
            }
            is QueryResult.Error -> {
                //TODO handle error
            }
        }
    }

    private fun initSpinner(tableNames: ArrayList<String>) {
        // Creating adapter for spinner
        val dataAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            tableNames
        )
        // Drop down layout style - list view
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // attaching data adapter to spinner
        sp_table.adapter = dataAdapter
        sp_table.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                toast(tableNames[position])
            }

        }
    }

    private fun getData(query: String): QueryResult {
        val roomDatabase = Room.databaseBuilder(this, databaseClass, databaseName).build()
        val sqlDB = roomDatabase.openHelper.writableDatabase

        return try {
            //execute the query results will be save in Cursor c
            val c = sqlDB.query(query, null)
            if (null != c && c.count > 0) {
                QueryResult.Success(c)
            } else
                QueryResult.Error(java.lang.Exception())
        } catch (ex: Exception) {
            ex.printStackTrace()
            QueryResult.Error(ex)
        }
    }
}
