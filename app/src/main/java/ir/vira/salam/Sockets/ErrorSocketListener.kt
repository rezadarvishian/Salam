package ir.vira.salam.Sockets

/**
 * When socket can not connect to server error method run and can use message of error
 *
 * @author Ali Ghasemi
 * @see SocketListener
 *
 * @see ir.vira.salam.Threads.ConnectToServerThread
 */
fun interface ErrorSocketListener {
    fun error(message: String?)
}