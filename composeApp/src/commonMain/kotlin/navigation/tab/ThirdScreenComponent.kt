package navigation.tab

import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ThirdScreenComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    val text = "Hello from ThirdScreen"
    val countDownText = mutableStateOf<String>("0")

    init {
        val scope = coroutineScope(Dispatchers.Default + SupervisorJob())
        scope.launch {
            for (i in 10 downTo 0) {
                countDownText.value = i.toString()
                delay(1000)
            }
        }
    }


    private fun CoroutineScope(context: CoroutineContext, lifecycle: Lifecycle): CoroutineScope {
        val scope = CoroutineScope(context)
        lifecycle.doOnDestroy(scope::cancel)
        return scope
    }

    private fun LifecycleOwner.coroutineScope(context: CoroutineContext): CoroutineScope =
        CoroutineScope(context, lifecycle)
}