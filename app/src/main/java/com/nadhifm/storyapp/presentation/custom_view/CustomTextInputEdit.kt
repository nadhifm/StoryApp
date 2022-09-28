package com.nadhifm.storyapp.presentation.custom_view

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.nadhifm.storyapp.R

class CustomTextInputEdit : TextInputEditText {

    private lateinit var textInputLayout: TextInputLayout

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        textInputLayout = parent.parent as TextInputLayout
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            val delay: Long = 700
            var last_text_edit: Long = 0
            val handler: Handler = Handler(Looper.getMainLooper())
            var text = ""

            private val input_finish_checker = Runnable {
                if (System.currentTimeMillis() > last_text_edit + delay - 500) {
                    if (inputType == 0x00000081) {
                        if (text.length < 6) {
                            textInputLayout.error = context.getString(R.string.password_must_be_at_6_characters_long)
                        }
                    } else if (inputType == 0x00000021) {
                        if (!Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                            textInputLayout.error = context.getString(R.string.email_format_must_be_valid)
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                textInputLayout.isErrorEnabled = false
                text = s.toString()
                handler.removeCallbacks(input_finish_checker)
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isNotEmpty()) {
                    last_text_edit = System.currentTimeMillis()
                    handler.postDelayed(input_finish_checker, delay)
                }
            }
        })
    }
}