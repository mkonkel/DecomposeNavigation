package navigation.tab

import com.arkivanov.decompose.ComponentContext

class ThirdScreenComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    val text = "Hello from ThirdScreen"
}