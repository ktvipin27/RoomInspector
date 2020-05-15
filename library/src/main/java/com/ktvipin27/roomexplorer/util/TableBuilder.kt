package com.ktvipin27.roomexplorer.util

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.ktvipin27.roomexplorer.R

/**
 * Created by Vipin KT on 15/05/20
 */
internal object TableBuilder {

    lateinit var context: Context
    private val tableRowMinHeight by lazy {
        context.resources.getDimension(R.dimen.re_min_height_table_row).toInt()
    }
    private val tableRowBackground by lazy {
        ContextCompat.getColor(
            context,
            R.color.re_color_table_row
        )
    }

    fun init(context: Context) {
        this.context = context
    }

    private fun tableHeader(text: String) = TextView(context)
        .apply {
            minHeight = tableRowMinHeight
            gravity = Gravity.CENTER_VERTICAL
            setPadding(10, 10, 10, 10)
            this.text = text
            setTextColor(Color.BLACK)
            typeface = Typeface.DEFAULT_BOLD
        }

    private fun tableTuple(text: String) = TextView(context)
        .apply {
            minHeight = tableRowMinHeight
            gravity = Gravity.CENTER_VERTICAL
            setPadding(10, 10, 10, 10)
            this.text = text
            setTextColor(Color.BLACK)
            typeface = Typeface.DEFAULT
        }

    private fun tableRow(values: List<String>, isHeader: Boolean = false) =
        TableRow(context).apply {
            setPadding(0, 2, 0, 2)
            values.forEach {
                if (isHeader)
                    addView(tableHeader(it))
                else
                    addView(tableTuple(it))
            }
        }

    fun build(
        columnNames: List<String>,
        rows: List<List<String>>,
        onClickAction: (pos: Int) -> Unit,
        onLongClickAction: (pos: Int) -> Unit
    ) =
        TableLayout(context).apply {
            addView(tableRow(columnNames, true))
            rows.forEachIndexed { index, list ->
                val tableRow = tableRow(list, false).apply {
                    setOnClickListener { onClickAction(index) }
                    setOnLongClickListener {
                        onLongClickAction(index)
                        true
                    }
                    if (index % 2 != 0)
                        setBackgroundColor(tableRowBackground)
                }
                addView(tableRow)
            }
        }
}