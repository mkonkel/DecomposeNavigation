package navigation

import com.arkivanov.decompose.ComponentContext

class SecondScreenComponent(
    componentContext: ComponentContext,
    private val text: String
) : ComponentContext by componentContext {
    // Some code here
}