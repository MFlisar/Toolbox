package com.michaelflisar.toolbox.windowsapp.tmp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun rememberMyPagerState(
    initialPage: Int,
    pages: Int
): MyPagerState {
    return MyPagerState(pages, remember { mutableStateOf(initialPage) })
}

@Immutable
data class MyPagerState(
    val pages: Int,
    internal val page: MutableState<Int>
) {
    suspend fun scrollToPage(page: Int) {
        this.page.value = page.coerceIn(0, pages - 1)
    }

    val currentPage: Int
        get() = page.value
}

@Composable
fun <T> MyHorizontalPager(
    state: MyPagerState,
    data: List<T>,
    modifier: Modifier = Modifier,
    content: @Composable (index: Int, item: T) -> Unit
) {
    if (data.isNotEmpty()) {
        // Custom
        key(state.page.value) {
            Box(modifier = modifier) {
                content(state.page.value, data[state.page.value])
            }
        }

        //
        //HorizontalPager(
        //    modifier = modifier,
        //    state = pagerState
        //) {
        //    content(it, data[it])
        //}
    }
}