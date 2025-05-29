package com.runvpn.app.android.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import co.touchlab.kermit.Logger
import com.runvpn.app.android.BuildConfig
import com.runvpn.app.data.device.data.models.device.register.ApplicationInfo
import com.runvpn.app.data.device.data.models.device.register.CustomData
import com.runvpn.app.data.device.data.models.device.register.DeviceInfo
import com.runvpn.app.data.device.data.models.device.register.Hardware
import com.runvpn.app.data.device.data.models.device.register.Software
import java.lang.reflect.Field
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

object AndroidUtils {

    fun getDeviceInfo(context: Context): DeviceInfo {
        return DeviceInfo(
            guid = getAndroidId(context),
            timezone = TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT),
            language = Locale.getDefault().language,
            pushToken = "firebase not implemented yet",
            source = BuildConfig.APP_SOURCE,
            referrer = "referrer not implemented yet",
            hardware = getHardwareInfo(),
            software = getSoftWareInfo(),
            customData = getCustomData(context),
            application = ApplicationInfo(
                code = BuildConfig.APPLICATION_ID,
                source = BuildConfig.APP_SOURCE
            ),
            applicationPackageName = context.packageName
        )
    }

    @SuppressLint("HardwareIds")
    private fun getAndroidId(context: Context): String {
        return try {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (_: Exception) {
            UUID.randomUUID().toString()
        }
    }

    private fun getCustomData(context: Context): CustomData {
        val metrics: DisplayMetrics = context.resources.displayMetrics
        return CustomData(
            networkOperator = "mobile",
            screenDensityDpi = metrics.densityDpi,
            screenHeightPx = metrics.heightPixels,
            screenWidthPx = metrics.widthPixels
        )
    }

    private fun getHardwareInfo() = Hardware(
        brand = Build.BRAND,
        manufacture = Build.MANUFACTURER,
        name = Build.DEVICE,
        productName = Build.PRODUCT,
        marketName = "" /** fill by backend*/
    )

    private fun getSoftWareInfo(): Software {
        val builder = StringBuilder()
        builder.append("android : ").append(Build.VERSION.RELEASE)

        val fields: Array<Field> = Build.VERSION_CODES::class.java.fields
        var fieldName = ""
        for (field in fields) {
            fieldName = field.name
            var fieldValue = -1
            try {
                fieldValue = field.getInt(Any())
            } catch (e: IllegalArgumentException) {
                Logger.e(e) { "error" }
            } catch (e: IllegalAccessException) {
                Logger.e(e) { "error" }
            } catch (e: NullPointerException) {
                Logger.e(e) { "error" }
            }
            if (fieldValue == Build.VERSION.SDK_INT) {
                break
            }
        }
        return Software(
            versionName = fieldName,
            versionCode = Build.VERSION.SDK_INT.toString(),
            name = Build.VERSION.RELEASE,
            platformName = "Android"
        )

    }

    fun getCachePath(context: Context): String {
        val s = context.cacheDir.toString()
        return s.substring(0, s.length - if (s.endsWith("/")) 1 else 0) + "/"
    }

    fun getVersionCode(context: Context?): Int {
        if (context != null) {
            try {
                return context.packageManager.getPackageInfo(context.packageName, 0).versionCode
            } catch (ignored: PackageManager.NameNotFoundException) {
            }
        }
        return 0
    }

}

