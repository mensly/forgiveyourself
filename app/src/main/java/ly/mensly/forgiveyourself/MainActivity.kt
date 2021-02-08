package ly.mensly.forgiveyourself

import android.app.NotificationManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private val adapter = MistakeAdapter()
    private lateinit var clearBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val input = findViewById<EditText>(R.id.input)
        val add = findViewById<Button>(R.id.btn_add)
        input.setOnEditorActionListener { _, _, _ -> add.performClick() }
        add.setOnClickListener {
            val text = input.text
            if (text.isBlank()) return@setOnClickListener
            ListService.instance.addItem(text.toString())
            input.setText("")
            if (ListService.instance.list.size == 1 && NotificationService.instance.enabled.value != true) {
                AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.notifications_prompt)
                    .setPositiveButton(android.R.string.ok) { _,_ ->
                        startActivity(Intent(this, NotificationActivity::class.java))
                    }
                    .setNegativeButton(android.R.string.cancel) { _,_ -> }
                    .show()
            }
        }
        clearBtn = findViewById(R.id.btn_clear)
        clearBtn.setOnClickListener {
            if (ListService.instance.list.isEmpty()) { return@setOnClickListener }
            AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(R.string.clear_prompt)
                .setPositiveButton(android.R.string.ok) { _,_ ->
                    ListService.instance.clearItems()
                }
                .setNegativeButton(android.R.string.cancel) { _,_ -> }
                .show()
        }
        findViewById<RecyclerView>(R.id.list).adapter = adapter
        NotificationService.instance.apply {
            if (enabled.value == true && scheduledTime.value!! < System.currentTimeMillis()) {
                addOneYear()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ListService.instance.addListChangedListener {
            adapter.mistakes = it
            clearBtn.isEnabled = it.isNotEmpty()
        }
        adapter.mistakes = ListService.instance.list
        clearBtn.isEnabled = ListService.instance.list.isNotEmpty()
    }

    override fun onPause() {
        super.onPause()
        ListService.instance.clearListChangedListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_help -> {
                startActivity(Intent(this, HelpActivity::class.java))
                return true
            }
            R.id.menu_notifications -> {
                startActivity(Intent(this, NotificationActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}