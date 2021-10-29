package ir.vira.salam.Repositories

import ir.vira.salam.Contracts.UserContract
import ir.vira.salam.Models.UserModel
import java.util.ArrayList

class UserRepository : UserContract {
    private val userModels: MutableList<UserModel>
    override fun addAll(items: List<UserModel>?) {
        userModels.addAll(items!!)
    }

    override fun add(item: UserModel) {
        userModels.add(item)
    }

    override val all: List<UserModel>
        get() = userModels

    override fun findUserByIP(ip: String?): UserModel {
        for (i in userModels.indices) {
            if (userModels[i].ip == ip) return userModels[i]
        }
        return userModels[0]
    }

    override fun removeUser(userModel: UserModel) {
        userModels.remove(userModel)
    }

    companion object {
        private var userRepository: UserRepository? = null
        val instance: UserRepository?
            get() {
                if (userRepository == null) userRepository = UserRepository()
                return userRepository
            }
    }

    init {
        userModels = ArrayList()
    }
}