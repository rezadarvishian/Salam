package ir.vira.salam.Threads

import android.app.Activity
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.vira.network.NetworkInformation
import ir.vira.salam.Adapters.ChatRecyclerAdapter
import ir.vira.salam.Contracts.MessageContract
import ir.vira.salam.Contracts.UserContract
import ir.vira.salam.DesignPatterns.Factory.RepositoryFactory
import ir.vira.salam.Enumerations.RepositoryType
import ir.vira.salam.Models.MessageModel
import ir.vira.salam.R
import ir.vira.utils.Cipher.encryptData
import ir.vira.utils.EncryptionAlgorithm
import ir.vira.utils.encodeToString
import org.json.JSONException
import org.json.JSONObject
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.net.UnknownHostException

class SendEventsThread : Thread() {


    private var context: Context? = null
    private var messageModel: MessageModel? = null
    private var recyclerView: RecyclerView? = null


    fun setMessageModel(
        messageModel: MessageModel?,
        context: Context?,
        recyclerView: RecyclerView?
    ) {
        this.messageModel = messageModel
        this.context = context
        this.recyclerView = recyclerView
    }

    override fun run() {
        super.run()
        var socket: Socket
        var dataOutputStream: DataOutputStream
        val jsonObject = JSONObject()
        val userContract: UserContract =
            RepositoryFactory.getRepository(RepositoryType.USER_REPO) as UserContract
        val networkInformation = NetworkInformation(context)
        try {
            jsonObject.put("event", "NEW_MSG")
            jsonObject.put("ip", messageModel!!.userModel.ip)
            jsonObject.put("text", ((messageModel!!.text).encryptData(EncryptionAlgorithm.AES))?.encodeToString())


            for (userModel in userContract.all) {
                if (userModel.ip != networkInformation.getIpAddress()) {
                    if (userModel.ip == "0.0.0.0") socket = Socket(
                        networkInformation.getServerIpAddress(),
                        context!!.resources.getInteger(R.integer.portNumber)
                    ) else socket =
                        Socket(userModel.ip, context!!.resources.getInteger(R.integer.portNumber))
                    dataOutputStream = DataOutputStream(socket.getOutputStream())
                    dataOutputStream.writeUTF(jsonObject.toString())
                    dataOutputStream.flush()
                    dataOutputStream.close()
                    socket.close()
                }
            }
            val messageContract: MessageContract =
                RepositoryFactory.getRepository(RepositoryType.MESSAGE_REPO) as MessageContract
            messageContract.add(messageModel!!)
            (context as Activity?)?.runOnUiThread(Runnable {
                (recyclerView!!.adapter as ChatRecyclerAdapter?)?.newMsg(messageModel!!)
                (recyclerView!!.layoutManager as LinearLayoutManager?)!!.scrollToPosition(0)
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}