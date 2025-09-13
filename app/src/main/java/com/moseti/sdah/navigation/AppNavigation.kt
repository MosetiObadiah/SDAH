package com.moseti.sdah.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.moseti.sdah.ui.screens.HomeScreen
import com.moseti.sdah.ui.screens.HymnalScreen
import com.moseti.sdah.ui.screens.PlayListsScreen
import com.moseti.sdah.ui.screens.SingleHymnScreen
import com.moseti.sdah.viewmodels.SingleHymnViewModel
import kotlinx.serialization.Serializable

@Serializable object Home
@Serializable object Hymnals
@Serializable object PlayLists
@Serializable object SingleHymn

@Composable
fun AppNavHost(
    singleHymnViewModel: SingleHymnViewModel,
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = Home,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable<Home> { HomeScreen() }
        composable<Hymnals> { HymnalScreen() }
        composable<PlayLists> { PlayListsScreen() }
        composable<SingleHymn> { SingleHymnScreen(singleHymnViewModel) }
    }
}