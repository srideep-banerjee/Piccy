package com.example.piccy.view

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText

class Util {
    companion object {

        fun TextInputEditText.setTextListener(onTextChange: (String) -> Unit) {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    onTextChange(s?.toString() ?: "")
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })
        }
    }
}