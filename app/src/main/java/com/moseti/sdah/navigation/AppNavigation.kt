package com.moseti.sdah.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.moseti.sdah.R
import com.moseti.sdah.ui.screens.HomeScreen
import com.moseti.sdah.ui.screens.HymnalScreen
import com.moseti.sdah.ui.screens.PlayListsScreen
import com.moseti.sdah.ui.screens.SingleHymnScreen
import com.moseti.sdah.viewmodels.HymnsViewModel
import kotlinx.serialization.Serializable

@Serializable object Hymns
@Serializable object Hymnals
@Serializable object PlayLists
@Serializable object SingleHymn

@Composable
fun AppNavHost(
    hymnsViewModel: HymnsViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Hymns,
        modifier = modifier
    ) {
        composable<Hymns> { HomeScreen(
            hymnsViewModel = hymnsViewModel,
            navController = navController,
            onHymnClick = { index ->
                // When a hymn is clicked, update the VM and navigate
                hymnsViewModel.selectHymn(index)
                navController.navigate(SingleHymn)
            }
        ) }
        composable<Hymnals> { HymnalScreen() }
        composable<PlayLists> { PlayListsScreen() }
        composable<SingleHymn> { SingleHymnScreen(
            hymnsViewModel = hymnsViewModel,
            onNavigateBack = {
                navController.popBackStack()
            }
        ) }
    }
}

sealed class BottomNavItem(
    val route: @Serializable Any,
    val identifier: String?,
    val titleRes: Int,
    val selectedIcon: Int,
    val unselectedIcon: Int
) {

    data object Hymns : BottomNavItem(
        route = com.moseti.sdah.navigation.Hymns,
        identifier = "Hymns",
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