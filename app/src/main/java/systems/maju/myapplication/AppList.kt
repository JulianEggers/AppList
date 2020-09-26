package systems.maju.myapplication

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.pm.PackageInfoCompat
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


data class App(
    val packageName: String,
    val type: AppType? = null,
    val appName: String? = null,
    var website: String? = null,
    var versionCode: Long? = null,
    var version: String? = null,
    var requiredAndroid: Int? = null,
    var requiredVersionCode: Long? = null
)

object AppList {

    private val TAG: String = "AppList"

    fun getList(context: Context, appType: AppType): MutableList<App> {

        val items = mutableListOf<App>()

        for (element: JSONObject in getJsonList(context, appType)) {
            var packageName: String
            var type: AppType

            try {
                packageName = element.getString("package")
            } catch (jsonException: JSONException) {
                Log.e(TAG, "Skipping element from json -> package not found:/n$element")
                continue
            } catch (illegalArgumentException: IllegalArgumentException) {
                Log.e(TAG, "Skipping element from json -> package not valid:/n$element")
                continue
            }

            try {

                val typeString: String = element.getString("type")
                try {
                    type = AppType.valueOf(typeString)
                } catch (illegalArgumentException: IllegalArgumentException) {
                    Log.e(
                        TAG,
                        "Skipping $packageName -> \"type\": \"$typeString\" in json not valid"
                    )
                    continue
                }
            } catch (jsonException: JSONException) {
                Log.e(TAG, "Skipping $packageName -> required attribute 'type' not found  in json ")
                continue
            }

            val app = App(packageName = packageName, type = type)

            try {
                app.website = element.getString("website")
            } catch (jsonException: JSONException) {
            }

            try {
                app.requiredAndroid = element.getInt("required_android")
            } catch (jsonException: JSONException) {
            }

            try {
                app.requiredVersionCode = element.getLong("required_version_code")
            } catch (jsonException: JSONException) {
            }
            items.add(app)
        }

        return items
    }

    private fun getJsonList(
        context: Context,
        appType: AppType
    ): JSONArray {
        val jsonStream = when (appType) {
            AppType.AUTO -> context.resources.openRawResource(R.raw.auto_app_list)
            AppType.MANUAL -> context.resources.openRawResource(R.raw.manual_app_list)
        }

        try {
            val streamReader = BufferedReader(InputStreamReader(jsonStream, "UTF-8"))
            val responseStrBuilder = StringBuilder()
            var inputStr: String?
            while (streamReader.readLine().also { inputStr = it } != null)
                responseStrBuilder.append(inputStr)

            return JSONArray(responseStrBuilder.toString())

        } catch (e: IOException) {
        } catch (e: JSONException) {
        }
        return JSONArray()
    }

    fun getInstalledApps(context: Context): MutableList<App> {
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val pkgAppsList = context.packageManager.queryIntentActivities(mainIntent, 0)

        val allApps: MutableList<App> = mutableListOf()

        for (app in pkgAppsList) {
            try {
                val packageName = app.activityInfo.packageName
                val appName = context.packageManager.getApplicationLabel(
                    context.packageManager.getApplicationInfo(
                        packageName, PackageManager.GET_META_DATA
                    )
                ) as String

                val packageInfo = context.packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_ACTIVITIES
                )

                val versionCode = PackageInfoCompat.getLongVersionCode(packageInfo)

                val appItem =
                    App(packageName = packageName, appName = appName, version = packageInfo.versionName, versionCode = versionCode)

                if (!allApps.contains(appItem))
                    allApps.add(appItem)

            } catch (ignore: Exception) {
                Log.e("Exception", "$ignore")
            }
        }
        return allApps
    }
}

operator fun JSONArray.iterator(): Iterator<JSONObject> {
    class JsonArrayIterator : Iterator<JSONObject> {
        var index = -1

        override fun hasNext(): Boolean {
            return try {
                getJSONObject(index + 1)
                true
            } catch (e: JSONException) {
                false
            }
        }

        override fun next(): JSONObject {
            index++
            return getJSONObject(index)
        }
    }

    return JsonArrayIterator()
}
