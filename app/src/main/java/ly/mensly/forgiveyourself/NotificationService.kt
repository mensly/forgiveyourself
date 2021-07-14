package ly.mensly.forgiveyourself

import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

class NotificationService private constructor() {
    companion object {
        val instance = NotificationService()
        private const val KEY_ENABLED = "notif_enabled"
        private const val KEY_TIME = "notif_time"
        private const val REQUEST_CODE = 3
        private const val NOTIFICATION_ID = 5
        private const val CHANNEL_ID = "reminder"
    }

    val enabled by lazy { MutableLiveData<Boolean>().apply { value = loadEnabled() } }
    val scheduledTime by lazy { MutableLiveData<Long>().apply { value = loadScheduledTime() } }
    @Suppress("DEPRECATION")
    private val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(App.instance) }
    private val alarmManager by lazy { App.instance.getSystemService(ALARM_SERVICE) as AlarmManager }

    init {
        enabled.observeForever {
            sharedPreferences.edit {
                putBoolean(KEY_ENABLED, it)
                if (it) {
                    putLong(KEY_TIME, scheduledTime.value!!)
                }
                else {
                    remove(KEY_TIME)
                }
            }
            configureAlarm()
        }
        scheduledTime.observeForever {
            sharedPreferences.edit { putLong(KEY_TIME, it) }
            configureAlarm()
        }
    }

    fun addOneYear() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = scheduledTime.value!!
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1)
        scheduledTime.value = calendar.timeInMillis
    }

    fun show() {
        if (ListService.instance.list.isEmpty()) return
        val context = App.instance
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(context)
        }
        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0)

        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.notif_text))
            .setSmallIcon(R.drawable.ic_notif)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, notif)
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel(context: Context) {
        // Create the NotificationChannel
        val name = context.getString(R.string.notif_channel)
        val descriptionText = context.getString(R.string.notif_channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        with(NotificationManagerCompat.from(context)) {
            createNotificationChannel(mChannel)
        }
    }

    private fun loadEnabled() = sharedPreferences.getBoolean(KEY_ENABLED, false)

    private fun loadScheduledTime(): Long {
        val scheduledTime = sharedPreferences.getLong(KEY_TIME, Long.MIN_VALUE)
        if (scheduledTime == Long.MIN_VALUE) {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1)
            cal.set(Calendar.DAY_OF_YEAR, 1)
            cal.set(Calendar.HOUR_OF_DAY, 12)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            return cal.timeInMillis
        }
        return scheduledTime
    }

    fun configureAlarm() {
        val alarmIntent = Intent(App.instance, NotificationReceiver::class.java)
        val broadcast = PendingIntent.getBroadcast(App.instance, REQUEST_CODE, alarmIntent, 0)
        alarmManager.cancel(broadcast)
        if (enabled.value == true) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, scheduledTime.value ?: Long.MIN_VALUE, broadcast)
        }
    }
}
