package ly.mensly.forgiveyourself

import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit

class ListService private constructor() {
    companion object {
        val instance = ListService()
        private const val KEY_MISTAKES = "mistakes"
    }

    val list: List<String> get() = mistakes
    private val mistakes by lazy { loadMistakes().toMutableList() }
    private val listChanged = mutableListOf<(List<String>) -> Unit>()
    @Suppress("DEPRECATION")
    private val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(App.instance) }

    fun addListChangedListener(listener: (List<String>)->Unit) {
        listChanged += listener
    }

    fun clearListChangedListeners() = listChanged.clear()

    fun addItem(mistake: String) {
        mistakes.add(mistake)
        listChanged.forEach { it(mistakes) }
        saveMistakes()
    }

    private fun saveMistakes() {
        sharedPreferences.edit {
            putStringSet(KEY_MISTAKES, mistakes.toSet())
        }
    }

    private fun loadMistakes() = sharedPreferences.getStringSet(KEY_MISTAKES, emptySet())!!
}