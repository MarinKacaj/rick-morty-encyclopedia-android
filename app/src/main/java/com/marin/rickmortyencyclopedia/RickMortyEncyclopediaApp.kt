/**
 * Created by Backbase RnD BV on 10/05/2025.
 */
package com.marin.rickmortyencyclopedia

import android.app.Application
import com.marin.rickmortyencyclopedia.data.AppContainer
import com.marin.rickmortyencyclopedia.data.DefaultAppContainer


class RickMortyEncyclopediaApp : Application() {

    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = DefaultAppContainer()
    }
}