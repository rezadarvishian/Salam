package ir.vira.salam.DesignPatterns.Factory

import ir.vira.salam.Enumerations.ThreadType
import ir.vira.salam.Threads.ConnectToServerThread
import ir.vira.salam.Threads.ReceiveEventsThread
import ir.vira.salam.Threads.SendEventsThread
import ir.vira.salam.Enumerations.RepositoryType
import ir.vira.salam.Repositories.UserRepository
import ir.vira.salam.Repositories.MessageRepository
import java.lang.Exception

object ThreadFactory {
    fun getThread(threadType: ThreadType?): Thread? {
        return when (threadType) {
            ThreadType.CONNECT_TO_SERVER -> ConnectToServerThread()
            ThreadType.RECEIVER_EVENT -> ReceiveEventsThread()
            ThreadType.SENDER_EVENT -> SendEventsThread()
            else -> {
                val exception = Exception("ThreadType invalid")
                exception.printStackTrace()
                null
            }
        }
    }
}