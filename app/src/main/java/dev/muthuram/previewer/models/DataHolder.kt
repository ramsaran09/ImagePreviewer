package dev.muthuram.previewer.models

class DataHolder<T> {
    private var value: T? = null

    fun set(value: T) {
        this.value = value
    }

    fun get(): T? = value

    fun getOrDefault(default: T): T = value.takeIf { it != null } ?: default

    fun getOrThrow(): T {
        return value ?: throw NullPointerException("DataHolder: get on null value")
    }
}