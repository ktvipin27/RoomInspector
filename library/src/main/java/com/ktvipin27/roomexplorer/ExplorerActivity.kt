package com.ktvipin27.roomexplorer

import android.database.SQLException
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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

        when (val queryResult = getData(Queries.GET_TABLE_NAMES)) {
            is QueryResult.Success -> {
                val tableNames = arrayListOf<String>()
                val cursor = queryResult.cursor
                cursor.moveToFirst()
                do {
                    //add names of the table to table names array list
                    tableNames.add(cursor.getString(0))
                } while (cursor.moveToNext())
                cursor.close()
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
                displayCount(tableNames[position])
                displayData(tableNames[position])
            }

        }
    }

    private fun displayData(tableName: String) {
        tl.removeAllViews()
        when (val queryResult = getData(Queries.GET_TABLE_DATA + tableName)) {
            is QueryResult.Success -> {
                val cursor = queryResult.cursor

                //display the first row of the table with column names of the table selected by the user
                val th = TableRow(this)
                cursor.moveToFirst()
                for (i in 0 until cursor.columnCount) {
                    TextView(this)
                        .apply {
                            setPadding(10, 10, 10, 10)
                            text = cursor.getColumnName(i)
                            setTextColor(Color.BLACK)
                            typeface = Typeface.DEFAULT_BOLD
                        }.also {
                            th.addView(it)
                        }
                }
                tl.addView(th)
                do {
                    val tr = TableRow(applicationContext).apply {
                        setPadding(0, 2, 0, 2)
                    }
                    for (i in 0 until cursor.columnCount) {
                        TextView(this)
                            .apply {
                                setPadding(10, 10, 10, 10)
                                text = try {
                                    cursor.getString(i)
                                } catch (e: Exception) {
                                    ""
                                }
                                setTextColor(Color.BLACK)
                                typeface = Typeface.DEFAULT
                            }.also {
                                tr.addView(it)
                            }
                    }
                    tl.addView(tr)
                } while (cursor.moveToNext())
                cursor.close()
            }
            is QueryResult.Error -> {
                //TODO handle error
            }
        }
    }

    private fun displayCount(tableName: String) {
        when (val queryResult = getData(Queries.GET_COUNT + tableName)) {
            is QueryResult.Success -> {
                val cursor = queryResult.cursor
                cursor.moveToFirst()
                val count = cursor.getInt(0)
                tv_record_count.text = getString(R.string.number_of_records, count)
                cursor.close()
            }
            is QueryResult.Error -> {
                //TODO handle error
            }
        }
    }

    private fun getData(query: String, bindArgs: Array<Any>? = null): QueryResult = try {
        //execute the query results will be save in Cursor c
        val c = supportSQLiteDatabase().query(query, bindArgs)
        if (null != c && c.count > 0) {
            QueryResult.Success(c)
        } else
            QueryResult.Error(java.lang.Exception())
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
