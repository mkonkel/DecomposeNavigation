package screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import navigation.FirstScreenComponent
import navigation.SecondScreenComponent

@Composable
fun SecondScreen(
    component: SecondScreenComponent
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Second screen")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Greetings: ${component.getGreeting()}")
        Button(onClick = { component.goBack() }) {
            Text("Go Back")
        }
    }
}