/**
 * Created by Backbase RnD BV on 10/05/2025.
 */
package com.marin.rickmortyencyclopedia.data

import com.marin.rickmortyencyclopedia.data.impl.DefaultEpisodesRepository
import com.marin.rickmortyencyclopedia.data.impl.OkHttpEpisodesNetworkDataSource
import okhttp3.OkHttpClient


// For the purpose of this app, I don't see dependency injection libs as necessary.
// Going for "vanilla" app-based DI.

interface AppContainer {

    val episodesRepository: EpisodesRepository
}

class DefaultAppContainer() : AppContainer {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient()
    }

    override val episodesRepository: EpisodesRepository by lazy {
        val networkDataSource = OkHttpEpisodesNetworkDataSource(okHttpClient)
        DefaultEpisodesRepository(networkDataSource)
    }

}