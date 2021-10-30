package ir.vira.salam.DesignPatterns.Factory

import ir.vira.salam.Contracts.Contract
import ir.vira.salam.Enumerations.RepositoryType
import ir.vira.salam.Repositories.UserRepository
import ir.vira.salam.Repositories.MessageRepository
import java.lang.Exception

object RepositoryFactory {
    fun getRepository(repositoryType: RepositoryType?): Contract<*>? {
        return when (repositoryType) {
            RepositoryType.USER_REPO -> UserRepository.instance
            RepositoryType.MESSAGE_REPO -> MessageRepository.instance
            else -> {
                val exception = Exception("repo type is invalid ! ")
                exception.printStackTrace()
                null
            }
        }
    }
}