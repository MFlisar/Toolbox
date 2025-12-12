package com.michaelflisar.toolbox.demo.pages.tests

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.components.MyButton
import com.michaelflisar.toolbox.components.MyCheckbox
import com.michaelflisar.toolbox.components.MyDropdown
import com.michaelflisar.toolbox.components.MyDropdownIndex
import com.michaelflisar.toolbox.components.MyInput
import com.michaelflisar.toolbox.components.MyInputButton
import com.michaelflisar.toolbox.components.MyNumberPicker
import com.michaelflisar.toolbox.components.MyRow
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.feature.translatablecontent.TranslatableContent
import com.michaelflisar.toolbox.numbers.rememberMyNumberParser
import com.michaelflisar.toolbox.numbers.rememberMyNumberValidator
import com.michaelflisar.toolbox.ui.MyScrollableColumn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Parcelize
object PageTestUI : NavScreen() {

    @Composable
    override fun provideData() = rememberNavScreenData(
        name = "UI",
        icon = Icons.Default.Visibility.toIconComposable()
    )

    @Composable
    override fun Screen() {
        Page()
    }

}

@Composable
private fun Page(
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    val navigator = LocalNavigator.currentOrThrow
    val appState = LocalAppState.current
    val scope = rememberCoroutineScope()

    MyScrollableColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(all = 16.dp)
            .fillMaxSize()
    ) {
        MyRow(
            modifier = Modifier
                .background(Color.Red)
                .padding(all = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MyDropdownIndex(
                modifier = Modifier.width(200.dp),
                title = "Dropdown",
                items = listOf("One", "Two", "Three"),
                selectedIndex = remember { mutableStateOf(0) },
                style = MyDropdown.Style.OutlinedButton()
            )
            MyInput(
                modifier = Modifier.width(128.dp),
                title = "Input",
                value = remember { mutableStateOf("") },
            )
            MyInputButton(
                title = "Button",
                modifier = Modifier.width(128.dp),
                value = "Text...",
                onClick = {
                    appState.showToast("InputButton clicked")
                }
            )
        }
        MyRow {
            MyDropdownIndex(
                modifier = Modifier.width(128.dp),
                title = "Button Style",
                items = listOf("One", "Two", "Three"),
                selectedIndex = remember { mutableStateOf(0) }
            )

            MyDropdown(
                modifier = Modifier.width(128.dp),
                title = "Button Style",
                items = listOf("One", "Two", "Three"),
                selected = remember { mutableStateOf("One") }
            )

            MyDropdown(
                modifier = Modifier.width(128.dp),
                title = "Button Style",
                items = listOf("One", "Two", "Three"),
                selected = "One",
                onSelectionChanged = {

                }
            )
        }

        val number = remember { mutableIntStateOf(5) }
        val validator = rememberMyNumberValidator(number.value, 1, 10)
        val parser = rememberMyNumberParser(number.value, 1, 1)
        MyNumberPicker(
            modifier = Modifier.fillMaxWidth(),
            //modifierInnerPicker = Modifier.fillMaxWidth(),
            validator = validator,
            parser = parser,
            label = "Number Picker",
            value = number,
            selectAllOnFocus = true
        )

        val stretch = remember { mutableStateOf(false) }
        val sides = TranslatableContent.Side.entries.toList()
        val state = rememberPagerState(0) { sides.size }
        MyCheckbox(
            title = "Stretch",
            checked = stretch
        )
        TabRow(
            selectedTabIndex = state.currentPage
        ) {
            sides.forEach {
                Tab(
                    selected = state.currentPage == it.ordinal,
                    onClick = { scope.launch { state.animateScrollToPage(it.ordinal) } },
                    text = { Text(it.name) }
                )
            }
        }
        HorizontalPager(
            state = state
        ) { page ->
            val side = sides[page]
            val translation = remember { Animatable(1f) }
            val modifier = Modifier
                .width(600.dp)
                .height(400.dp)
                .align(Alignment.CenterHorizontally)
            when (side) {
                TranslatableContent.Side.Left -> {
                    Row(
                        modifier = modifier,
                    ) {
                        TestTranslatableContent(side, translation, stretch.value)
                        TestContent(Modifier.fillMaxHeight().weight(1f), scope, translation)
                    }
                }

                TranslatableContent.Side.Right -> {
                    Row(
                        modifier = modifier,
                    ) {
                        TestContent(Modifier.fillMaxHeight().weight(1f), scope, translation)
                        TestTranslatableContent(side, translation, stretch.value)
                    }
                }

                TranslatableContent.Side.Top -> {
                    Column(
                        modifier = modifier,
                    ) {
                        TestTranslatableContent(side, translation, stretch.value)
                        TestContent(Modifier.fillMaxWidth().weight(1f), scope, translation)
                    }
                }

                TranslatableContent.Side.Bottom -> {
                    Column(
                        modifier = modifier,
                    ) {
                        TestContent(Modifier.fillMaxWidth().weight(1f), scope, translation)
                        TestTranslatableContent(side, translation, stretch.value)
                    }
                }
            }
        }
    }
}

// --------------------

@Composable
private fun TestTranslatableContent(
    side: TranslatableContent.Side,
    translation: Animatable<Float, *>,
    stretch: Boolean
) {
    TranslatableContent(
        modifier = Modifier,
        side = side,
        translation = translation.value
    ) {
        Text(
            modifier = Modifier
                .background(Color.Magenta)
                .border(2.dp, Color.Black)
                .stretch(stretch)
                .padding(16.dp),
            text = "${side.name} CONTENT"
        )
    }
}

@Composable
private fun TestContent(
    modifier: Modifier,
    scope: CoroutineScope,
    translation: Animatable<Float, *>
) {
    Column(
        modifier = modifier
            .background(Color.Blue)
            .border(2.dp, Color.Black)
            .padding(16.dp)
    ) {
        TestButtons(scope, translation)
    }
}

@Composable
fun ColumnScope.TestButtons(
    scope: CoroutineScope,
    translation: Animatable<Float, *>
) {
    MyRow {
        listOf(0f, 25f, 50f, 75f, 100f).forEach { v ->
            MyButton(
                text = "${v.roundToInt()}%",
                onClick = { scope.launch { translation.animateTo(v / 100f) } },
                modifier = Modifier.weight(1f)
            )
        }
    }
    Text(
        text = "State: ${(translation.value * 100).roundToInt()}%",
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )
}