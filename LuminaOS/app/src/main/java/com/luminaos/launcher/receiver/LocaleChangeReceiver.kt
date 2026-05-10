package com.luminaos.launcher.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class LocaleChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_LOCALE_CHANGED) {
            // Handle locale change if needed
            // App names will be automatically updated by the system
        }
    }
}
