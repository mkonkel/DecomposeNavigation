package navigation

import com.arkivanov.decompose.ComponentContext

class SecondScreenComponent(
    componentContext: ComponentContext,
    private val text: String,
    private val onBackButtonClick: () -> Unit
) : ComponentContext by componentContext {
    fun getGreeting(): String = text
    fun goBack() {
        onBackButtonClick()
    }
}