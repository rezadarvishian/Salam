package ir.vira.salam.Repositories


import ir.vira.salam.Contracts.MessageContract
import ir.vira.salam.Models.MessageModel
import java.util.ArrayList

class MessageRepository : MessageContract {


    private val messageModels: MutableList<MessageModel>

    override fun addAll(items: List<MessageModel>?) {
        messageModels.addAll(items!!)
    }

    override fun add(item: MessageModel) {
        messageModels.add(item)
    }

    override val all: List<MessageModel>
        get() = messageModels

    companion object {
        private var messageRepository: MessageRepository? = null
        val instance: MessageRepository?
            get() {
                if (messageRepository == null) messageRepository = MessageRepository()
                return messageRepository
            }
    }

    init {
        messageModels = ArrayList()
    }
}