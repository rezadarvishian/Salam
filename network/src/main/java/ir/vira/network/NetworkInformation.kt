package ir.vira.network

import android.net.wifi.WifiManager
import android.net.ConnectivityManager
import kotlin.Throws
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.text.format.Formatter
import ir.vira.network.NetworkInformation
import java.lang.reflect.InvocationTargetException

/**
 * This class for get some information about network like ip address or server ip address and etc .
 *
 * @author Ali Ghasemi
 */

class NetworkInformation(context: Context) {


    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val ipAddress: String
        get() = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
    val serverIpAddress: String
        get() = Formatter.formatIpAddress(wifiManager.dhcpInfo.serverAddress)
    val isConnectedToNetwork: Boolean
        get() = ipAddress != "0.0.0.0"


    val isMobileDataEnabled: Boolean
        get() {
            @SuppressLint("DiscouragedPrivateApi")
            val method = connectivityManager.javaClass.getDeclaredMethod("getMobileDataEnabled")
            method.isAccessible = true
            return method.invoke(connectivityManager) as Boolean
        }


    val isWifiEnabled: Boolean
        get() = wifiManager.isWifiEnabled


    val isWifiAccessPointEnabled: Boolean
        get() {
            val method = wifiManager.javaClass.getDeclaredMethod("isWifiApEnabled")
            method.isAccessible = true
            return method.invoke(wifiManager) as Boolean
        }

}