package ir.vira.salam.Receivers.PendingReceiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ir.vira.salam.Contracts.MessageContract
import ir.vira.salam.Contracts.UserContract
import ir.vira.salam.DesignPatterns.Factory.RepositoryFactory
import ir.vira.salam.Enumerations.RepositoryType
import ir.vira.salam.Models.JoinRequest
import ir.vira.salam.Notifications.JoinNotification
import ir.vira.salam.R
import ir.vira.utils.Cipher.encryptData
import ir.vira.utils.EncryptionAlgorithm
import ir.vira.utils.SecretKey.generateKey
import ir.vira.utils.encodeToString
import ir.vira.utils.getEncodeImage
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket

class AcceptJoinReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(JoinNotification.notifyId)
        val thread = Thread {
            try {
                val jsonObject = JSONObject()
                jsonObject.put("event", "JOIN")
                jsonObject.put("requestStatus", "accept")
                val userContract: UserContract = RepositoryFactory.getRepository(RepositoryType.USER_REPO) as UserContract
                val jsonArrayUsers = JSONArray()
                for (user in userContract.all) {
                    jsonArrayUsers.put(
                        JoinRequest(
                            name = (user.name).encryptData(EncryptionAlgorithm.AES)?.encodeToString().toString(),
                            ip = (user.ip).encryptData(EncryptionAlgorithm.AES)?.encodeToString().toString(),
                            profile = (user.profile).getEncodeImage(),
                            secretKey = (user.secretKey.encoded).encodeToString()
                        )
                    )
                }

                val messageContract: MessageContract = RepositoryFactory.getRepository(RepositoryType.MESSAGE_REPO) as MessageContract
                val jsonArrayMessages = JSONArray()
                for (messageModel in messageContract.all) {

                    val jsonMessage = JSONObject()
                    jsonMessage.put(
                        "ip" ,(messageModel.userModel.ip).encryptData( EncryptionAlgorithm.AES)?.encodeToString().toString())


                    jsonMessage.put(
                        "text" ,(messageModel.text).encryptData( EncryptionAlgorithm.AES)?.encodeToString().toString())

                    jsonArrayMessages.put(jsonMessage)
                }

                var socket: Socket
                jsonObject.put("users", jsonArrayUsers)
                jsonObject.put("messages", jsonArrayMessages)
                jsonObject.put("secretKey", (generateKey(EncryptionAlgorithm.AES)?.encoded)?.encodeToString())


                for (user in userContract.all) {
                    if (user.ip != "0.0.0.0") {
                        socket = Socket(user.ip, context.resources.getInteger(R.integer.portNumber))
                        val dataOutputStream = DataOutputStream(socket.getOutputStream())
                        dataOutputStream.writeUTF(jsonObject.toString())
                        dataOutputStream.flush()
                        dataOutputStream.close()
                        socket.close()
                    }
                }
                /*ChatActivity.getActivity().runOnUiThread {
                    ChatActivity.getTextViewMemberNum()
                        .setText(" تعداد اعضا :" + Utils.toPersian(userContract.all.size.toString() + ""))
                }*/
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