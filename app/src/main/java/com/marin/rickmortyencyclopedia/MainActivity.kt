package com.marin.rickmortyencyclopedia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.marin.rickmortyencyclopedia.ui.episode.EpisodesScreen
import com.marin.rickmortyencyclopedia.ui.theme.RickMortyEncyclopediaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController: NavHostController = rememberNavController()
            val onNavUp: () -> Unit = { navController.navigateUp() }
            RickMortyEncyclopediaTheme {
                NavHost(navController, startDestination = Screen.Episodes.route) {
                    composable(route = Screen.Episodes.route) {
                        EpisodesScreen()
                    }
                }
            }
        }
    }
}