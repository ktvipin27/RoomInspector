package com.ktvipin27.roominspector.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ktvipin27.roominspector.R
import com.ktvipin27.roominspector.query.QueryRunner
import com.ktvipin27.roominspector.util.TableBuilder
import com.ktvipin27.roominspector.util.hideKeyboard
import com.ktvipin27.roominspector.util.toast
import kotlinx.android.synthetic.main.activity_ri_query.*

/**
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

    private fun executeQuery(query: String) {
        QueryRunner.query(query, { result ->
            toast(R.string.ri_message_operation_success)
            TableBuilder
                .build(result.first, result.second, { }, { })
                .also { hsv.addView(it) }
        }, {
            toast(getString(R.string.ri_error_operation_failed, it.message))
        })
    }
}
