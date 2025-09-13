package com.moseti.sdah

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.compose.rememberNavController
import com.moseti.sdah.data.HymnLoader
import com.moseti.sdah.models.Hymn
import com.moseti.sdah.navigation.AppNavHost
import com.moseti.sdah.ui.theme.SDAHTheme
import com.moseti.sdah.viewmodels.HymnsViewModel
import com.moseti.sdah.viewmodels.HymnsViewModelFactory

class MainActivity : ComponentActivity() {

    val hymnsList: List<Hymn> by lazy {
        HymnLoader.loadHymns(this)
    }

    val singleHymnViewModel: HymnsViewModel by viewModels {
        HymnsViewModelFactory(hymnsList)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SDAHTheme {
                val navController = rememberNavController()
                AppNavHost(
                    hymnsViewModel = singleHymnViewModel,
                    navController = navController
                )
            }
        }
    }
}