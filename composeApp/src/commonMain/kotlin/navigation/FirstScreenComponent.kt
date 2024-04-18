package navigation

import com.arkivanov.decompose.ComponentContext

class FirstScreenComponent(
    componentContext: ComponentContext,
    private val onButtonClick: (String) -> Unit,
) : ComponentContext by componentContext {

    fun click() {
        onButtonClick("Hello from FirstScreenComponent!")
    }
}