package ly.mensly.forgiveyourself

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat


class NotificationActivity : AppCompatActivity() {
    private companion object {
        private const val NOTIFICATION_PERMISSION_CODE = 6
    }
    private lateinit var enabled: SwitchCompat
    private lateinit var time: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        enabled = findViewById(R.id.enabled)
        enabled.setOnCheckedChangeListener { _, isChecked -> NotificationService.instance.enabled.value = isChecked }
        time = findViewById(R.id.time)
        time.setOnClickListener {
            DatePickerFragment.showInstance(
                this,
                NotificationService.instance.scheduledTime.value!!
            )
        }
        NotificationService.instance.enabled.observe(this) {
            if (it) {
                requestNotificationPermission()
            }
            enabled.isChecked = it
            time.isEnabled = it
        }
        NotificationService.instance.scheduledTime.observe(this) {
            time.text = SimpleDateFormat.getDateTimeInstance().format(it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
            ) == PackageManager.PERMISSION_GRANTED
        ) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_NOTIFICATION_POLICY),
                NOTIFICATION_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        NotificationService.instance.enabled.value =
            requestCode == NOTIFICATION_PERMISSION_CODE && grantResults.contains(PackageManager.PERMISSION_GRANTED)
    }
}