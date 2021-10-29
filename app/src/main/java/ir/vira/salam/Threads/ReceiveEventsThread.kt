package ir.vira.salam.Threads

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import ir.vira.salam.Contracts.UserContract
import ir.vira.salam.DesignPatterns.Factory.RepositoryFactory
import ir.vira.salam.Enumerations.EventType
import ir.vira.salam.Enumerations.RepositoryType
import ir.vira.salam.Models.MessageModel
import ir.vira.salam.Models.UserModel
import ir.vira.salam.Sockets.EventListener
import ir.vira.salam.Sockets.JoinEventListener
import ir.vira.salam.Sockets.NewMsgEventListener
import ir.vira.utils.Cipher.decryptData
import ir.vira.utils.EncryptionAlgorithm
import ir.vira.utils.decodeToByte
import org.json.JSONException
import org.json.JSONObject
import java.io.DataInputStream
import java.io.IOException
import java.net.ServerSocket
import java.util.HashMap
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class ReceiveEventsThread : Thread() {


    private var serverSocket: ServerSocket? = null
    private var listeners: HashMap<EventType, EventListener>? = null
    @Throws(IOException::class)
    fun setup(port: Int) {
        serverSocket = ServerSocket(port)
        listeners = HashMap()
    }

    override fun run() {
        while (true) {
            try {
                val socket = serverSocket!!.accept()
                val dataInputStream = DataInputStream(socket.getInputStream())
                val data = dataInputStream.readUTF()
                dataInputStream.close()
                socket.close()
                val jsonObject = JSONObject(data)
                when (EventType.valueOf(jsonObject.getString("event"))) {
                    EventType.JOIN -> {
                        val secretKey: SecretKey = SecretKeySpec(
                            (jsonObject.getString("secretKey")).decodeToByte(),
                            EncryptionAlgorithm.AES.name
                        )
                        val ip = secretKey.decryptData(
                            (jsonObject.getString("ip")).decodeToByte(),
                            EncryptionAlgorithm.AES
                        )
                        val name = secretKey.decryptData(
                            (jsonObject.getString("name")).decodeToByte(),
                            EncryptionAlgorithm.AES
                        )
                        val profileStr: String = jsonObject.getString("profile")
                        val profileStream: ByteArray = profileStr.decodeToByte()
                        val profile: Bitmap = BitmapFactory.decodeByteArray(profileStream, 0, profileStream.size)
                        val userModel = UserModel(ip!!, name!!, profile, secretKey)
                        socket.close()
                        (listeners!![EventType.JOIN] as JoinEventListener?)?.join(userModel)
                    }
                    EventType.NEW_MSG -> {
                        val userContract: UserContract = RepositoryFactory.getRepository(RepositoryType.USER_REPO) as UserContract
                        val key: SecretKey = userContract.findUserByIP(jsonObject.getString("ip")).secretKey
                        val ip = jsonObject.getString("ip")
                        val text =  key.decryptData(
                            (jsonObject.getString("text")).decodeToByte(),
                            EncryptionAlgorithm.AES
                        )
                        val messageModel = MessageModel(userContract.findUserByIP(ip), text!!)
                        socket.close()
                        (listeners!![EventType.NEW_MSG] as NewMsgEventListener?)?.newMsg(messageModel)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                run()
            } catch (e: JSONException) {
                e.printStackTrace()
                run()
            }
        }
    }

    fun on(eventType: EventType, eventListener: EventListener) {
        listeners!![eventType] = eventListener
    }
}