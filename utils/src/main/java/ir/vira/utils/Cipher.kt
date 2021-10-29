package ir.vira.utils

import ir.vira.utils.SecretKey.generateKey
import java.lang.Exception
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

object Cipher {

    var cipher: Cipher? = null
    private const val UNICODE_FORMAT = "utf-8"

    fun getCipher(encryptionAlgorithm: EncryptionAlgorithm): Cipher? {
        return try {
            if (cipher == null) cipher = Cipher.getInstance(encryptionAlgorithm.name)
            cipher
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            null
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
            null
        }
    }

    fun String.encryptData(encryptionAlgorithm: EncryptionAlgorithm): ByteArray? {
        return try {
            val dataToEncrypt = this.toByteArray(charset(UNICODE_FORMAT))
            getCipher(encryptionAlgorithm)!!.init(Cipher.ENCRYPT_MODE, generateKey(encryptionAlgorithm))
            cipher!!.doFinal(dataToEncrypt)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun SecretKey.decryptData(dataToDecrypt: ByteArray?, encryptionAlgorithm: EncryptionAlgorithm?): String? {
        return try {
            getCipher(encryptionAlgorithm!!)!!.init(Cipher.DECRYPT_MODE, this)
            val dataDecrypted = cipher!!.doFinal(dataToDecrypt)
            String(dataDecrypted)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun ByteArray.decryptData(secretKey: SecretKey?, encryptionAlgorithm: EncryptionAlgorithm): String? {
        return try {
            getCipher(encryptionAlgorithm)!!.init(Cipher.DECRYPT_MODE, secretKey)
            val dataDecrypted = cipher!!.doFinal(this)
            String(dataDecrypted)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}