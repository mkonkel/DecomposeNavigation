This is a Kotlin Multiplatform project targeting Android and iOS where we will showcase the Decompose as the app
navigation.

- Application sgould allow us to navigate from one screen to another.
- Application should allow to pass some parameters from first to second screen.
- Application should handle the screen rotation without loosing data.
  ...

Base project setup as always is made with [Kotlin Multiplatform Wizard](https://kmp.jetbrains.com), we also need to add
some [Decompose](https://arkivanov.github.io/Decompose/getting-started/installation/#__tabbed_1_2) as it is the core
thing that we would like to examine.
There is also one thing that we need to add to the project and that is
the [Kotlin Serialization](https://github.com/Kotlin/kotlinx.serialization) plugin.

*libs.versions.toml*

```toml
[versions]
decompose = "3.0.0-beta01"
serialization = "1.6.3"

[libraries]
decompose = { module = "com.arkivanov.decompose:decompose", version.ref = "decompose" }
decompose-compose = { module = "com.arkivanov.decompose:extensions-compose", version.ref = "decompose" }
serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "serialization" }

[plugins]
kotlinSerialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

Freshly added dependencies needs to be synced with the project and added to the ***build.gradle.kts***

```kotlin 
plugins {
    alias(libs.plugins.kotlinSerialization)
}

sourceSets {
    androidMain.dependencies {
        ...
        implementation(libs.decompose)
    }
    commonMain.dependencies {
        ...
        implementation(libs.decompose)
        implementation(libs.decompose.compose)
        implementation(libs.serialization)
    }
}
```

Now we can sync the project and start coding. Following the Decompose documentation we can notice that the main element
of the library is the [Component](https://arkivanov.github.io/Decompose/component/overview/) class that is encapsulating
logic (and other components).
Components are lifecycle-aware with their own [lifecycle](https://arkivanov.github.io/Decompose/component/overview/)that
is automatically managed. its lifecycle is very similar to the androids activity lifecycle. Components are independent
of the UI and the UI should relay on the components.
The idea is to hold as much code in the shared logic as possible - components are responsive for holding business logic
and the navigation itself (the navigation is separated from teh UI). If you are familiar with the Android development
you can think of the components as the ViewModel.

Each component should have a *ComponentContext* that manages its lifecycle, keeps it state (can preserve component state
during changes) and handles back button. The context is passed through the constructor and can be added to the component
by the delegation.

As mentioned above the main point of the app should be a *RootComponent* which should be provided with the
*ComponentContext* to determine how it should act on different platforms. Therefore, it's context cannot be provided and
must be created on the platform itself.
For such situations, we can use the *DefaultComponentContext()* - if it's created inside the ***@Composable*** function
we should always use the ***remember{}***  so the context will not be created with every recomposition.

With that covered we can start to code, lets create a *navigation* package in our project with the RootComponent. The
RootComponent will live as long as the application.

![Basic Project Structure](/blog/images/1_basic_project_structure.png "Basic Project Structure")

```kotlin
class RootComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    // Some code here
}
```

Let's assume that our application will hold two screens - *FirstScreen* and *SecondScreen*. Both of them will be
represented by the *Component* class. The *FirstScreen* will be the first screen that will be shown to the user and the
*SecondScreen* will be shown after the user clicks the button on the *FirstScreen*.
To handle such case we need to create a [Stack](https://arkivanov.github.io/Decompose/navigation/overview/) in the
*RootComponent* - the stack is provided to the component via the ComponentContext. Every stack requires the
***Configuration*** that needs to be @Serializable, it will represent the child components and contains all arguments
needed to create it.

```kotlin
@Serializable
sealed class Configuration {
    @Serializable
    data object FirstScreen : Configuration()

    @Serializable
    data class SecondScreen(val text: String) : Configuration()
}
```

The created configuration can be used now in the stack creation. We should use
the [StackNavigator](https://arkivanov.github.io/Decompose/navigation/stack/navigation/) interface.
It contains the methods needed to handle the process, such as ***navigate()***, ***push()***, ***pop()*** etc...

```kotlin
private val navigation = StackNavigation<Configuration>()
```

The definitions of child components are created by the Configuration, but now e need also to
create [Child Components](https://arkivanov.github.io/Decompose/component/child-components/) itself.
Components are organised as trees, where the root component is the main component and the child components are the
components that are created by the main component. The parent component knows only about it's direct children.
Every component ca be independently reuse in every place in the app. With the usage of the navigation components are
automatically created and destroyed, and they need a provided component context from the parent.
Let's now focus on the [Child Stack](https://arkivanov.github.io/Decompose/navigation/stack/overview/) approach, but you
can find other solutions in the docs.

During the navigation, the child stack compares new configurations with previous one, there should be only one (the top)
component active, others are in the back and stopped or destroyed.

```kotlin
class FirstScreenComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    // Some code here
}

class SecondScreenComponent(
    componentContext: ComponentContext,
    private val text: String
) : ComponentContext by componentContext {
    // Some code here
}
```

With new components added we now need to create them inside the root component - they should be called children.

```kotlin
sealed class Child {
    data class FirstScreen(val component: FirstScreenComponent) : Child()
    data class SecondScreen(val component: SecondScreenComponent) : Child()
}
```

The last thing to do is to create the ***childStack***. The childStack requires some parameters to be passed, such as
the source of the navigation, the serializer, the initial configuration, the handleBackButton and the childFactory.
The ***childFactory*** is a function that creates the child component based on the configuration and component context.
The childStack is responsible for creating the child components and managing their lifecycle.

```kotlin
    val childStack = childStack(
    source = navigation,
    serializer = Configuration.serializer(),
    initialConfiguration = Configuration.FirstScreen,
    handleBackButton = true,
    childFactory = ::createChild
)
```

```kotlin
    private fun createChild(configuration: Configuration, componentContext: ComponentContext): Child =
    when (configuration) {
        is Configuration.FirstScreen -> Child.FirstScreen(FirstScreenComponent(componentContext))
        is Configuration.SecondScreen -> Child.SecondScreen(SecondScreenComponent(componentContext, configuration.text))
    }
```

ChildStack cannot be empty, it has to have at leas active (resumed) child component. Component in the back are always
stopped. If we want to use multiple ChildStacks in one component all of them has to have unique key associated.
If we examine the ***childStack*** we can notice that it is
a [Value](https://github.com/arkivanov/Decompose/tree/master/decompose/src/commonMain/kotlin/com/arkivanov/decompose/value)
type.

![Decompose Value](/blog/images/2_child_stack_value_type.png "Decompose Value Type")

The ***Value*** is a type that represents a value that can be observed is the Decomposes equivalent of Jetpack Compose
***State***, it is also independent of the approach u want to use further in the application. Nevertheless, in the *
*Compose Multiplatform** approach it can (and should) be transformed to the state.

With all things done, we can now handle the actual navigation, following
the [documentation](https://arkivanov.github.io/Decompose/navigation/stack/overview/#delivering-a-result-when-navigating-back)
we can handle it with multiple ways - with traditional callbacks or with a bit more reactive approach with `flow`
or `observable`. It's all upon to yuo how you want to communicate child components with the root component.
You can also create a global ***navigation*** object that will be responsible for changing the screens from any place in
the app, there is no good or bad practice. For the simplification of the example, I will use the callbacks.

In the ***firstScreen*** I will add a lambda expression on `onButtonClick: (String) -> Unit` that will be called when
the button is clicked. The lambda will be called with the greetings text, and handled in the ***RootComponent***.

```kotlin
class FirstScreenComponent(
    componentContext: ComponentContext,
    private val onButtonClick: (String) -> Unit,
) : ComponentContext by componentContext {

    fun click() {
        onButtonClick("Hello from FirstScreenComponent!")
    }
}
```

Now I need to implement the callback and handle the navigation.

```kotlin
    @OptIn(ExperimentalDecomposeApi::class)
private fun createChild(configuration: Configuration, componentContext: ComponentContext): Child =
    when (configuration) {
        is Configuration.FirstScreen -> {
            Child.FirstScreen(
                component = FirstScreenComponent(
                    componentContext = componentContext,
                    onButtonClick = { textFromFirstScreen ->
                        navigation.pushNew(Configuration.SecondScreen(text = textFromFirstScreen))
                    }
                )
            )
        }
            ...
    }
```

The ***Decompose*** gives a plenty wat of starting new screen:

- `push(configuration)` - pushes new screen to top of the stack
- `pushNew(configuration)` - pushes new screen to top of the stack, does nothing if configuration already on the top of
  stack
- `pushToFront(configuration)` - pushes the provided configuration to the top of the stack, removing the configuration
  from the back stack, if any
- `pop()` - pops the latest configuration at the top of the stack.
- and more, that are
  described [here](https://arkivanov.github.io/Decompose/navigation/stack/navigation/#stacknavigator-extension-functions)

Same approach can be used to handle the back button, the ***handleBackButton*** parameter in the ***childStack*** is
responsible for that. If the back button is pressed the ***childStack*** will pop the latest configuration from the
stack.

```kotlin
class SecondScreenComponent(
    componentContext: ComponentContext,
    private val text: String,
    private val onBackButtonClick: () -> Unit
) : ComponentContext by componentContext {
    fun getGreeting(): String = text
    fun goBack() {
        onBackButtonClick()
    }
}
```

```kotlin
    @OptIn(ExperimentalDecomposeApi::class)
private fun createChild(configuration: Configuration, componentContext: ComponentContext): Child =
    when (configuration) {
            ...
        is Configuration.SecondScreen -> Child.SecondScreen(
            component = SecondScreenComponent(
                componentContext = componentContext,
                text = configuration.text,
                onBackButtonClick = { navigation.pop() }
            )
        )
    }
```

The whole navigation is now completed, and it is independent of th UI, it's pure kotlin and in shared code, and can be
unit tested.
The last thing to do is to create the UI for the screens. It will be as simple as possible, a column with texts and
buttons.
Each screen will be a `@Composable` function that takes a ***component*** as a parameter.

```kotlin
@Composable
fun FirstScreen(
    component: FirstScreenComponent
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("First screen")
        Button(onClick = { component.click() }) {
            Text("Second Screen")
        }
    }
}
```

```kotlin
@Composable
fun SecondScreen(
    component: SecondScreenComponent
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("First screen")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Greetings: ${component.getGreeting()}")
        Button(onClick = { component.goBack() }) {
            Text("Go Back")
        }
    }
}   
```

The buttons are invoking functions that are provided via the components, as we remember that functions will trigger the
navigation in our ***rootComponent***.

The entrypoint to our application is the `App` function that will take the `RootComponent` as a parameter and handle the
navigation events from the ***childStack***.
Each platform ***iOS*** and ***Android*** will create the ***rootComponent*** and pass it to the ***App()*** function.

```kotlin
val childStack = rootComponent.childStack.subscribeAsState()
```

The decomposes ***Value*** can be transformed to the ***State*** by the `subscribeAsState()` function.
To handle upcoming changes in the stack the decompose provides special composable function called `Children` that takes
stack as a parameter, and can be configured using standard modifiers it also can use different type of transition
animations with the `StackAnimation`.
Last parameter of the ***Children*** function is lambda expression that will be called with every new child on the top
of the stack. This is the place where we can say how to display new components.

```kotlin
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
```

The last thing to to is to create the ***RootComponent*** in the platform specific code and pass it to the ***App()***
function.
For Android ti will be the `MainActivity` located in the `androidMain` and for iOS the `MainViewController` located
in `iosMain`.

For android we should use decomposes `retainedComponent()` function that will create the ***RootComponent*** and retain
it during the configuration changes. It also creates the ***componentContext***

```kotlin
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalDecomposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootComponent = retainedComponent { componentContext ->
            RootComponent(
                componentContext = componentContext
            )
        }
        setContent {
            App(rootComponent = rootComponent)
        }
    }
}
```

Since the iOS entry point is a composable function we will need to create ***componentContext*** by ourselves,
thankfully decompose got proper functions for it.
I will use the `DefaultComponentContext()` that takes the ***Lifecycle*** as a parameter which is also created by the
part of the decompose lib via the `LifecycleRegistry()`.
To prevent creating new components on each recomposition we should remember the instantiated component.

```kotlin
fun MainViewController() = ComposeUIViewController {
    val rootComponent = remember {
        RootComponent(
            componentContext = DefaultComponentContext(LifecycleRegistry())
        )
    }

    App(rootComponent)
}
```

That's all! We can now run the application on both Android and iOS devices and expect same behaviour!

![Navigation](/blog/images/3_navigation.gif "Navigation")