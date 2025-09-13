package com.moseti.sdah.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.moseti.sdah.models.Hymn
import com.moseti.sdah.navigation.BottomNavItem
import com.moseti.sdah.viewmodels.HymnsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    hymnsViewModel: HymnsViewModel,
    navController: NavHostController,
    onHymnClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val hymns = hymnsViewModel.allHymns
    val listState = rememberLazyListState()
    val isScrollingDown = listState.isScrollingDown()

    val navItems = remember {
        listOf(
            BottomNavItem.Hymns,
            BottomNavItem.Hymnals,
            BottomNavItem.PlayLists
        )
    }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Hymns") })
        },

        bottomBar = {
            // Wrap the NavigationBar in AnimatedVisibility
            AnimatedVisibility(
                visible = !isScrollingDown,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                // --- PASTE YOUR NavigationBar LOGIC FROM MainActivity HERE ---
                AppBottomNavigationBar(navController = navController)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // CHANGED: Using itemsIndexed with key and contentType
            itemsIndexed(
                items = hymns,
                // OPTIMIZATION 1: Provide a stable, unique key
                key = { _, hymn -> hymn.number },
                // OPTIMIZATION 3: Specify the item type for better recycling
                contentType = { _, _ -> "HymnListItem" }
            ) { index, hymn ->
                HymnListItem(
                    hymn = hymn,
                    // Pass the index directly to the onHymnClick function reference
                    // This avoids creating a new lambda for each item.
                    onClick = { onHymnClick(index) }
                )
                Divider()
            }
        }
    }
}

@Composable
fun AppBottomNavigationBar(navController: NavHostController) {
    val navItems = remember {
        listOf(BottomNavItem.Hymns, BottomNavItem.Hymnals, BottomNavItem.PlayLists)
    }

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        navItems.forEach { item ->
            // TODO fix the icon not changing on navigation
            val isSelected = currentDestination?.hierarchy?.any { it.route == item.identifier } == true
            NavigationBarItem(
                icon = {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(id = if (isSelected) item.selectedIcon else item.unselectedIcon),
                        contentDescription = stringResource(id = item.titleRes)
                    )
                },
                label = { Text(stringResource(id = item.titleRes)) },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to avoid building up a large back stack
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when re-selecting the same item
                        launchSingleTop = true
                        // Restore state when re-selecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
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
    onClick: () -> Unit, // The onClick lambda is now stable
    modifier: Modifier = Modifier
) {
    // The implementation of this composable does not need to change,
    // as it was already receiving a stable lambda. The change was in the calling site.
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