package screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import navigation.tab.TabNavigationComponent
import screens.tab.FourthScreen
import screens.tab.ThirdScreen

@Composable
fun TabsScreen(
    tabNavigationComponent: TabNavigationComponent
) {
    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { tabNavigationComponent.onTabOneClick() }
                ) {
                    Text("TAB ONE")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { tabNavigationComponent.onTabTwoClick() }
                ) {
                    Text("TAB TWO")
                }
            }
        }
    ) { innerPadding ->
        val childStack = tabNavigationComponent.childStack.subscribeAsState()

        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Children(
                stack = childStack.value,
                animation = stackAnimation(slide()),
            ) { child ->
                when (val instance = child.instance) {
                    is TabNavigationComponent.Child.TabOne ->
                        ThirdScreen(instance.component)

                    is TabNavigationComponent.Child.TabTwo ->
                        FourthScreen(instance.component)
                }
            }
        }
    }
}