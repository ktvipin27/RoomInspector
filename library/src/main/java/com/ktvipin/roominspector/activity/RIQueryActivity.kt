package com.ktvipin.roominspector.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ktvipin.roominspector.R
import com.ktvipin.roominspector.query.QueryRunner
import com.ktvipin.roominspector.util.TableBuilder
import com.ktvipin.roominspector.util.hideKeyboard
import com.ktvipin.roominspector.util.toast
import kotlinx.android.synthetic.main.activity_ri_query.*

/**
 * [AppCompatActivity] for executing custom query.
 *
 * Created by Vipin KT on 14/05/20
 */
internal class RIQueryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ri_query)
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

    /**
     * Executes the given [query] and displays the result in a table.
     *
     * @param query SQL query
     */
    private fun executeQuery(query: String) = QueryRunner.query(query, { result ->
        toast(R.string.ri_message_operation_success)
        TableBuilder
            .build(result.first, result.second, { }, { })
            .also { hsv.addView(it) }
    }, {
        toast(getString(R.string.ri_error_operation_failed, it.message))
    })
}
