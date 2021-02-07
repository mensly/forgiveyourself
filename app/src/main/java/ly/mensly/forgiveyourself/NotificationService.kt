package ly.mensly.forgiveyourself

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.preference.PreferenceManager
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
    }

    val enabled by lazy { MutableLiveData<Boolean>().apply { value = loadEnabled() } }
    val scheduledTime by lazy { MutableLiveData<Long>().apply { value = loadScheduledTime() } }
    @Suppress("DEPRECATION")
    private val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(App.instance) }
    private val alarmManager by lazy { App.instance.getSystemService(ALARM_SERVICE) as AlarmManager }

    init {
        enabled.observeForever {
            sharedPreferences.edit(true) {
                putBoolean(KEY_ENABLED, it)
                remove(KEY_TIME)
            }
            configureAlarm()
        }
        scheduledTime.observeForever {
            sharedPreferences.edit(true) { putLong(KEY_TIME, it) }
            configureAlarm()
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

    private fun configureAlarm() {
        val alarmIntent = Intent(App.instance, NotificationReceiver::class.java)
        val broadcast = PendingIntent.getBroadcast(App.instance, REQUEST_CODE, alarmIntent, 0)
        alarmManager.cancel(broadcast)
        if (enabled.value == true) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, scheduledTime.value ?: Long.MIN_VALUE, broadcast)
        }
    }

}