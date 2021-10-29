package ir.vira.salam.Utiles

import android.app.Activity
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun View.setGone() {this.visibility = View.GONE}

fun View.setVisible() {this.visibility = View.VISIBLE}

fun View.setInvisible() {this.visibility = View.INVISIBLE}

fun EditText.trimString() = this.text.trim().toString()

fun Fragment.doAfterDelay(time:Long = 200, block: suspend () -> Unit) {
    this.viewLifecycleOwner.lifecycleScope.launchWhenResumed {
        delay(time)
        block.invoke()
    }
}


fun Activity.showToast(message:String){
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}
fun Fragment.showToast(message:String?){
    message?.let {
        Toast.makeText(this.requireActivity(), message, Toast.LENGTH_LONG).show()
    }
}

fun LifecycleOwner.doOnUiThread( block:suspend ()-> Unit){
    this.lifecycleScope.launch {
        withContext(Dispatchers.Main){
            block.invoke()
        }
    }
}