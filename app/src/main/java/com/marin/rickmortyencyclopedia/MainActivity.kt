package com.marin.rickmortyencyclopedia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.marin.rickmortyencyclopedia.ui.character.details.CharacterDetailsScreen
import com.marin.rickmortyencyclopedia.ui.character.list.EpisodeCharactersIdsScreen
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
                NavHost(navController, startDestination = Screen.Episodes) {
                    composable<Screen.Episodes> {
                        EpisodesScreen { code: String, charactersIds: List<Int> ->
                            navController.navigate(Screen.Characters(charactersIds, code))
                        }
                    }
                    composable<Screen.Characters> {
                        val args = it.toRoute<Screen.Characters>()
                        EpisodeCharactersIdsScreen(
                            episodeCode = args.episodeCode,
                            charactersIds = args.charactersIds,
                            onCharacterIdSelected = {
                                navController.navigate(Screen.CharacterDetails(characterId = it))
                            },
                            onNavUp = onNavUp,
                        )
                    }
                    composable<Screen.CharacterDetails> {
                        val args = it.toRoute<Screen.CharacterDetails>()
                        CharacterDetailsScreen(
                            id = args.characterId,
                            onNavUp = onNavUp,
                        )
                    }
                }
            }
        }
    }
}