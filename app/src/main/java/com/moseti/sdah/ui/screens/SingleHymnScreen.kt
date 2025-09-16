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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.BottomAppBar
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.moseti.sdah.R
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
    val hymns = hymnsViewModel.allHymns
    val initialPage = hymnsViewModel.selectedHymnIndex.collectAsState().value ?: 0

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { hymns.value.size }
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

    val currentHymn = hymns.collectAsState().value.getOrNull(pagerState.currentPage)

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
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            painter = painterResource(R.drawable.outline_font_download_24),
                            contentDescription = "Font Control")
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            painter = painterResource(R.drawable.outline_playlist_add_24),
                            contentDescription = "Add to playlist",
                        )
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = "Add to favourites",
                        )
                    }
                }
            )
        },
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { pageIndex ->
            val hymn = hymns.collectAsState().value[pageIndex]
            HymnDetailContent(
                hymn = hymn,
                modifier = Modifier.graphicsLayer {
                    val pageOffset = (
                            (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
                            ).absoluteValue

                    alpha = lerp(
                        start = 0.5f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )

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
fun HymnDetailContent(hymn: Hymn, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
    ) {
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