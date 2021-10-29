package ir.vira.salam.DesignPatterns.Singleton

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(@ApplicationContext context: Context){

    private var preferences=context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)

    private val FIRST_RUN = Pair("isFirstRun", true)
    private val SHARED_KEY_ENTER = Pair("isUserEnter", false)
    private val PRO_FILE = Pair("profile", "")
    private val USERNAME = Pair("username", "")


    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var isFirstRun: Boolean
        get() = preferences.getBoolean(FIRST_RUN.first, FIRST_RUN.second)
        set(value) = preferences.edit {
            it.putBoolean(FIRST_RUN.first, value)
        }


    var sharedKey: Boolean
        get() = preferences.getBoolean(SHARED_KEY_ENTER.first, SHARED_KEY_ENTER.second)
        set(value) = preferences.edit {
            it.putBoolean(SHARED_KEY_ENTER.first, value)
        }

    var profile: String
        get() = preferences.getString(PRO_FILE.first, PRO_FILE.second)?:""
        set(value) = preferences.edit {
            it.putString(PRO_FILE.first, value)
        }

    var userName: String
        get() = preferences.getString(USERNAME.first, USERNAME.second)?:""
        set(value) = preferences.edit {
            it.putString(USERNAME.first, value)
        }



}