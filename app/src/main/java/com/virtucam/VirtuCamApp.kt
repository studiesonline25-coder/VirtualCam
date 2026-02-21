package com.virtucam

import android.app.Application
import android.content.Context

/**
 * VirtuCam Application class
 * Initializes app-wide configurations and provides global context
 */
class VirtuCamApp : Application() {

    companion object {
        private lateinit var instance: VirtuCamApp
        
        fun getContext(): Context = instance.applicationContext
        
        // Shared preferences keys
        const val PREFS_NAME = "virtucam_prefs"
        const val KEY_ENABLED = "enabled"
        const val KEY_IMAGE_URI = "image_uri"
        const val KEY_TARGET_APPS = "target_apps"
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
