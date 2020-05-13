package com.ktvipin27.roomexplorer

import android.database.Cursor
import android.database.SQLException
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
    private val tableNamesAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            arrayListOf<String>()
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
    }
    private val tableNameSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) = displayData()
    }
    private val selectedTableName
        get() = sp_table.selectedItem as String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explorer)
        setSupportActionBar(toolbar)

        sp_table.adapter = tableNamesAdapter
        sp_table.onItemSelectedListener = tableNameSelectedListener

        intent.extras?.let {
            if (it.containsKey(RoomExplorer.KEY_DATABASE_CLASS)) {
                databaseClass = it.get(RoomExplorer.KEY_DATABASE_CLASS) as Class<out RoomDatabase>
            } else {
                throw RuntimeException("No database class passed in the launch Intent.")
            }
            if (it.containsKey(RoomExplorer.KEY_DATABASE_NAME)) {
                databaseName = it.getString(RoomExplorer.KEY_DATABASE_NAME, "")
            } else {
                throw RuntimeException("No database name passed in the launch Intent.")
            }
        } ?: throw RuntimeException("No data passed in the launch Intent.")

        getTableNames()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_explorer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_add -> true
        R.id.action_delete -> true.also { deleteTable() }
        R.id.action_drop -> true.also { dropTable() }
        else -> super.onOptionsItemSelected(item)
    }

    private fun getTableNames() {
        tableNamesAdapter.clear()
        when (
            val queryResult = getData(Queries.GET_TABLE_NAMES)) {
            is QueryResult.Success -> {
                val cursor = queryResult.data
                cursor.moveToFirst()
                do {
                    tableNamesAdapter.add(cursor.getString(0))
                } while (cursor.moveToNext())
                cursor.close()
            }
            is QueryResult.Error -> {
                //TODO handle error
            }
        }
        tableNamesAdapter.notifyDataSetChanged()
        if (!tableNamesAdapter.isEmpty)
            sp_table.setSelection(0)
    }

    private fun displayData() {
        tl.removeAllViews()
        when (val queryResult = getData(Queries.GET_TABLE_DATA + selectedTableName)) {
            is QueryResult.Success -> {
                val cursor = queryResult.data
                tv_record_count.text = getString(R.string.number_of_records, cursor.count)
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

    private fun deleteTable() {
        showAlert(
            getString(R.string.title_delete_table),
            getString(R.string.message_delete_table, selectedTableName),
            getString(android.R.string.ok)
        ) {
            when (val queryResult = execute(Queries.DELETE_TABLE + selectedTableName)) {
                is QueryResult.Success -> {
                    toast(R.string.message_operation_success)
                    displayData()
                }
                is QueryResult.Error -> {
                    toast(
                        getString(
                            R.string.message_operation_failed,
                            queryResult.exception.message
                        )
                    )
                }
            }
        }
    }

    private fun dropTable() {
        showAlert(
            getString(R.string.title_drop_table),
            getString(R.string.message_drop_table, selectedTableName),
            getString(android.R.string.ok)
        ) {
            when (val queryResult = execute(Queries.DROP_TABLE + selectedTableName)) {
                is QueryResult.Success -> {
                    toast(R.string.message_operation_success)
                    if (tableNamesAdapter.count < 2)
                        refreshActivity()
                    else
                        getTableNames()
                }
                is QueryResult.Error -> {
                    toast(
                        getString(
                            R.string.message_operation_failed,
                            queryResult.exception.message
                        )
                    )
                }
            }
        }
    }

    private fun getData(query: String, bindArgs: Array<Any>? = null): QueryResult<Cursor> = try {
        val c = supportSQLiteDatabase().query(query, bindArgs)
        if (null != c) {
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

    private fun execute(query: String): QueryResult<Any> = try {
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
