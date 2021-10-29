package ir.vira.salam.Receivers.PendingReceiver

import ir.vira.salam.DesignPatterns.Factory.RepositoryFactory.getRepository
import android.content.BroadcastReceiver
import android.content.Intent
import android.app.NotificationManager
import android.content.Context
import ir.vira.salam.Notifications.JoinNotification
import org.json.JSONObject
import ir.vira.salam.Contracts.UserContract
import ir.vira.salam.Enumerations.RepositoryType
import ir.vira.salam.Models.UserModel
import ir.vira.salam.R
import org.json.JSONException
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket

class DeclineJoinReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        val userModel = intent.getSerializableExtra("user") as UserModel?
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(JoinNotification.notifyId)
        val thread = Thread {
            try {
                val userContract = getRepository(RepositoryType.USER_REPO) as UserContract?
                userContract!!.removeUser(userContract.findUserByIP(userModel!!.ip))
                val socket =
                    Socket(userModel.ip, context.resources.getInteger(R.integer.portNumber))
                val jsonObject = JSONObject()
                jsonObject.put("event", "JOIN")
                jsonObject.put("requestStatus", "decline")
                val dataOutputStream = DataOutputStream(socket.getOutputStream())
                dataOutputStream.writeUTF(jsonObject.toString())
                dataOutputStream.flush()
                dataOutputStream.close()
                socket.close()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        thread.priority = Thread.MAX_PRIORITY
        thread.start()
    }
}