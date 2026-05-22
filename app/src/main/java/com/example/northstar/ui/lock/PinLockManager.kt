package com.example.northstar.ui.lock

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinLockManager @Inject constructor(
    private val application: Application
) : DefaultLifecycleObserver {

    private val prefs = application.getSharedPreferences("northstar_pin", 0)

    private val _isLocked = MutableStateFlow(false)
    val isLocked: StateFlow<Boolean> = _isLocked

    init {
        ProcessLifecycleOwner.Companion.get().lifecycle.addObserver(this)
    }

    // App went to background — lock it
    override fun onStop(owner: LifecycleOwner) {
        if (hasPin()) _isLocked.value = true
    }

    fun unlock() {
        _isLocked.value = false
    }

    fun savePin(pin: String) {
        prefs.edit().putString("pin", pin).apply()
    }

    fun verifyPin(pin: String): Boolean {
        return prefs.getString("pin", null) == pin
    }

    fun hasPin(): Boolean {
        return prefs.getString("pin", null) != null
    }

    fun clearPin() {
        prefs.edit().remove("pin").apply()
    }
}