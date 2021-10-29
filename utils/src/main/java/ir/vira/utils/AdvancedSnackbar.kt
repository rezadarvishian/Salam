package ir.vira.utils

import androidx.appcompat.app.ActionBarDrawerToggle
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.Typeface
import com.google.android.material.textfield.TextInputLayout
import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import android.widget.TextView
import android.content.SharedPreferences
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.content.Intent
import android.widget.Toast
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlin.Throws
import android.os.Environment
import ir.vira.utils.EncryptionAlgorithm
import android.graphics.BitmapFactory
import androidx.annotation.RequiresApi
import android.os.Build
import android.view.View
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import android.widget.FrameLayout
import com.google.android.material.R

/**
 * This snackbar can set can font
 *
 * @author Ali Ghasemi
 */
object AdvancedSnackbar {
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun make(
        view: View?,
        charSequence: CharSequence?,
        duration: Int,
        context: Context,
        font: Int
    ): Snackbar {
        val snackbar = Snackbar.make(view!!, charSequence!!, duration)
        snackbar.view.layoutDirection = View.LAYOUT_DIRECTION_RTL
        val textView = snackbar.view.findViewById<TextView>(R.id.snackbar_text)
        val button = snackbar.view.findViewById<Button>(R.id.snackbar_action)
        textView.typeface = context.resources.getFont(font)
        button.typeface = context.resources.getFont(font)
        return snackbar
    }

    @Deprecated("")
    fun make(
        view: View?,
        charSequence: CharSequence?,
        duration: Int,
        context: Context,
        font: String?
    ): Snackbar {
        val snackbar = Snackbar.make(view!!, charSequence!!, duration)
        snackbar.view.layoutDirection = View.LAYOUT_DIRECTION_RTL
        val textView = snackbar.view.findViewById<TextView>(R.id.snackbar_text)
        val button = snackbar.view.findViewById<Button>(R.id.snackbar_action)
        val typeface = Typeface.createFromAsset(context.assets, font)
        textView.typeface = typeface
        button.typeface = typeface
        return snackbar
    }

    fun makeWithMargin(
        view: View?,
        charSequence: CharSequence?,
        duration: Int,
        context: Context,
        font: String?,
        margin: Int
    ): Snackbar {
        val snackbar = Snackbar.make(view!!, charSequence!!, duration)
        snackbar.view.layoutDirection = View.LAYOUT_DIRECTION_RTL
        val layoutParams = snackbar.view.layoutParams as FrameLayout.LayoutParams
        layoutParams.setMargins(margin, layoutParams.topMargin, margin, margin)
        snackbar.view.layoutParams = layoutParams
        val textView = snackbar.view.findViewById<TextView>(R.id.snackbar_text)
        val button = snackbar.view.findViewById<Button>(R.id.snackbar_action)
        val typeface = Typeface.createFromAsset(context.assets, font)
        textView.typeface = typeface
        button.typeface = typeface
        return snackbar
    }
}