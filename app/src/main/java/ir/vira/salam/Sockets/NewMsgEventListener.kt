package ir.vira.salam.Sockets

import ir.vira.salam.Models.MessageModel

interface NewMsgEventListener : EventListener {
    fun newMsg(messageModel: MessageModel?)
}