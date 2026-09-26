package com.example.data.cloud

import android.content.Context

class GuestSession(context: Context) {
    private val prefs = context.getSharedPreferences("codenest_session", Context.MODE_PRIVATE)

    fun isGuest(): Boolean = prefs.getBoolean("guest", false)

    fun setGuest(value: Boolean) {
        prefs.edit().putBoolean("guest", value).apply()
    }
}
