package screens.tab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import navigation.tab.ThirdScreenComponent

@Composable
fun ThirdScreen(
    component: ThirdScreenComponent,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("TAB ONE")
        Text(component.text)
        Spacer(modifier = Modifier.height(16.dp))
        Text("COUNTDOWN: ${component.countDownText.value}")
    }
}