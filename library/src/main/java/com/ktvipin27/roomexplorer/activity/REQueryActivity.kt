package com.ktvipin27.roomexplorer.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ktvipin27.roomexplorer.R
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
        QueryRunner.query(query, { result ->
            toast(R.string.re_message_operation_success)
            TableBuilder
                .build(result.first, result.second, { }, { })
                .also { hsv.addView(it) }
        }, {
            toast(getString(R.string.re_error_operation_failed, it.message))
        })
    }
}
