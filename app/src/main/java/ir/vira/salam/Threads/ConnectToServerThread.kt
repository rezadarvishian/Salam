package ir.vira.salam.Threads


import android.content.Context
import ir.vira.salam.Sockets.SocketListener
import ir.vira.salam.Sockets.ErrorSocketListener
import java.io.IOException
import java.net.Socket

/**
 * This class for connect to server
 *
 * @author Ali Ghasemi
 */
class ConnectToServerThread : Thread() {
    private var host: String? = null
    private var context: Context? = null
    private var port = 0
    private var socketListener: SocketListener? = null
    private var errorSocketListener: ErrorSocketListener? = null
    fun setupConnection(
        host: String?,
        port: Int,
        context: Context?,
        socketListener: SocketListener?,
        errorSocketListener: ErrorSocketListener?
    ) {
        this.host = host
        this.port = port
        this.socketListener = socketListener
        this.errorSocketListener = errorSocketListener
        this.context = context
        priority = MAX_PRIORITY
    }

    override fun run() {
        super.run()
        try {
            val socket = Socket(host, port)
            socketListener!!.connect(socket)
        } catch (e: IOException) {
            e.printStackTrace()
            errorSocketListener!!.error(e.message)
        }
    }
}