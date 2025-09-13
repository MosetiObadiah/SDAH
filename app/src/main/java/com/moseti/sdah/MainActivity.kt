package com.moseti.sdah

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.moseti.sdah.data.HymnLoader
import com.moseti.sdah.models.Hymn
import com.moseti.sdah.navigation.AppNavHost
import com.moseti.sdah.ui.theme.SDAHTheme
import com.moseti.sdah.viewmodels.SingleHymnViewModel
import com.moseti.sdah.viewmodels.SingleHymnViewModelFactory
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {

    val hymnsList: List<Hymn> by lazy {
        HymnLoader.loadHymns(this)
    }

    val singleHymnViewModel: SingleHymnViewModel by viewModels {
        SingleHymnViewModelFactory(hymnsList)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SDAHTheme {
                val navController = rememberNavController()
                val navItems = remember {
                    listOf(
                        BottomNavItem.Home,
                        BottomNavItem.Hymnals,
                        BottomNavItem.PlayLists
                    )
                }
                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

                Scaffold(
                    topBar = {
                        LargeTopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = {
                                Text(
                                    "Large Top App Bar",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            actions = {
                                IconButton(onClick = { /* do something */ }) {
                                    Icon(
                                        imageVector = Icons.Filled.Menu,
                                        contentDescription = "Localized description"
                                    )
                                }
                            },
                            scrollBehavior = scrollBehavior
                        )
                    },

                    bottomBar = {
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
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    AppNavHost(singleHymnViewModel, navController, innerPadding)
                }
            }
        }
    }
}

sealed class BottomNavItem(
    val route: @Serializable Any,
    val identifier: String?,
    val titleRes: Int,
    val selectedIcon: Int,
    val unselectedIcon: Int
) {

    data object Home : BottomNavItem(
        route = com.moseti.sdah.navigation.Home,
        identifier = "Home",
        titleRes = R.string.bottom_nav_home,
        selectedIcon = R.drawable.home_svgrepo_com_selected,
        unselectedIcon = R.drawable.home_svgrepo_com
    )
    data object Hymnals : BottomNavItem(
        route = com.moseti.sdah.navigation.Hymnals,
        identifier = "Hymnals",
            titleRes = R.string.bottom_nav_hymnals,
        selectedIcon = R.drawable.books_svgrepo_com,
        unselectedIcon = R.drawable.books_svgrepo_com_unselected
    )
    data object PlayLists : BottomNavItem(
        route = com.moseti.sdah.navigation.PlayLists,
        identifier = "PlayLits",
            titleRes = R.string.bottom_nav_playlists,
        selectedIcon = R.drawable.playlist_svgrepo_com_selected,
        unselectedIcon = R.drawable.playlist_2_svgrepo_com
    )
}