package ir.vira.salam.Models

import android.graphics.Bitmap
import java.io.Serializable
import javax.crypto.SecretKey

class UserModel(
    val ip: String,
    val name: String,
    @field:Transient val profile: Bitmap,
    @field:Transient val secretKey: SecretKey
) : Serializable