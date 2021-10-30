package ir.vira.salam

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import dagger.hilt.android.AndroidEntryPoint
import ir.vira.network.NetworkInformation
import ir.vira.salam.Contracts.MessageContract
import ir.vira.salam.Contracts.UserContract
import ir.vira.salam.DesignPatterns.Factory.RepositoryFactory
import ir.vira.salam.DesignPatterns.Factory.ThreadFactory
import ir.vira.salam.Enumerations.RepositoryType
import ir.vira.salam.Enumerations.ThreadType
import ir.vira.salam.Models.JoinRequest
import ir.vira.salam.Models.MessageModel
import ir.vira.salam.Models.UserModel
import ir.vira.salam.Sockets.ErrorSocketListener
import ir.vira.salam.Sockets.SocketListener
import ir.vira.salam.Threads.ConnectToServerThread
import ir.vira.salam.Utiles.*
import ir.vira.salam.Utiles.ConstVal.IS_ADMIN
import ir.vira.salam.core.BaseActivity
import ir.vira.salam.databinding.MainActivityBinding
import ir.vira.salam.viewModel.MainViewModel
import ir.vira.utils.*
import ir.vira.utils.Cipher.decryptData
import ir.vira.utils.Cipher.encryptData
import ir.vira.utils.SecretKey.generateKey
import kotlinx.coroutines.flow.onEach
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.lang.Exception
import java.net.ServerSocket
import java.net.Socket
import java.util.ArrayList
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@AndroidEntryPoint
class MainActivity : BaseActivity<MainActivityBinding>(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var networkInformation: NetworkInformation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeViewModel()
        networkInformation = NetworkInformation(this)
        binding.mainEditIP.setText(networkInformation.ipAddress)

        binding.mainImageProfile.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setItems(R.array.item_name) { _: DialogInterface?, position: Int ->
                when (position) {
                    0 -> chooseImageFromGallery(resources.getInteger(R.integer.chooseImageFromGallery))
                    1 -> takeImage(resources.getInteger(R.integer.takeImage))
                }
            }
            alertDialog.show()
        }

        binding.mainBtnSendRequest.setOnClickListener {
            if (validateUserInputData()) sendRequest()
        }
    }

    private fun observeViewModel() {

        viewModel.profile.onEach {
            if (it.isNotBlank()) {
                binding.mainImageProfile.setImageBitmap(it.getBitmap())
                binding.mainTextProfile.setInvisible()
            }
        }.observeInLifecycle(this)

        viewModel.profile.onEach {
            if (it.isNotBlank()) binding.mainEditName.setText(it)
        }.observeInLifecycle(this)

    }

    private fun validateUserInputData(): Boolean {

        if (binding.mainEditName.text.trim().toString().isBlank()) {
            binding.mainEditName.error = "لطفا نامی برای خود وارد کنید."
            return false
        }

        if (binding.mainImageProfile.drawable == null) {
            showToast("شما باید یک پروفایل انتخاب کنید !")
            return false
        }

        viewModel.saveUserName(binding.mainEditName.trimString())

        return true
    }


    private fun sendRequest() {
        binding.mainBtnSendRequest.setInvisible()

        val thread = ThreadFactory.getThread(ThreadType.CONNECT_TO_SERVER)

        val socketListener = SocketListener { socket: Socket? ->
            if (socket!!.isConnected) {
                try {

                    val encryptedIp = networkInformation.ipAddress.encryptData( EncryptionAlgorithm.AES)?.encodeToString()
                    val encryptedName = binding.mainEditName.trimString().encryptData( EncryptionAlgorithm.AES)?.encodeToString()
                    val encryptedSecretKey = generateKey(EncryptionAlgorithm.AES)?.encoded?.encodeToString()

                    if (encryptedIp==null||encryptedName==null||encryptedSecretKey==null){
                        showToast("در رمزنگاری اطلاعات مشکلی به وجود آمد لطفا برنامه را ببنید و دوباره باز کنید")
                    }


                    val joinRequest = JoinRequest(
                        event = "JOIN",
                        ip = encryptedIp!!,
                        name = encryptedName!!,
                        secretKey = encryptedSecretKey!!,
                        profile = viewModel.userProfile()
                    )

                    val dataOutputStream = DataOutputStream(socket.getOutputStream())
                    dataOutputStream.writeUTF(joinRequest.toString())
                    dataOutputStream.flush()
                    dataOutputStream.close()
                    socket.close()
                    val serverSocket = ServerSocket(resources.getInteger(R.integer.portNumber))
                    val socketReceived = serverSocket.accept()
                    val dataInputStream = DataInputStream(socketReceived.getInputStream())
                    val jsonObject = JSONObject(dataInputStream.readUTF())
                    dataInputStream.close()
                    socketReceived.close()
                    serverSocket.close()
                    if (jsonObject.getString("requestStatus") == "accept") {
                        val userContract: UserContract = RepositoryFactory.getRepository(RepositoryType.USER_REPO) as UserContract
                        val messageContract: MessageContract = RepositoryFactory.getRepository(RepositoryType.MESSAGE_REPO) as MessageContract
                        val userModels: MutableList<UserModel> = ArrayList<UserModel>()
                        val messageModels: MutableList<MessageModel> = ArrayList<MessageModel>()
                        val jsonArrayUsers: JSONArray = jsonObject.getJSONArray("users")
                        val jsonArrayMessages: JSONArray = jsonObject.getJSONArray("messages")
                        val decodedKey = jsonObject.getString("secretKey").decodeToByte()
                        val secretKey=SecretKeySpec(decodedKey, EncryptionAlgorithm.AES.name)


                        for (i in 0 until jsonArrayUsers.length()) {

                            val decodedIp = jsonArrayUsers.getJSONObject(i).getString("ip").decodeToByte()
                            val ip = secretKey.decryptData(decodedIp,EncryptionAlgorithm.AES)

                            val decodedName = jsonArrayUsers.getJSONObject(i).getString("name").decodeToByte()
                            val name = secretKey.decryptData(decodedName,EncryptionAlgorithm.AES)

                            val decodedProf = jsonArrayUsers.getJSONObject(i).getString("profile").decodeToByte()
                            val profile = BitmapFactory.decodeByteArray(decodedProf, 0, decodedProf.size)

                            val secretKeyForUser = SecretKeySpec(jsonArrayUsers.getJSONObject(i).getString("secretKey").decodeToByte(), EncryptionAlgorithm.AES.name)

                            val userModel = UserModel(ip!!, name!!, profile, secretKeyForUser)

                            userModels.add(userModel)
                        }
                        userContract.addAll(userModels)

                        for (i in 0 until jsonArrayMessages.length()) {

                            val decodedIp = jsonArrayUsers.getJSONObject(i).getString("ip").decodeToByte()
                            val ip = secretKey.decryptData(decodedIp,EncryptionAlgorithm.AES)

                            val decodedText = jsonArrayUsers.getJSONObject(i).getString("text").decodeToByte()
                            val text = secretKey.decryptData(decodedText,EncryptionAlgorithm.AES)

                            val messageModel = MessageModel(userContract.findUserByIP(ip), text!!)
                            messageModels.add(messageModel)
                        }

                        messageContract.addAll(messageModels)

                        val intent = Intent(this,ChatActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.putExtra(IS_ADMIN,false)
                        startActivity(intent)

                    } else {
                        doOnUiThread {
                            binding.mainBtnSendRequest.setVisible()
                            showToast(resources.getString(R.string.msg_admin_did_not_allowed))
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                binding.mainBtnSendRequest.setVisible()
                showToast(resources.getString(R.string.problem_in_connect_to_admin))
            }
        } as (Socket?) -> Unit



        val errorSocketListener = ErrorSocketListener { message: String? ->
            doOnUiThread {
                binding.mainBtnSendRequest.setVisible()
                showToast(resources.getString(R.string.problem_in_connect_to_admin))
            }
        }

        (thread as ConnectToServerThread).setupConnection(
            networkInformation.serverIpAddress,
            resources.getInteger(R.integer.portNumber),
            this,
            socketListener,
            errorSocketListener
        )
        thread.priority = Thread.MAX_PRIORITY
        thread.start()

    }

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == getResources().getInteger(R.integer.chooseImageFromGallery) && data != null) {
        try {
            val profile = MediaStore.Images.Media.getBitmap(contentResolver, data.data)
            binding.mainImageProfile.setImageURI(data.data)
            binding.mainTextProfile.setInvisible()
            putProfileInSharedPreferences(profile)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    } else if (requestCode == getResources().getInteger(R.integer.takeImage) && data != null) {
        try {
            //  val profile = MediaStore.Images.Media.getBitmap(contentResolver, getTempImage())
            val profile = MediaStore.Images.Media.getBitmap(contentResolver, data.data)
            binding.mainImageProfile.setImageURI(data.data)
            binding.mainTextProfile.setInvisible()
            putProfileInSharedPreferences(profile)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

private fun putProfileInSharedPreferences(bitmap: Bitmap) {
    var bitmapImage: Bitmap = bitmap
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmapImage.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream)
    val options: BitmapFactory.Options = BitmapFactory.Options()
    options.inSampleSize = 2
    options.inPurgeable = true
    bitmapImage = BitmapFactory.decodeByteArray(
        byteArrayOutputStream.toByteArray(),
        0,
        byteArrayOutputStream.toByteArray().size,
        options
    )
    viewModel.saveUserImage(bitmapImage.getEncodeImage())
}

}