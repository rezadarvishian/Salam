package ir.vira.salam.Sockets

import ir.vira.salam.Models.UserModel

@FunctionalInterface
interface JoinEventListener : EventListener {
    fun join(userModel: UserModel?)
}