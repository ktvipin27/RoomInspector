package com.ktvipin27.roomexplorer.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ktvipin27.roomexplorer.R
import com.ktvipin27.roomexplorer.query.QueryResult
import com.ktvipin27.roomexplorer.query.QueryRunner
import com.ktvipin27.roomexplorer.util.TableBuilder
import com.ktvipin27.roomexplorer.util.hideKeyboard
import com.ktvipin27.roomexplorer.util.toast
import kotlinx.android.synthetic.main.activity_re_query.*

/**
 * Created by Vipin KT on 14/05/20
 */
internal class REQueryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_re_query)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        btn_submit.setOnClickListener {
            hsv.removeAllViews()
            val query = et_query.text.toString()
            if (query.isEmpty()) return@setOnClickListener
            hideKeyboard()
            et_query.clearFocus()
            executeQuery(query)
        }
    }

    private fun executeQuery(query: String) {
        when (val queryResult = QueryRunner.getData(query)) {
            is QueryResult.Success -> {
                toast(R.string.re_message_operation_success)
                val cursor = queryResult.data
                cursor.moveToFirst()
                val columnNames = arrayListOf<String>()
                for (i in 0 until cursor.columnCount) columnNames.add(cursor.getColumnName(i))
                val rows = mutableListOf<ArrayList<String>>()
                do {
                    val rowValues = arrayListOf<String>()
                    for (i in 0 until cursor.columnCount) rowValues.add(cursor.getString(i))
                    rows.add(rowValues)
                } while (cursor.moveToNext())
                cursor.close()

                TableBuilder.build(columnNames, rows, { }, { }).also { hsv.addView(it) }
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
