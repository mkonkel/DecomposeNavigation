import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import navigation.RootComponent
import screens.FirstScreen
import screens.SecondScreen

@Composable
fun App(rootComponent: RootComponent) {
    MaterialTheme {
        val childStack = rootComponent.childStack.subscribeAsState()
        Children(
            stack = childStack.value,
            animation = stackAnimation(slide()),
        ) { child ->
            when (val instance = child.instance) {
                is RootComponent.Child.FirstScreen ->
                    FirstScreen(instance.component)

                is RootComponent.Child.SecondScreen ->
                    SecondScreen(instance.component)
            }
        }
    }
}