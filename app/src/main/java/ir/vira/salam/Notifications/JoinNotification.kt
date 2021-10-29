package ir.vira.salam.Notifications

import android.app.Notification
import android.app.NotificationChannel
import ir.vira.salam.Models.UserModel
import android.app.NotificationManager
import android.os.Build
import android.content.Intent
import ir.vira.salam.Receivers.PendingReceiver.DeclineJoinReceiver
import ir.vira.salam.Receivers.PendingReceiver.AcceptJoinReceiver
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import ir.vira.salam.R
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.app.NotificationCompat

class JoinNotification {
    private var notificationChannel: NotificationChannel? = null
    private var notification: Notification? = null
    fun showNotification(userModel: UserModel, context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NOTIFICATION_IMPORTANCE)
            notificationManager.createNotificationChannel(notificationChannel!!)
        }
        val intentDecline = Intent(context, DeclineJoinReceiver::class.java)
        val intentAccept = Intent(context, AcceptJoinReceiver::class.java)
        intentDecline.putExtra("user", userModel)
        val pendingIntentDecline = PendingIntent.getBroadcast(
            context,
            context.resources.getInteger(R.integer.declineRequestCode),
            intentDecline,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val pendingIntentAccept = PendingIntent.getBroadcast(
            context,
            context.resources.getInteger(R.integer.acceptRequestCode),
            intentAccept,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val spannableStringAccept = SpannableString("قبول")
        spannableStringAccept.setSpan(
            ForegroundColorSpan(Color.parseColor("#4CAF50")),
            0,
            spannableStringAccept.length,
            0
        )
        val spannableStringDecline = SpannableString("لغو")
        spannableStringDecline.setSpan(
            ForegroundColorSpan(Color.parseColor("#F44336")),
            0,
            spannableStringDecline.length,
            0
        )
        notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentText(userModel.name + " در خواست عضویت کرده . آیا آنرا می پذیزید ؟ ")
            .setContentTitle("در خواست عضویت").setSmallIcon(R.mipmap.salam_logo).addAction(
            NotificationCompat.Action(
                R.drawable.ic_account,
                spannableStringAccept,
                pendingIntentAccept
            )
        ).addAction(
            NotificationCompat.Action(
                R.drawable.ic_account,
                spannableStringDecline,
                pendingIntentDecline
            )
        ).setOnlyAlertOnce(true).setColor(
            Color.parseColor("#2A91AD")
        ).setAutoCancel(true).build()
        notificationManager.notify(notifyId, notification)
    }

    companion object {
        private const val CHANNEL_NAME = "Join"
        private const val CHANNEL_ID = "Salam"
        const val notifyId = 21
        private const val NOTIFICATION_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH
    }
}