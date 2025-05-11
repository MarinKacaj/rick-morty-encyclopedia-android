/**
 * Created by Backbase RnD BV on 11/05/2025.
 */
package com.marin.rickmortyencyclopedia.data.impl

import com.marin.rickmortyencyclopedia.data.ApiException
import com.marin.rickmortyencyclopedia.data.EpisodesNetworkDataSource
import com.marin.rickmortyencyclopedia.model.Episodes
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.serialization.json.Json


class OkHttpEpisodesNetworkDataSource(
    val okHttpClient: OkHttpClient,
    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    val parsingDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : EpisodesNetworkDataSource {

    override suspend fun getEpisodes(from: String): Result<Episodes> {
        val request = Request.Builder().url(from).build()
        return withContext(ioDispatcher) {
            try {
                val response = okHttpClient.newCall(request).execute()
                val responseBody = response.body
                if (response.isSuccessful && responseBody != null) {
                    // It's not nice to keep Dispatchers.IO busy with computationally-intensive work
                    withContext(parsingDispatcher) {
                        val episodes = Json.decodeFromString<Episodes>(responseBody.string())
                        Result.success(episodes)
                    }
                } else {
                    Result.failure(ApiException())
                }
            } catch (e: Exception) {
                Result.failure<Episodes>(ApiException(e.message))
            }
        }
    }
}