package navigation

import com.arkivanov.decompose.ComponentContext

class FirstScreenComponent(
    componentContext: ComponentContext,
    private val onGoToSecondScreenClick: (String) -> Unit,
    private val onGoToTabsScreen: () -> Unit,
) : ComponentContext by componentContext {

    fun newScreen() {
        onGoToSecondScreenClick("Hello from FirstScreenComponent!")
    }

    fun tabScreen() {
        onGoToTabsScreen()
    }
}