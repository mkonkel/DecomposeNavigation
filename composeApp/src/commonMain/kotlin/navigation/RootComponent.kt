package navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import kotlinx.serialization.Serializable

class RootComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val navigation = StackNavigation<Configuration>()
    private val childStack = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration = Configuration.FirstScreen,
        handleBackButton = true,
        childFactory = ::createChild
    )

    private fun createChild(configuration: Configuration, componentContext: ComponentContext): Child =
        when (configuration) {
            is Configuration.FirstScreen -> Child.FirstScreen(FirstScreenComponent(componentContext))
            is Configuration.SecondScreen -> Child.SecondScreen(SecondScreenComponent(componentContext, configuration.text))
        }

    sealed class Child {
        data class FirstScreen(val component: FirstScreenComponent) : Child()
        data class SecondScreen(val component: SecondScreenComponent) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data object FirstScreen : Configuration()
        @Serializable
        data class SecondScreen(val text: String) : Configuration()
    }
}