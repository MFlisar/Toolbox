package com.michaelflisar.toolbox.app.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.toolbox.IconComposable
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.components.MyRow
import com.michaelflisar.toolbox.extensions.Render

abstract class PageScreensList : NavScreen() {

    sealed class Item {
        class Icon(
            val name: String,
            val icon: IconComposable,
            val screen: Screen
        ) : Item()

        class Text(
            val name: String,
            val iconText: String = name,
            val color: Color,
            val backgroundColor: Color,
            val screen: Screen
        ) : Item()
    }


    abstract val items: List<Item>
        @Composable get

    @Composable
    open fun Header() {
    }

    @Composable
    open fun Footer() {
    }

    @Composable
    override fun provideMenu(): List<MenuItem> = emptyList()

    @Composable
    override fun Screen() {
        Page(
            items = items,
            header = { Header() },
            footer = { Footer() }
        )
    }
}

@Composable
private fun Page(
    items: List<PageScreensList.Item>,
    header: @Composable () -> Unit,
    footer: @Composable () -> Unit,
) {
    MyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        header()
        MyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items.forEach {
                when (it) {
                    is PageScreensList.Item.Icon -> {
                        FunctionCard(
                            icon = { it.icon.Render(modifier = Modifier.size(48.dp)) },
                            label = it.name,
                            screen = it.screen
                        )
                    }

                    is PageScreensList.Item.Text -> {
                        FunctionCard(
                            icon = {
                                TextIcon(
                                    text = it.iconText,
                                    color = it.color,
                                    backgroundColor = it.backgroundColor,
                                    modifier = Modifier.size(48.dp)
                                )
                            },
                            label = it.name,
                            screen = it.screen
                        )
                    }
                }

            }
        }
        footer()
    }
}

@Composable
private fun FunctionCard(
    icon: @Composable () -> Unit,
    label: String,
    screen: Screen,
) {
    val navigator = LocalNavigator.currentOrThrow
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            navigator.push(screen)
        }
    ) {
        MyRow(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            itemSpacing = 16.dp
        ) {
            icon()
            Text(label, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun TextIcon(
    text: String,
    color: Color,
    backgroundColor: Color,
    modifier: Modifier,
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = color, fontWeight = FontWeight.Bold)
    }
}