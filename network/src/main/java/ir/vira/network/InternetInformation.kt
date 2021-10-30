package ir.vira.network

import android.net.wifi.WifiManager
import android.net.ConnectivityManager
import kotlin.Throws
import android.annotation.SuppressLint
import android.content.Context
import ir.vira.network.NetworkInformation
import java.io.IOException
import java.lang.reflect.InvocationTargetException

/**
 * This class for get some information about internet connection .
 *
 * @author Ali Ghasemi
 */
object InternetInformation {


    fun isConnectedToInternet(context: Context): Boolean {
        val networkInformation = NetworkInformation(context)
        return if (networkInformation.isWifiEnabled || networkInformation.isMobileDataEnabled) {
            val runtime = Runtime.getRuntime()
            val process = runtime.exec("ping -c 1 8.8.8.8")
            val exitCode = process.waitFor()
            exitCode == 0
        } else false
    }
}