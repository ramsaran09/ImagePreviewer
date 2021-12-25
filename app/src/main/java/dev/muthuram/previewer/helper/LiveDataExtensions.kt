package dev.muthuram.previewer.helper

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


fun <T> LiveData<T>.observeLiveData(lifecycleOwner: LifecycleOwner, function: (T) -> Unit) {
    this.observe(lifecycleOwner, {
        if (it != null)
            function.invoke(it)
    })
}