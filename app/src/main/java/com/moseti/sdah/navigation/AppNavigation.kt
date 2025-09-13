package com.moseti.sdah.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.moseti.sdah.ui.screens.HymnsListScreen
import com.moseti.sdah.ui.screens.SingleHymnScreen
import com.moseti.sdah.viewmodels.HymnsViewModel
import kotlinx.serialization.Serializable

@Serializable object Hymns
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
        composable<Hymns> { HymnsListScreen(
            hymnsViewModel = hymnsViewModel,
            onHymnClick = { index ->
                // When a hymn is clicked, update the VM and navigate
                hymnsViewModel.selectHymn(index)
                navController.navigate(SingleHymn)
            }
        ) }
        composable<SingleHymn> { SingleHymnScreen(
            hymnsViewModel = hymnsViewModel,
            onNavigateBack = {
                navController.popBackStack()
            }
        ) }
    }
}