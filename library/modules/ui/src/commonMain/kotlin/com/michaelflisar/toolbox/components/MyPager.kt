package com.michaelflisar.toolbox.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.padding

import kotlinx.coroutines.launch

object MyPagerDefaults {

    @Composable
    fun Tab(text: String) {
        Text(text = text, modifier = Modifier.padding(MaterialTheme.padding.default))
    }

}

@Composable
fun <T> MyPager(
    pages: List<T>,
    tab: @Composable (page: T) -> Unit,
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState { pages.size },
    pageContent: @Composable PagerScope.(page: Int) -> Unit,
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
    ) {
        // Tabs
        TabRow(
            selectedTabIndex = pagerState.currentPage, modifier = Modifier.fillMaxWidth()
        ) {
            repeat(pages.size) { index ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.scrollToPage(index) } }) {
                    tab(pages[index])
                }
            }
        }

        // Pager
        HorizontalPager(
            state = pagerState, modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            pageContent(it)
        }
    }
}