package com.moseti.sdah.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moseti.sdah.models.Hymn
import com.moseti.sdah.ui.components.NumberPadSheet
import com.moseti.sdah.viewmodels.HymnsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HymnsListScreen(
    hymnsViewModel: HymnsViewModel,
    onHymnClick: (Int) -> Unit,
) {
    val hymns = hymnsViewModel.allHymns
    val listState = rememberLazyListState()
    val isScrollingDown = listState.isScrollingDown()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val context = LocalContext.current

    var showNumpadSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = "Hymns") },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showNumpadSheet = true },
                expanded = !isScrollingDown,
                icon = { Icon(Icons.Filled.Search, contentDescription = "Open number pad") },
                text = { Text("Find") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            itemsIndexed(
                items = hymns,
                key = { _, hymn -> hymn.number }
            ) { index, hymn ->
                HymnListItem(
                    hymn = hymn,
                    onClick = { onHymnClick(index) }
                )
                HorizontalDivider()
            }
        }
    }

    NumberPadSheet(
        showSheet = showNumpadSheet,
        onDismiss = { showNumpadSheet = false },
        onHymnSelect = { hymnNumber ->
            val index = hymns.indexOfFirst { it.number == hymnNumber }
            if (index != -1) {
                onHymnClick(index)
            } else {
                Toast.makeText(context, "Hymn not found", Toast.LENGTH_SHORT).show()
            }
        }
    )
}
@Composable
private fun LazyListState.isScrollingDown(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex < firstVisibleItemIndex
            } else {
                previousScrollOffset < firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}
@Composable
fun HymnListItem(
    hymn: Hymn,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${hymn.number}.",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = hymn.title,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f)
        )
    }
}