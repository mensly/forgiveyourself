package ly.mensly.forgiveyourself

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
}