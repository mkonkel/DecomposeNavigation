package navigation.tab

import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FourthScreenComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    val text = "Hello from FourthScreen"

    //Essently
    private val scope = coroutineScope(Dispatchers.Default + SupervisorJob())

    val countDownText = mutableStateOf<String>("0")

    init {
        scope.launch {
            for (i in 10 downTo 0) {
                countDownText.value = i.toString()
                delay(1000)
            }
        }
    }
}