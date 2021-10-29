package ir.vira.salam.Models

import android.graphics.Bitmap
import java.io.Serializable


data class JoinRequest(
    var event: String? = null,
    var ip: String,
    var name: String,
    var profile: String,
    var secretKey: String,
    var requestStatus: String? = "",
    var bitmapImage:Bitmap? = null
):Serializable{

    constructor():this(null,"","","","",null)

}

