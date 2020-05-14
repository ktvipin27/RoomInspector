package com.ktvipin27.roomexplorer

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.iterator
import androidx.room.RoomDatabase
import kotlinx.android.synthetic.main.activity_room_explorer_main.*

/**
 * Created by Vipin KT on 08/05/20
 */
internal class RoomExplorerMainActivity : AppCompatActivity() {

    private lateinit var databaseClass: Class<out RoomDatabase>
    private lateinit var databaseName: String
    private lateinit var queryRunner: QueryRunner
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
        setContentView(R.layout.activity_room_explorer_main)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        sp_table.adapter = tableNamesAdapter
        sp_table.onItemSelectedListener = tableNameSelectedListener

        parseIntent()

        getTableNames()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_explorer, menu)
        menu?.iterator()?.forEach {
            it.isVisible = !tableNamesAdapter.isEmpty
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_refresh -> true.also { displayData() }
        R.id.action_add -> true.also { addRow() }
        R.id.action_delete -> true.also { deleteTable() }
        R.id.action_drop -> true.also { dropTable() }
        R.id.action_custom -> true.also { RoomExplorer.query(this, databaseClass, databaseName) }
        else -> super.onOptionsItemSelected(item)
    }

    private fun parseIntent() = intent.extras?.let {

        if (!it.containsKey(RoomExplorer.KEY_DATABASE_CLASS))
            toast(R.string.error_no_db_class).also { finish() }
        if (!it.containsKey(RoomExplorer.KEY_DATABASE_NAME))
            toast(R.string.error_no_db_name).also { finish() }

        databaseClass = it.get(RoomExplorer.KEY_DATABASE_CLASS) as Class<out RoomDatabase>
        databaseName = it.getString(RoomExplorer.KEY_DATABASE_NAME, "")

        queryRunner = QueryRunner(this, databaseClass, databaseName)
    } ?: toast(R.string.error_no_data_passed).also { finish() }

    private fun getTableNames() {
        tableNamesAdapter.clear()
        when (
            val queryResult = queryRunner.getData(Queries.GET_TABLE_NAMES)) {
            is QueryResult.Success -> {
                val cursor = queryResult.data
                cursor.moveToFirst()
                do tableNamesAdapter.add(cursor.getString(0))
                while (cursor.moveToNext())
                cursor.close()
            }
            is QueryResult.Error -> toast(
                getString(
                    R.string.error_operation_failed,
                    queryResult.exception.message
                )
            )
        }
        tableNamesAdapter.notifyDataSetChanged()
        if (!tableNamesAdapter.isEmpty)
            sp_table.setSelection(0)
        invalidateOptionsMenu()
    }

    private fun displayData() {
        tl.removeAllViews()
        when (val queryResult = queryRunner.getData(Queries GET_TABLE_DATA selectedTableName)) {
            is QueryResult.Success -> {
                val cursor = queryResult.data
                tv_record_count.text = getString(R.string.number_of_records, cursor.count)
                val th = TableRow(this)
                cursor.moveToFirst()
                for (i in 0 until cursor.columnCount)
                    TextView(this)
                        .apply {
                            setPadding(10, 10, 10, 10)
                            text = cursor.getColumnName(i)
                            setTextColor(Color.BLACK)
                            typeface = Typeface.DEFAULT_BOLD
                        }.also {
                            th.addView(it)
                        }
                tl.addView(th)
                do {
                    val tr = TableRow(this).apply {
                        setPadding(0, 2, 0, 2)
                    }
                    for (i in 0 until cursor.columnCount)
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
                    tl.addView(tr)
                } while (cursor.moveToNext())
                cursor.close()
            }
            is QueryResult.Error -> toast(
                getString(
                    R.string.error_operation_failed,
                    queryResult.exception.message
                )
            )
        }
    }

    private fun addRow() {
        when (val queryResult = queryRunner.getData(Queries GET_COLUMN_NAMES selectedTableName)) {
            is QueryResult.Success -> {
                val ll = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    val dialogPadding =
                        resources.getDimension(R.dimen.dialog_add_row_padding_vertical).toInt()
                    setPadding(dialogPadding, 0, dialogPadding, 0)
                }
                val topMargin =
                    resources.getDimension(R.dimen.dialog_add_row_item_margin_top).toInt()
                val etPadding = resources.getDimension(R.dimen.dialog_add_row_item_padding).toInt()
                val etList = mutableListOf<EditText>()
                val cursor = queryResult.data
                cursor.moveToFirst()
                do {
                    val columnName = cursor.getString(1)
                    val tv = TextView(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            weight = 1f
                            setMargins(0, topMargin, 0, 0)
                        }
                        text = columnName
                        setTextColor(Color.BLACK)
                    }
                    val et = EditText(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            weight = 1f
                            setMargins(0, topMargin, 0, 0)
                        }
                        setPadding(
                            paddingStart + etPadding,
                            paddingTop,
                            paddingEnd + etPadding,
                            paddingBottom
                        )
                        setTextColor(Color.BLACK)
                        background = getDrawable(R.drawable.bg_spinner)
                    }
                    etList.add(et)
                    val row = LinearLayout(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        orientation = LinearLayout.HORIZONTAL
                        addView(tv)
                        addView(et)
                    }
                    ll.addView(row)
                } while (cursor.moveToNext())
                cursor.close()
                val sv = ScrollView(this).apply { addView(ll) }
                showAlert(
                    getString(R.string.action_add_row),
                    sv,
                    getString(android.R.string.ok)
                ) {
                    val values = etList.map { it.text.toString() }

                    when (val result =
                        queryRunner.execute(Queries INSERT Pair(selectedTableName, values))) {
                        is QueryResult.Success -> {
                            toast(R.string.message_operation_success)
                            displayData()
                        }
                        is QueryResult.Error -> toast(
                            getString(
                                R.string.error_operation_failed,
                                result.exception.message
                            )
                        )
                    }
                }
            }
            is QueryResult.Error -> toast(
                getString(
                    R.string.error_operation_failed,
                    queryResult.exception.message
                )
            )
        }
    }

    private fun deleteTable() {
        showAlert(
            getString(R.string.action_delete_table),
            getString(R.string.message_delete_table, selectedTableName),
            getString(android.R.string.ok)
        ) {
            when (val queryResult = queryRunner.execute(Queries DELETE_TABLE selectedTableName)) {
                is QueryResult.Success -> {
                    toast(R.string.message_operation_success)
                    displayData()
                }
                is QueryResult.Error -> toast(
                    getString(
                        R.string.error_operation_failed,
                        queryResult.exception.message
                    )
                )
            }
        }
    }

    private fun dropTable() {
        showAlert(
            getString(R.string.action_drop_table),
            getString(R.string.message_drop_table, selectedTableName),
            getString(android.R.string.ok)
        ) {
            when (val queryResult = queryRunner.execute(Queries DROP_TABLE selectedTableName)) {
                is QueryResult.Success -> {
                    toast(R.string.message_operation_success)
                    if (tableNamesAdapter.count < 2)
                        refreshActivity()
                    else
                        getTableNames()
                }
                is QueryResult.Error -> toast(
                    getString(
                        R.string.error_operation_failed,
                        queryResult.exception.message
                    )
                )
            }
        }
    }
}
