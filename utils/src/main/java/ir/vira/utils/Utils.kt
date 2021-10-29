package ir.vira.utils

import android.annotation.SuppressLint
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.textfield.TextInputLayout
import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.content.Intent
import android.content.res.Resources
import android.graphics.*
import android.widget.Toast
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlin.Throws
import android.os.Environment
import android.net.Uri
import android.provider.Settings
import android.util.Base64
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

/**
 * This class provide some utils
 *
 * @author Ali Ghasemi
 */


fun Activity.getBitmap(imageId: Int): Bitmap {
    val drawable = AppCompatResources.getDrawable(this, imageId)
    val canvas = Canvas()
    val bitmap = Bitmap.createBitmap(
        drawable!!.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    canvas.setBitmap(bitmap)
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    drawable.draw(canvas)
    return bitmap
}

fun View.setFont(fontPath: String?) {
    val typeface = Typeface.createFromAsset(this.context.assets, fontPath)
    //When set inputType=textPassword fontFamily not work for textInputLayout so set font programmatically solve this problem .
    if (this is TextInputLayout) this.typeface = typeface
}

fun Activity.changeLayoutDirection(direction: Int) {
    this.window.decorView.layoutDirection = direction
}


fun setToolbar(
    toolbar: Toolbar?,
    activity: AppCompatActivity,
    drawerLayout: DrawerLayout,
    colorArrow: Int
) {
    var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    activity.setSupportActionBar(toolbar)
    activity.title = ""
    actionBarDrawerToggle = ActionBarDrawerToggle(activity, drawerLayout, 0, 0)
    drawerLayout.addDrawerListener(actionBarDrawerToggle!!)
    actionBarDrawerToggle!!.syncState()
    activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    activity.supportActionBar!!.setHomeButtonEnabled(true)
    actionBarDrawerToggle!!.drawerArrowDrawable.color = colorArrow
}

fun AppCompatActivity.setSubToolbar(
    toolbar: Toolbar,
    title: String?,
    direction: Int,
    homeIndicator: Int,
    textViewTitleID: Int
) {
    toolbar.layoutDirection = direction
    this.title = ""
    this.setSupportActionBar(toolbar)
    (toolbar.findViewById<View>(textViewTitleID) as TextView).text = title
    this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    this.supportActionBar!!.setHomeAsUpIndicator(homeIndicator)
}

fun Activity.checkPermission(permission: String, message: String, requestCode: Int): Boolean {
    val sharedPreferences = this.getSharedPreferences("permissionsList", Context.MODE_PRIVATE)
    return if (ActivityCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_DENIED
    ) {
        if (!sharedPreferences.contains(requestCode.toString())) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
            val editor = sharedPreferences.edit()
            editor.putString(requestCode.toString(), requestCode.toString())
            editor.apply()
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission))
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        else if (message.isNotEmpty()) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:" + this.packageName)
            this.startActivity(intent)
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
        false
    } else true
}


fun Activity.takeImage(imageCaptureRequestCode: Int):String? {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    return try {
        val image = createTempImageFile()
        val uri = FileProvider.getUriForFile(this, "image-capture", image)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        this.startActivityForResult(intent, imageCaptureRequestCode)
        null
    } catch (e: IOException) {
        e.printStackTrace()
        "در گرفتن عکس مشکلی پیش آمد"
    }

}

@SuppressLint("SimpleDateFormat")
@Throws(IOException::class)
private fun Activity.createTempImageFile(): File {
    var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    timeStamp = "JPEG_$timeStamp"
    return File.createTempFile(
        timeStamp,
        ".jpg",
        this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    )
}

fun Activity.chooseImageFromGallery(chooseImageFromGalleryRequestCode: Int) {
    val intent = Intent(Intent.ACTION_PICK)
    intent.type = "image/jpg"
    this.startActivityForResult(intent, chooseImageFromGalleryRequestCode)
}

fun getScreenSize(): HashMap<String, Int> {
    val screenSize = HashMap<String, Int>()
    screenSize["Width"] = Resources.getSystem().displayMetrics.widthPixels
    screenSize["Height"] =Resources.getSystem().displayMetrics.heightPixels
    return screenSize
}

fun String.isNationalCodeValid(): Boolean {
    return if (this.length == 10) {
        var mul: Int = 0
        for (i in this.length - 2 downTo 0) {
            mul += ((this[i].toString() + "").toByte() * (10 - i))
        }
        val remain: Short = (mul % 11).toShort()
        if (remain < 2) {
            remain == this[this.length - 1].toShort()
        } else {
            (this[this.length - 1].toString() + "").toByte().toInt() == 11 - remain
        }
    } else false
}

fun Bitmap.getEncodeImage(): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
    return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
}

fun String.getBitmap(): Bitmap {
    val bytes = Base64.decode(this, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

fun ByteArray.encodeToString(): String {
    return Base64.encodeToString(this, Base64.DEFAULT)
}

fun String.toPersian(): String {
    var text = this
    val nums = arrayOf(
        charArrayOf('0', '۰'),
        charArrayOf('1', '۱'),
        charArrayOf('2', '۲'),
        charArrayOf('3', '۳'),
        charArrayOf('4', '۴'),
        charArrayOf('5', '۵'),
        charArrayOf('6', '۶'),
        charArrayOf('7', '۷'),
        charArrayOf('8', '۸'),
        charArrayOf('9', '۹')
    )
    for (i in nums.indices) {
        text = text.replace(nums[i][0], nums[i][1])
    }
    return text
}

fun String.decodeToByte(): ByteArray {
    return Base64.decode(this, Base64.DEFAULT)
}
