package com.ktvipin27.roomexplorer.activity

import android.graphics.Color
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
import com.ktvipin27.roomexplorer.query.QueryRunner
import com.ktvipin27.roomexplorer.util.*
import kotlinx.android.synthetic.main.activity_re_main.*

/**
 * Created by Vipin KT on 08/05/20
 */
internal class REMainActivity : AppCompatActivity() {

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
        TableBuilder.init(this)
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
        R.id.action_custom -> true.also { RoomExplorer.query(this) }
        else -> super.onOptionsItemSelected(item)
    }

    private fun parseIntent() = intent.extras?.let {

        if (!it.containsKey(RoomExplorer.KEY_DATABASE_CLASS))
            toast(R.string.re_error_no_db_class).also { finish() }
        if (!it.containsKey(RoomExplorer.KEY_DATABASE_NAME))
            toast(R.string.re_error_no_db_name).also { finish() }

        val databaseClass = it.get(RoomExplorer.KEY_DATABASE_CLASS) as Class<out RoomDatabase>
        val databaseName = it.getString(RoomExplorer.KEY_DATABASE_NAME, "")

        QueryRunner.init(applicationContext, databaseClass, databaseName)
    } ?: toast(R.string.re_error_no_data_passed).also { finish() }

    private fun getTableNames() {
        tableNamesAdapter.clear()
        QueryRunner.query(QueryBuilder.GET_TABLE_NAMES, {
            it.second.forEach { list ->
                tableNamesAdapter.addAll(list)
            }
        }, {
            toast(getString(R.string.re_error_operation_failed, it.message))
        })
        if (!tableNamesAdapter.isEmpty)
            sp_table.setSelection(0)
        invalidateOptionsMenu()
    }

    private fun displayData() {
        hsv.removeAllViews()
        QueryRunner.query(QueryBuilder getAllValues selectedTableName, { result ->
            val columns = result.first
            val rows = result.second
            tv_record_count.text = getString(R.string.re_label_number_of_records, rows.size)

            TableBuilder.build(columns, rows,
                { updateRow(columns, rows[it]) },
                { deleteRow(columns, rows[it]) })
                .also { hsv.addView(it) }
        }, {
            tv_record_count.text = ""
            toast(
                getString(
                    R.string.re_error_operation_failed,
                    it.message
                )
            )
        })
    }

    private fun deleteRow(columnNames: List<String>, rowValues: List<String>) {
        showAlert(
            getString(R.string.re_title_delete_row),
            getString(R.string.re_message_delete_row, selectedTableName),
            getString(R.string.re_action_delete)
        ) {
            val query = QueryBuilder.deleteRow(
                selectedTableName,
                columnNames,
                rowValues
            )
            QueryRunner.execute(query, {
                toast(R.string.re_message_operation_success)
                displayData()
            }, {
                toast(getString(R.string.re_error_operation_failed, it.message))
            })
        }
    }

    private fun updateRow(columnNames: List<String>, rowValues: List<String>) {
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
            getString(R.string.re_title_update_row),
            sv,
            getString(R.string.re_action_update)
        ) {
            val query = QueryBuilder.updateTable(
                selectedTableName,
                columnNames,
                rowValues,
                etList.map { it.text.toString() }
            )
            QueryRunner.execute(query, {
                toast(R.string.re_message_operation_success)
                displayData()
            }, {
                toast(getString(R.string.re_error_operation_failed, it.message))
            })
        }
    }

    private fun addRow() {
        QueryRunner.query(QueryBuilder getColumnNames selectedTableName, { result ->
            val ll = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                val dialogPadding =
                    resources.getDimension(R.dimen.re_padding_vertical_dialog_add_row)
                        .toInt()
                setPadding(dialogPadding, 0, dialogPadding, 0)
            }
            val topMargin =
                resources.getDimension(R.dimen.re_margin_top_dialog_add_row_item).toInt()
            val etPadding =
                resources.getDimension(R.dimen.re_padding_dialog_add_row_item).toInt()
            val etList = mutableListOf<EditText>()
            val columns = result.second.map { it[1] }
            columns.forEach { columnName ->
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
            }
            val sv = ScrollView(this).apply { addView(ll) }
            showAlert(
                getString(R.string.re_title_add_row),
                sv,
                getString(R.string.re_action_add)
            ) {
                val query =
                    QueryBuilder.insert(
                        selectedTableName,
                        etList.map { it.text.toString() })
                QueryRunner.execute(query, {
                    toast(R.string.re_message_operation_success)
                    displayData()
                }, {
                    toast(getString(R.string.re_error_operation_failed, it.message))
                })
            }
        }, {
            toast(getString(R.string.re_error_operation_failed, it.message))
        })
    }

    private fun deleteTable() {
        showAlert(
            getString(R.string.re_title_delete_table),
            getString(R.string.re_message_delete_table, selectedTableName),
            getString(R.string.re_action_delete)
        ) {
            QueryRunner.execute(QueryBuilder deleteTable selectedTableName, {
                toast(R.string.re_message_operation_success)
                displayData()
            }, {
                toast(getString(R.string.re_error_operation_failed, it.message))
            })
        }
    }

    private fun dropTable() {
        showAlert(
            getString(R.string.re_title_drop_table),
            getString(R.string.re_message_drop_table, selectedTableName),
            getString(R.string.re_action_drop)
        ) {
            QueryRunner.execute(QueryBuilder dropTable selectedTableName, {
                toast(R.string.re_message_operation_success)
                if (tableNamesAdapter.count < 2)
                    refreshActivity()
                else
                    getTableNames()
            }, {
                toast(getString(R.string.re_error_operation_failed, it.message))
            })
        }
    }
}
