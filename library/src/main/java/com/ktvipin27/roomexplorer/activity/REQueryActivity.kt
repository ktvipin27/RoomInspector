package com.ktvipin27.roomexplorer.activity

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.RoomDatabase
import com.ktvipin27.roomexplorer.R
import com.ktvipin27.roomexplorer.RoomExplorer
import com.ktvipin27.roomexplorer.RoomExplorer.KEY_DATABASE_CLASS
import com.ktvipin27.roomexplorer.query.QueryResult
import com.ktvipin27.roomexplorer.query.QueryRunner
import com.ktvipin27.roomexplorer.util.hideKeyboard
import com.ktvipin27.roomexplorer.util.toast
import kotlinx.android.synthetic.main.activity_re_query.*

/**
 * Created by Vipin KT on 14/05/20
 */
internal class REQueryActivity : AppCompatActivity() {

    private lateinit var queryRunner: QueryRunner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_re_query)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        intent.extras?.let {
            queryRunner = QueryRunner(
                this,
                it.get(KEY_DATABASE_CLASS) as Class<out RoomDatabase>,
                it.getString(RoomExplorer.KEY_DATABASE_NAME, "")
            )
        } ?: finish()
        btn_submit.setOnClickListener {
            tl.removeAllViews()
            val query = et_query.text.toString()
            if (query.isEmpty()) return@setOnClickListener
            hideKeyboard()
            et_query.clearFocus()
            executeQuery(query)
        }
    }

    private fun executeQuery(query: String) {
        when (val queryResult = queryRunner.getData(query)) {
            is QueryResult.Success -> {
                toast(R.string.re_message_operation_success)
                val cursor = queryResult.data
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
                    R.string.re_error_operation_failed,
                    queryResult.exception.message
                )
            )
        }
    }
}
