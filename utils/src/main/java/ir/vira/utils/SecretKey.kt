package ir.vira.utils

import java.security.NoSuchAlgorithmException
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

object SecretKey {

    private var secretKey: SecretKey? = null

    fun generateKey(encryptionAlgorithm: EncryptionAlgorithm, keySize: Int? = null): SecretKey? {
        return try {
            if (secretKey == null) {
                val keyGenerator = KeyGenerator.getInstance(encryptionAlgorithm.name)
                if (keySize!=null)keyGenerator.init(keySize)
                secretKey = keyGenerator.generateKey()
            }
            secretKey
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            null
        }
    }

}