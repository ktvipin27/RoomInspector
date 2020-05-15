package com.ktvipin27.roomexplorer.activity

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.iterator
import androidx.room.RoomDatabase
import com.ktvipin27.roomexplorer.R
import com.ktvipin27.roomexplorer.RoomExplorer
import com.ktvipin27.roomexplorer.query.QueryBuilder
import com.ktvipin27.roomexplorer.query.QueryResult
import com.ktvipin27.roomexplorer.query.QueryRunner
import com.ktvipin27.roomexplorer.util.forEach
import com.ktvipin27.roomexplorer.util.refreshActivity
import com.ktvipin27.roomexplorer.util.showAlert
import com.ktvipin27.roomexplorer.util.toast
import kotlinx.android.synthetic.main.activity_re_main.*
import java.util.*

/**
 * Created by Vipin KT on 08/05/20
 */
internal class REMainActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_re_main)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        sp_table.adapter = tableNamesAdapter
        sp_table.onItemSelectedListener = tableNameSelectedListener

        parseIntent()

        getTableNames()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_re_main, menu)
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
        R.id.action_custom -> true.also {
            RoomExplorer.query(
                this,
                databaseClass,
                databaseName
            )
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun parseIntent() = intent.extras?.let {

        if (!it.containsKey(RoomExplorer.KEY_DATABASE_CLASS))
            toast(R.string.re_error_no_db_class).also { finish() }
        if (!it.containsKey(RoomExplorer.KEY_DATABASE_NAME))
            toast(R.string.re_error_no_db_name).also { finish() }

        databaseClass = it.get(RoomExplorer.KEY_DATABASE_CLASS) as Class<out RoomDatabase>
        databaseName = it.getString(RoomExplorer.KEY_DATABASE_NAME, "")

        queryRunner = QueryRunner(
            this,
            databaseClass,
            databaseName
        )
    } ?: toast(R.string.re_error_no_data_passed).also { finish() }

    private fun getTableNames() {
        tableNamesAdapter.clear()
        when (
            val queryResult = queryRunner.getData(QueryBuilder.GET_TABLE_NAMES)) {
            is QueryResult.Success -> {
                val cursor = queryResult.data
                cursor.moveToFirst()
                do tableNamesAdapter.add(cursor.getString(0))
                while (cursor.moveToNext())
                cursor.close()
            }
            is QueryResult.Error -> toast(
                getString(
                    R.string.re_error_operation_failed,
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
        when (val queryResult = queryRunner.getData(QueryBuilder getAllValues selectedTableName)) {
            is QueryResult.Success -> {
                val cursor = queryResult.data
                tv_record_count.text = getString(R.string.re_label_number_of_records, cursor.count)
                val th = TableRow(this)
                cursor.moveToFirst()
                val columnNames = arrayListOf<String>()
                for (i in 0 until cursor.columnCount) {
                    val columnName = cursor.getColumnName(i)
                    columnNames.add(columnName)
                    TextView(this)
                        .apply {
                            setPadding(10, 10, 10, 10)
                            text = columnName
                            setTextColor(Color.BLACK)
                            typeface = Typeface.DEFAULT_BOLD
                        }.also {
                            th.addView(it)
                        }
                }
                tl.addView(th)
                do {
                    val tr = TableRow(this).apply {
                        setPadding(0, 2, 0, 2)
                    }
                    val rowValues = arrayListOf<String>()
                    for (i in 0 until cursor.columnCount) {
                        val value = try {
                            cursor.getString(i)
                        } catch (e: Exception) {
                            ""
                        }
                        rowValues.add(value)
                        TextView(this)
                            .apply {
                                setPadding(10, 10, 10, 10)
                                text = value
                                setTextColor(Color.BLACK)
                                typeface = Typeface.DEFAULT
                            }.also {
                                tr.addView(it)
                            }
                    }
                    tr.setOnClickListener { showUpdateDialog(columnNames, rowValues) }
                    tl.addView(tr)
                } while (cursor.moveToNext())
                cursor.close()
            }
            is QueryResult.Error -> toast(
                getString(
                    R.string.re_error_operation_failed,
                    queryResult.exception.message
                )
            )
        }
    }

    private fun showUpdateDialog(columnNames: ArrayList<String>, rowValues: ArrayList<String>) {
        val ll = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val dialogPadding =
                resources.getDimension(R.dimen.re_padding_vertical_dialog_add_row).toInt()
            setPadding(dialogPadding, 0, dialogPadding, 0)
        }
        val topMargin =
            resources.getDimension(R.dimen.re_margin_top_dialog_add_row_item).toInt()
        val etPadding = resources.getDimension(R.dimen.re_padding_dialog_add_row_item).toInt()
        val etList = mutableListOf<EditText>()

        Pair(columnNames, rowValues).forEach { columnName, value ->
            val tv = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = 1f
                    gravity = Gravity.CENTER_VERTICAL
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
                setText(value)
                setPadding(
                    paddingStart + etPadding,
                    paddingTop,
                    paddingEnd + etPadding,
                    paddingBottom
                )
                setTextColor(Color.BLACK)
                background = getDrawable(R.drawable.re_bg_rounded_corner)
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
        }
        val sv = ScrollView(this).apply { addView(ll) }
        showAlert(
            getString(R.string.re_action_update),
            sv,
            getString(android.R.string.ok)
        ) {
            updateTable(etList.map { it.text.toString() }, columnNames, rowValues)
        }
    }

    private fun updateTable(
        newValues: List<String>,
        columnNames: List<String>,
        oldValues: List<String>
    ) {
        val query = QueryBuilder.updateTable(
            selectedTableName,
            columnNames,
            oldValues,
            newValues
        )
        when (val result = queryRunner.execute(query)) {
            is QueryResult.Success -> {
                toast(R.string.re_message_operation_success)
                displayData()
            }
            is QueryResult.Error -> toast(
                getString(
                    R.string.re_error_operation_failed,
                    result.exception.message
                )
            )
        }
    }

    private fun addRow() {
        when (val queryResult =
            queryRunner.getData(QueryBuilder getColumnNames selectedTableName)) {
            is QueryResult.Success -> {
                val ll = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    val dialogPadding =
                        resources.getDimension(R.dimen.re_padding_vertical_dialog_add_row).toInt()
                    setPadding(dialogPadding, 0, dialogPadding, 0)
                }
                val topMargin =
                    resources.getDimension(R.dimen.re_margin_top_dialog_add_row_item).toInt()
                val etPadding =
                    resources.getDimension(R.dimen.re_padding_dialog_add_row_item).toInt()
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
                        background = getDrawable(R.drawable.re_bg_rounded_corner)
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
                    getString(R.string.re_action_add_row),
                    sv,
                    getString(android.R.string.ok)
                ) {
                    val query =
                        QueryBuilder.insert(selectedTableName, etList.map { it.text.toString() })
                    when (val result = queryRunner.execute(query)) {
                        is QueryResult.Success -> {
                            toast(R.string.re_message_operation_success)
                            displayData()
                        }
                        is QueryResult.Error -> toast(
                            getString(
                                R.string.re_error_operation_failed,
                                result.exception.message
                            )
                        )
                    }
                }
            }
            is QueryResult.Error -> toast(
                getString(
                    R.string.re_error_operation_failed,
                    queryResult.exception.message
                )
            )
        }
    }

    private fun deleteTable() {
        showAlert(
            getString(R.string.re_action_delete_table),
            getString(R.string.re_message_delete_table, selectedTableName),
            getString(android.R.string.ok)
        ) {
            when (val queryResult =
                queryRunner.execute(QueryBuilder deleteTable selectedTableName)) {
                is QueryResult.Success -> {
                    toast(R.string.re_message_operation_success)
                    displayData()
                }
                is QueryResult.Error -> toast(
                    getString(
                        R.string.re_error_operation_failed,
                        queryResult.exception.message
                    )
                )
            }
        }
    }

    private fun dropTable() {
        showAlert(
            getString(R.string.re_action_drop_table),
            getString(R.string.re_message_drop_table, selectedTableName),
            getString(android.R.string.ok)
        ) {
            when (val queryResult = queryRunner.execute(QueryBuilder dropTable selectedTableName)) {
                is QueryResult.Success -> {
                    toast(R.string.re_message_operation_success)
                    if (tableNamesAdapter.count < 2)
                        refreshActivity()
                    else
                        getTableNames()
                }
                is QueryResult.Error -> toast(
                    getString(
                        R.string.re_error_operation_failed,
                        queryResult.exception.message
                    )
                )
            }
        }
    }
}