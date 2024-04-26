package navigation.tab

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import kotlinx.serialization.Serializable

class TabNavigationComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val navigation = StackNavigation<Configuration>()
    val childStack = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration = Configuration.TabOne,
        handleBackButton = true,
        childFactory = ::createChild,
        key = "TabNavigationStack"
    )

    @OptIn(ExperimentalDecomposeApi::class)
    private fun createChild(configuration: Configuration, componentContext: ComponentContext): Child =
        when (configuration) {
            is Configuration.TabOne -> {
                Child.TabOne(ThirdScreenComponent(componentContext))
            }

            is Configuration.TabTwo -> {
                Child.TabTwo(FourthScreenComponent(componentContext))
            }
        }

    fun onTabOneClick() {
        navigation.bringToFront(Configuration.TabOne)
    }

    fun onTabTwoClick() {
        navigation.bringToFront(Configuration.TabTwo)
    }

    sealed class Child {
        data class TabOne(val component: ThirdScreenComponent) : Child()
        data class TabTwo(val component: FourthScreenComponent) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data object TabOne : Configuration()

        @Serializable
        data object TabTwo : Configuration()
    }
}


