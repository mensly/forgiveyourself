package ly.mensly.forgiveyourself

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils

class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        configureButton(R.id.btn_au, "1300224636")
        configureButton(R.id.btn_uk, "01708765200")
        configureButton(R.id.btn_us, "18009855990")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun configureButton(@IdRes id: Int, phoneNumber: String) {
        findViewById<Button>(id).setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            })
        }
    }
}