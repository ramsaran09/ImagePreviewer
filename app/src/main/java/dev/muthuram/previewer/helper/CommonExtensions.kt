package dev.muthuram.previewer.helper

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import dev.muthuram.previewer.delegate.ActivityBindingProperty

fun String?.defaultValue(defaultValue: String = "") = this ?: defaultValue

inline fun <reified T : ViewBinding> AppCompatActivity.viewBinding(
    noinline bindingInflater: (LayoutInflater) -> T
) = ActivityBindingProperty(this, bindingInflater)