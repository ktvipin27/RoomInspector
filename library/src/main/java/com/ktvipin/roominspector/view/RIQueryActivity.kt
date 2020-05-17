/*
 * Copyright 2020 Vipin KT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ktvipin.roominspector.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ktvipin.roominspector.R
import com.ktvipin.roominspector.query.QueryRunner
import com.ktvipin.roominspector.util.hideKeyboard
import com.ktvipin.roominspector.util.toast
import kotlinx.android.synthetic.main.activity_ri_query.*

/**
 * [AppCompatActivity] for executing custom query.
 *
 * Created by Vipin KT on 14/05/20
 */
class RIQueryActivity : AppCompatActivity() {

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
        TableView(this)
            .create(result.first, result.second, { }, { })
            .also { hsv.addView(it) }
    }, {
        toast(getString(R.string.ri_error_operation_failed, it.message))
    })
}
