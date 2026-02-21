# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /sdk/tools/proguard/proguard-android.txt

# Keep Xposed classes
-keep class de.robv.android.xposed.** { *; }
-keep class com.virtucam.hooks.** { *; }

# Keep module class
-keep class com.virtucam.ModuleMain { *; }
