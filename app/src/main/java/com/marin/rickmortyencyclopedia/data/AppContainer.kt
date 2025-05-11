/**
 * Created by Backbase RnD BV on 10/05/2025.
 */
package com.marin.rickmortyencyclopedia.data

import com.marin.rickmortyencyclopedia.data.AppContainer.Companion.BASE_API_URL
import com.marin.rickmortyencyclopedia.data.impl.CharacterNetworkDataSource
import com.marin.rickmortyencyclopedia.data.impl.DefaultCharacterRepository
import com.marin.rickmortyencyclopedia.data.impl.DefaultEpisodesRepository
import com.marin.rickmortyencyclopedia.data.impl.EpisodesNetworkDataSource
import okhttp3.OkHttpClient


// For the purpose of this app, I don't see dependency injection libs as necessary.
// Going for "vanilla" app-based DI.

interface AppContainer {

    companion object {
        const val BASE_API_URL = "https://rickandmortyapi.com/api"
    }

    val episodesRepository: EpisodesRepository
    val characterRepository: CharacterRepository
}

class DefaultAppContainer() : AppContainer {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient()
    }

    override val episodesRepository: EpisodesRepository by lazy {
        val networkDataSource = EpisodesNetworkDataSource(okHttpClient)
        DefaultEpisodesRepository(networkDataSource)
    }

    override val characterRepository: CharacterRepository by lazy {
        val networkDataSource = CharacterNetworkDataSource(BASE_API_URL, okHttpClient)
        DefaultCharacterRepository(networkDataSource)
    }
}