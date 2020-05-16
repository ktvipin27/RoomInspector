package com.ktvipin.roominspector.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.iterator
import androidx.room.RoomDatabase
import com.ktvipin.roominspector.R
import com.ktvipin.roominspector.RoomInspector
import com.ktvipin.roominspector.query.QueryBuilder
import com.ktvipin.roominspector.query.QueryRunner
import com.ktvipin.roominspector.util.*
import kotlinx.android.synthetic.main.activity_ri_main.*

/**
 * The main activity of the  [RoomInspector].
 *
 * Created by Vipin KT on 08/05/20
 */
internal class RIMainActivity : AppCompatActivity() {

    /**
     * Adapter for table names.
     */
    private val tableNamesAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            arrayListOf<String>()
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
    }

    /**
     * An implementation of [AdapterView.OnItemSelectedListener],
     * will be triggered when user selects the table name.
     */
    private val tableNameSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) = displayData()
    }

    /**
     * Name of the selected table.
     */
    private val selectedTableName
        get() = sp_table.selectedItem as String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ri_main)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        sp_table.adapter = tableNamesAdapter
        sp_table.onItemSelectedListener = tableNameSelectedListener

        parseIntent()
        getTableNames()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_ri_main, menu)
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
        R.id.action_custom -> true.also { RoomInspector.query(this) }
        else -> super.onOptionsItemSelected(item)
    }

    /**
     * Parse the data passed in launch intent and
     * initializes [QueryRunner] if all necessary data is present, else throws error.
     */
    private fun parseIntent() = intent.extras?.let {
        if (!it.containsKey(RoomInspector.KEY_DATABASE_CLASS))
            toast(R.string.ri_error_no_db_class).also { finish() }
        if (!it.containsKey(RoomInspector.KEY_DATABASE_NAME))
            toast(R.string.ri_error_no_db_name).also { finish() }

        val databaseClass = it.get(RoomInspector.KEY_DATABASE_CLASS) as Class<out RoomDatabase>
        val databaseName = it.getString(RoomInspector.KEY_DATABASE_NAME, "")

        QueryRunner.init(applicationContext, databaseClass, databaseName)
    } ?: toast(R.string.ri_error_no_data_passed).also { finish() }

    /**
     * Displays the table names in a spinner.
     */
    private fun getTableNames() {
        tableNamesAdapter.clear()
        QueryRunner.query(QueryBuilder.GET_TABLE_NAMES, {
            it.second.forEach { list ->
                tableNamesAdapter.addAll(list)
            }
        }, {
            toast(getString(R.string.ri_error_operation_failed, it.message))
        })
        if (!tableNamesAdapter.isEmpty)
            sp_table.setSelection(0)
        invalidateOptionsMenu()
    }

    /**
     * Display's the data of the selected table in a table view.
     */
    private fun displayData() {
        hsv.removeAllViews()
        QueryRunner.query(QueryBuilder getAllValues selectedTableName, { result ->
            val columns = result.first
            val rows = result.second
            tv_record_count.text = getString(R.string.ri_label_number_of_records, rows.size)

            TableView(this)
                .create(columns, rows,
                    { updateRow(columns, rows[it]) },
                    { deleteRow(columns, rows[it]) })
                .also { hsv.addView(it) }
        }, {
            tv_record_count.text = ""
            toast(
                getString(
                    R.string.ri_error_operation_failed,
                    it.message
                )
            )
        })
    }

    /**
     * Deletes the table on user confirmation.
     */
    private fun deleteTable() {
        showAlert(
            getString(R.string.ri_title_delete_table),
            getString(R.string.ri_message_delete_table, selectedTableName),
            getString(R.string.ri_action_delete)
        ) {
            QueryRunner.execute(QueryBuilder deleteTable selectedTableName, {
                toast(R.string.ri_message_operation_success)
                displayData()
            }, {
                toast(getString(R.string.ri_error_operation_failed, it.message))
            })
        }
    }

    /**
     * Drops the table on user confirmation.
     */
    private fun dropTable() {
        showAlert(
            getString(R.string.ri_title_drop_table),
            getString(R.string.ri_message_drop_table, selectedTableName),
            getString(R.string.ri_action_drop)
        ) {
            QueryRunner.execute(QueryBuilder dropTable selectedTableName, {
                toast(R.string.ri_message_operation_success)
                if (tableNamesAdapter.count < 2)
                    refreshActivity()
                else
                    getTableNames()
            }, {
                toast(getString(R.string.ri_error_operation_failed, it.message))
            })
        }
    }

    /**
     * Deletes the selected row of table on user confirmation.
     */
    private fun deleteRow(columnNames: List<String>, rowValues: List<String>) {
        showAlert(
            getString(R.string.ri_title_delete_row),
            getString(R.string.ri_message_delete_row, selectedTableName),
            getString(R.string.ri_action_delete)
        ) {
            val query = QueryBuilder.deleteRow(
                selectedTableName,
                columnNames,
                rowValues
            )
            QueryRunner.execute(query, {
                toast(R.string.ri_message_operation_success)
                displayData()
            }, {
                toast(getString(R.string.ri_error_operation_failed, it.message))
            })
        }
    }

    /**
     * Display's a dialog with fields to enter data
     * and
     * adds a row to the selected table with entered values.
     *
     */
    private fun addRow() {
        QueryRunner.query(QueryBuilder getColumnNames selectedTableName, { result ->
            val columns = result.second.map { it[1] }
            val rows = result.second.map { "" }
            val dialogView = DialogView(this, columns, rows)
            showAlert(
                getString(R.string.ri_title_add_row),
                dialogView,
                getString(R.string.ri_action_add)
            ) {
                val query = QueryBuilder.insert(selectedTableName, dialogView.fieldValues)
                QueryRunner.execute(query, {
                    toast(R.string.ri_message_operation_success)
                    displayData()
                }, {
                    toast(getString(R.string.ri_error_operation_failed, it.message))
                })
            }
        }, {
            toast(getString(R.string.ri_error_operation_failed, it.message))
        })
    }

    /**
     * Display's a dialog with values of clicked row
     * and
     * updates the row with new/updated values.
     *
     * @param columnNames list of column names
     * @param rowValues list of values of the clicked row
     */
    private fun updateRow(columnNames: List<String>, rowValues: List<String>) {
        val dialogView = DialogView(this, columnNames, rowValues)
        showAlert(
            getString(R.string.ri_title_update_row),
            dialogView,
            getString(R.string.ri_action_update)
        ) {
            val query = QueryBuilder.updateTable(
                selectedTableName,
                columnNames,
                rowValues,
                dialogView.fieldValues
            )
            QueryRunner.execute(query, {
                toast(R.string.ri_message_operation_success)
                displayData()
            }, {
                toast(getString(R.string.ri_error_operation_failed, it.message))
            })
        }
    }
}
