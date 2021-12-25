package dev.muthuram.previewer.delegate

import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ActivityBindingProperty<T : ViewBinding>(
    private val activity: AppCompatActivity,
    private val bindingInflater: (LayoutInflater) -> T,
) : ReadOnlyProperty<AppCompatActivity, T>, LifecycleObserver {

    private var binding: T? = null

    init {
        activity.lifecycle.addObserver(this)
    }

    override fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T {
        if (binding == null) {
            binding = bindingInflater(thisRef.layoutInflater)
        }
        return binding!!
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        if(binding == null){
            binding = bindingInflater(activity.layoutInflater)
        }
        Log.d("ActivityBindingProperty", "onCreate: ${binding?.root}")
        activity.setContentView(binding!!.root)
        activity.lifecycle.removeObserver(this)
    }
}