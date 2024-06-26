package screens.tab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import navigation.tab.FourthScreenComponent

@Composable
fun FourthScreen(
    component: FourthScreenComponent
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("TAB TWO")
        Text(component.text)
        Text("COUNTDOWN: ${component.countDownText.value}")
    }
}