package ly.mensly.forgiveyourself

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val adapter = MistakeAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val input = findViewById<EditText>(R.id.input)
        findViewById<Button>(R.id.btn_add).setOnClickListener {
            val text = input.text
            if (text.isBlank()) return@setOnClickListener
            ListService.instance.addItem(text.toString())
            input.setText("")
        }
        findViewById<Button>(R.id.btn_clear).setOnClickListener {
            ListService.instance.clearItems()
        }
        findViewById<RecyclerView>(R.id.list).adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        ListService.instance.addListChangedListener {
            adapter.mistakes = it
        }
        adapter.mistakes = ListService.instance.list
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