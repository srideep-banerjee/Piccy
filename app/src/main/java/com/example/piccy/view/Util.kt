package com.example.piccy.view

import android.text.Editable
import android.text.TextWatcher
import com.example.piccy.viewmodels.ProfileViewModel
import com.google.android.material.textfield.TextInputEditText

class Util {
    companion object {
        fun addListener(
            textInputEditText: TextInputEditText,
            profileViewModel: ProfileViewModel,
            index: Int
        ) {
            textInputEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    profileViewModel.updateEntryAt(index, s?.toString())
                }

                override fun afterTextChanged(s: Editable?) {

                }

            })
        }
    }
}