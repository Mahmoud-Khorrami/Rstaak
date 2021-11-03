package com.example.rstaak.general

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

class BindingAdapter
{
    companion object
    {
        @JvmStatic
        @BindingAdapter("android:setError")
        fun setError(textInputLayout: TextInputLayout, errorMessage: String)
        {
            if(errorMessage.isEmpty()) textInputLayout.error = null
            else textInputLayout.error = errorMessage
        }

        @JvmStatic
        @BindingAdapter("android:addTextChangeListener")
        fun addTextChangeListener(textInputLayout: TextInputLayout, string: String)
        {
            textInputLayout.error = null
        }

    }
}