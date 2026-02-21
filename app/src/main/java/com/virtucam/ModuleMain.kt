package com.virtucam

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import com.virtucam.hooks.CameraHook

/**
 * Xposed Module Entry Point
 * 
 * This class is loaded by the Xposed framework (or LSPatch) when a target app starts.
 * It initializes our camera hooks to intercept and replace the camera feed.
 */
class ModuleMain : IXposedHookLoadPackage {
    
    companion object {
        const val TAG = "VirtuCam"
        
        fun log(message: String) {
            XposedBridge.log("[$TAG] $message")
        }
    }
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        // Skip our own app
        if (lpparam.packageName == "com.virtucam") {
            return
        }
        
        log("Loaded in: ${lpparam.packageName}")
        
        try {
            // Initialize camera hooks
            CameraHook.init(lpparam)
            log("Camera hooks initialized for ${lpparam.packageName}")
        } catch (e: Throwable) {
            log("Failed to initialize hooks: ${e.message}")
            XposedBridge.log(e)
        }
    }
}
