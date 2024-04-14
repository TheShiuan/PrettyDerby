package com.example.prettyderby

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils.SimpleStringSplitter

class CheckAccessibility {
    fun isAccessibilitySettingsOn(context: Context, clazz: Class<out AccessibilityService?>): Boolean {
        var accessibilityEnabled = false    // 判斷設備是否能使用無障礙
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            ) == 1
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
        val mStringColonSplitter = SimpleStringSplitter(':')
        if (accessibilityEnabled) {
            // 獲取無障礙啟用狀態
            val settingValue: String? = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )

            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
                    if (accessibilityService.equals(
                            "${context.applicationContext.packageName}/${clazz.canonicalName}",
                            ignoreCase = true
                        )
                    ) {
                        println("Accessibility Enable!!")
                        return true
                    }
                }
            }
        }
        return false
    }
}