package navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import kotlinx.serialization.Serializable
import navigation.tab.TabNavigationComponent

class RootComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val navigation = StackNavigation<Configuration>()
    val childStack = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration = Configuration.FirstScreen,
        handleBackButton = true,
        childFactory = ::createChild
    )

    @OptIn(ExperimentalDecomposeApi::class)
    private fun createChild(configuration: Configuration, componentContext: ComponentContext): Child =
        when (configuration) {
            is Configuration.FirstScreen -> {
                Child.FirstScreen(
                    component = FirstScreenComponent(
                        componentContext = componentContext,
                        onGoToSecondScreenClick = { textFromFirstScreen ->
                            navigation.pushNew(Configuration.SecondScreen(text = textFromFirstScreen))
                        },
                        onGoToTabsScreen = {
                            navigation.pushNew(Configuration.TabsNavigation)
                        }
                    )
                )
            }

            is Configuration.SecondScreen -> Child.SecondScreen(
                component = SecondScreenComponent(
                    componentContext = componentContext,
                    text = configuration.text,
                    onBackButtonClick = { navigation.pop() }
                )
            )

            Configuration.TabsNavigation -> Child.TabsScreen(
                component = TabNavigationComponent(
                    componentContext = componentContext
                )
            )
        }

    sealed class Child {
        data class FirstScreen(val component: FirstScreenComponent) : Child()
        data class SecondScreen(val component: SecondScreenComponent) : Child()
        data class TabsScreen(val component: TabNavigationComponent) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data object FirstScreen : Configuration()

        @Serializable
        data class SecondScreen(val text: String) : Configuration()

        @Serializable
        data object TabsNavigation : Configuration()
    }
}