package ly.mensly.forgiveyourself

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.NavUtils

class NotificationActivity : AppCompatActivity() {
    private lateinit var enabled: SwitchCompat
    private lateinit var time: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        enabled = findViewById(R.id.enabled)
        enabled.setOnCheckedChangeListener { _, isChecked -> NotificationService.instance.enabled.value = isChecked }
        time = findViewById(R.id.time)
        time.setOnClickListener {

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}