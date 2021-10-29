package ir.vira.salam.Contracts

import ir.vira.salam.Models.UserModel

interface UserContract : Contract<UserModel> {
    fun findUserByIP(ip: String?): UserModel
    fun removeUser(userModel: UserModel)
}