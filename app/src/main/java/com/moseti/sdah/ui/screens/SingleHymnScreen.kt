package com.moseti.sdah.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.moseti.sdah.models.Hymn
import com.moseti.sdah.viewmodels.HymnsViewModel
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SingleHymnScreen(
    hymnsViewModel: HymnsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // ... (All your existing state management code remains the same)
    val hymns = hymnsViewModel.allHymns
    val initialPage = hymnsViewModel.selectedHymnIndex.collectAsState().value ?: 0

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { hymns.size }
    )
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            hymnsViewModel.selectHymn(pagerState.currentPage)
        }
    }

    LaunchedEffect(initialPage) {
        if (pagerState.currentPage != initialPage) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(initialPage)
            }
        }
    }

    val currentHymn = hymns.getOrNull(pagerState.currentPage)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = currentHymn?.let { "${it.number}. ${it.title}" } ?: "Hymn") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to list"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { pageIndex ->
            // This is where we add the scroll effect!
            val hymn = hymns[pageIndex]
            HymnDetailContent(
                hymn = hymn,
                // NEW: Add a graphicsLayer modifier to apply transformations
                modifier = Modifier.graphicsLayer {
                    // 1. Calculate the offset of the page from the center
                    // This will be 0 for the current page, -1 for the page to the left, 1 for the page to the right
                    // and fractional values in between as you scroll.
                    val pageOffset = (
                            (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
                            ).absoluteValue

                    // 2. Animate the alpha (fade) based on the offset
                    // The page in the center has alpha 1 (fully visible)
                    // The pages next to it will have alpha 0.5f (partially faded)
                    alpha = lerp(
                        start = 0.5f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )

                    // 3. Animate the scale based on the offset
                    // The page in the center has scale 1 (full size)
                    // The pages next to it will be 85% of the size
                    val scale = lerp(
                        start = 0.85f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                    scaleX = scale
                    scaleY = scale
                }
            )
        }
    }
}

@Composable
fun HymnDetailContent(hymn: Hymn, modifier: Modifier = Modifier) { // UPDATED: Accept a modifier
    LazyColumn(
        // UPDATED: Apply the passed-in modifier here
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // ... (The rest of this function is exactly the same)
        items(hymn.verses) { verse ->
            if (verse.lines.isNotEmpty()) {
                Text(
                    text = "Verse ${verse.number}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                verse.lines.forEach { line ->
                    Text(text = line, fontSize = 16.sp, lineHeight = 24.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        hymn.chorus?.let { chorus ->
            if (chorus.lines.isNotEmpty()) {
                item {
                    Text(
                        text = "Chorus",
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    chorus.lines.forEach { line ->
                        Text(
                            text = line,
                            fontStyle = FontStyle.Italic,
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}