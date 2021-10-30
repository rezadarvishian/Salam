package ir.vira.salam

import android.os.Bundle
import android.os.PersistableBundle
import androidx.recyclerview.widget.LinearLayoutManager
import ir.vira.network.NetworkInformation
import ir.vira.salam.Adapters.ChatRecyclerAdapter
import ir.vira.salam.Contracts.MessageContract
import ir.vira.salam.Contracts.UserContract
import ir.vira.salam.DesignPatterns.Factory.RepositoryFactory
import ir.vira.salam.DesignPatterns.Factory.ThreadFactory
import ir.vira.salam.Enumerations.EventType
import ir.vira.salam.Enumerations.RepositoryType
import ir.vira.salam.Enumerations.ThreadType
import ir.vira.salam.Models.MessageModel
import ir.vira.salam.Models.UserModel
import ir.vira.salam.Notifications.JoinNotification
import ir.vira.salam.Repositories.MessageRepository
import ir.vira.salam.Sockets.JoinEventListener
import ir.vira.salam.Sockets.NewMsgEventListener
import ir.vira.salam.Threads.ReceiveEventsThread
import ir.vira.salam.Threads.SendEventsThread
import ir.vira.salam.Utiles.ConstVal.IS_ADMIN
import ir.vira.salam.Utiles.doOnUiThread
import ir.vira.salam.Utiles.trimString
import ir.vira.salam.core.BaseActivity
import ir.vira.salam.databinding.ChatActivityBinding
import ir.vira.utils.EncryptionAlgorithm
import ir.vira.utils.SecretKey.generateKey
import ir.vira.utils.getBitmap
import ir.vira.utils.toPersian
import java.io.IOException

class ChatActivity : BaseActivity<ChatActivityBinding>(R.layout.activity_chat) {


    private lateinit var receiverThread: ReceiveEventsThread
    private lateinit var senderThread: SendEventsThread
    private lateinit var userContract: UserContract
    private var joinEventListener: JoinEventListener? = null
    private lateinit var chatRecyclerAdapter: ChatRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        initChatRecyclerView()
        getDataFromBundle()
        setUpThreads()
        binding.inputLayout.chatImageSend.setOnClickListener { sendMessage() }
    }

    private fun initChatRecyclerView(){
        val messageContract: MessageContract = MessageRepository.instance as MessageContract
        val network = NetworkInformation(this)
        chatRecyclerAdapter = ChatRecyclerAdapter(messageContract.all.toMutableList(), network)
        binding.chatRecycler.adapter = chatRecyclerAdapter
    }

    private fun getDataFromBundle(){
        if (intent.getBooleanExtra(IS_ADMIN, false)) {
            val networkInformation = NetworkInformation(this)
            val profile = getBitmap(R.drawable.ic_admin)
            userContract.add(
                UserModel(
                    networkInformation.ipAddress,
                    "Admin",
                    profile,
                    generateKey(EncryptionAlgorithm.AES)!!
                )
            )
            initializeAdminEvents()
        } else {
            initializeClientEvents()
        }
    }

    private fun setUpThreads(){
        try {
            receiverThread = ThreadFactory.getThread(ThreadType.RECEIVER_EVENT) as ReceiveEventsThread
            userContract = RepositoryFactory.getRepository(RepositoryType.USER_REPO) as UserContract
            receiverThread.setup(resources.getInteger(R.integer.portNumber))
            receiverThread.priority = Thread.MAX_PRIORITY
            receiverThread.start()


            binding.chatToolbar.chatTextMemberNum.setText((" تعداد اعضا :" + userContract.all.size).toPersian())
            receiverThread.on(EventType.JOIN, joinEventListener!!)
            receiverThread.on(
                EventType.NEW_MSG,
                object : NewMsgEventListener {
                    override fun newMsg(messageModel: MessageModel?) {
                        doOnUiThread {
                            chatRecyclerAdapter.newMsg(messageModel!!)
                            (binding.chatRecycler.layoutManager as LinearLayoutManager?)!!.scrollToPosition(
                                0
                            )
                        }
                    }
                })
        } catch (e: IOException) {
            e.printStackTrace()
            finish()
        }
    }

    private fun initializeAdminEvents() {
        joinEventListener = object : JoinEventListener {
            override fun join(userModel: UserModel?) {
                val userContract: UserContract =
                    RepositoryFactory.getRepository(RepositoryType.USER_REPO) as UserContract
                userContract.add(userModel!!)
                val joinNotification = JoinNotification()
                joinNotification.showNotification(userModel, this@ChatActivity)
            }
        }
    }

    private fun initializeClientEvents() {
        joinEventListener = object : JoinEventListener {
            override fun join(userModel: UserModel?) {
                val userContract: UserContract =
                    RepositoryFactory.getRepository(RepositoryType.USER_REPO) as UserContract
                userContract.add(userModel!!)
                binding.chatToolbar.chatTextMemberNum.text = (" تعداد اعضا :" + userContract.all.size).toPersian()
            }
        }
    }

    private fun sendMessage(){
        val message = binding.inputLayout.chatEditMessage.trimString()
        if (message.isNotBlank()) {
            senderThread = ThreadFactory.getThread(ThreadType.SENDER_EVENT) as SendEventsThread
            val networkInformation = NetworkInformation(this)
            senderThread.setMessageModel(
                MessageModel(
                    userContract.findUserByIP(networkInformation.ipAddress),
                    message),
                this,
                binding.chatRecycler
            )
            senderThread.priority = Thread.MAX_PRIORITY
            senderThread.start()
            binding.inputLayout.chatEditMessage.setText("")
        }
    }

}