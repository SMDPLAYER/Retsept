package uz.smd.retsept.data

import android.content.Context
import android.content.SharedPreferences



object Prefs {
    private const val NAME = "wdwdefdweferfgergrtgrt"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    // list of app specific preferences
    private val IS_FIRST_RUN_PREF = Pair("is_first_run", false)

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    /**
     * SharedPreferences extension function, so we won't need to call edit() and apply()
     * ourselves on every SharedPreferences operation.
     */
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var url:String
    get() = preferences.getString("url", "")?:""
    set(value) = preferences.edit { it.putString("url", value) }

    var isFirstLaunch:Int
    get() = preferences.getInt("isFirstLaunch", -1)?:-1
    set(value) = preferences.edit { it.putInt("isFirstLaunch", value) }


}

