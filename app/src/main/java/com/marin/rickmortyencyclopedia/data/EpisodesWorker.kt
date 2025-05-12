/**
 * Created by Backbase RnD BV on 12/05/2025.
 */
package com.marin.rickmortyencyclopedia.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.marin.rickmortyencyclopedia.RickMortyEncyclopediaApp


class EpisodesWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repository =
            (applicationContext as RickMortyEncyclopediaApp).appContainer.episodesRepository
        val result = repository.refreshAll(first = "${AppContainer.BASE_API_URL}/episode")
        return if (result.isSuccess) Result.success() else Result.failure()
    }
}