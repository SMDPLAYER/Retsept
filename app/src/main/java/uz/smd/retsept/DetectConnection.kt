package uz.smd.retsept

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AlertDialog

/**
 * Created by Siddikov Mukhriddin on 23/11/22
 */


fun isInternetAvailable(context: Context): Boolean {
    var result = false
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.run {
            connectivityManager.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }

            }
        }
    }

    return result
}

//object DetectConnection {
//    fun checkInternetConnection(context: Context): Boolean {
//        val con_manager =
//            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        return (con_manager.activeNetworkInfo != null && con_manager.activeNetworkInfo!!.isAvailable
//                && con_manager.activeNetworkInfo!!.isConnected)
//    }
//}
fun showAlert(activity: Activity, link: String) {
    val dialog = AlertDialog.Builder(activity)
    dialog.setCancelable(false)
    dialog.setTitle("Нет соединения с интернетом")
    dialog.setPositiveButton("Повторить") { _, _ ->
        WBActivity.openWB(activity, link)
    }
    dialog.create().show()
}