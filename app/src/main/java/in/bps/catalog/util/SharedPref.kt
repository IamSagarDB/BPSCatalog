package `in`.bps.catalog.util

import android.content.Context
import android.content.SharedPreferences

class SharedPref(context: Context) {
    private  val sharedPreferences : SharedPreferences = context.getSharedPreferences(Constants.sharedPrefName, Context.MODE_PRIVATE)
    private val editor : SharedPreferences.Editor = sharedPreferences.edit();

    companion object {
        private const val IS_LOGGED_IN = "is_logged_in"
    }

    fun clearSharedPref(){
        editor.clear()
        editor.commit()
    }

    var isLoggedIn : String?
    get() = sharedPreferences.getString(IS_LOGGED_IN,"N")
    set(isLoggedIn) {
        editor.putString(IS_LOGGED_IN, isLoggedIn)
        editor.commit()
    }
}