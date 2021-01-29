package ly.mensly.forgiveyourself

class ListService private constructor() {
    companion object {
        val instance = ListService()
    }

    val list: List<String> get() = mistakes
    private val mistakes = mutableListOf<String>()
    private val listChanged = mutableListOf<(List<String>) -> Unit>()

    fun addListChangedListener(listener: (List<String>)->Unit) {
        listChanged += listener
    }

    fun clearListChangedListeners() = listChanged.clear()

    fun addItem(mistake: String) {
        mistakes.add(mistake)
        listChanged.forEach { it(mistakes) }
    }
}