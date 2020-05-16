package com.ktvipin.roominspector.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.ktvipin.roominspector.R

/**
 * Created by Vipin KT on 15/05/20
 */
@SuppressLint("ViewConstructor")
class DialogView(
    context: Context,
    private val columnNames: List<String>,
    private val values: List<String> = emptyList()
) : ScrollView(context) {
    private val editTextTopMargin: Int by lazy {
        resources.getDimension(R.dimen.ri_margin_top_dialog_add_row_item).toInt()
    }
    private val etPadding: Int by lazy {
        resources.getDimension(R.dimen.ri_padding_dialog_add_row_item).toInt()
    }
    private val dialogPadding: Int by lazy {
        resources.getDimension(R.dimen.ri_padding_vertical_dialog_add_row).toInt()
    }
    private val etBackground by lazy {
        ContextCompat.getDrawable(context, R.drawable.ri_bg_rounded_corner)
    }
    private val etList = mutableListOf<EditText>()

    private val mainLayout: LinearLayout by lazy {
        LinearLayout(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
            setPadding(dialogPadding, 0, dialogPadding, 0)
        }
    }

    val fieldValues
        get() = etList.map { it.text.toString() }

    init {
        setUp()
    }

    private fun setUp() {
        Pair(columnNames, values).forEach { columnName, value ->
            val tv = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = 1f
                    setMargins(0, editTextTopMargin, 0, 0)
                }
                text = columnName
                setTextColor(Color.BLACK)
            }
            val et = EditText(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = 1f
                    setMargins(0, editTextTopMargin, 0, 0)
                }
                setPadding(
                    paddingStart + etPadding,
                    paddingTop,
                    paddingEnd + etPadding,
                    paddingBottom
                )
                setTextColor(Color.BLACK)
                background = etBackground
                setText(value)
            }
            etList.add(et)
            val row = LinearLayout(context).apply {
                layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.HORIZONTAL
                addView(tv)
                addView(et)
            }
            mainLayout.addView(row)
        }
        addView(mainLayout)
    }
}