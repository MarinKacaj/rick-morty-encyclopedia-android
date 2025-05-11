/**
 * Created by Backbase RnD BV on 11/05/2025.
 */
package com.marin.rickmortyencyclopedia.data.impl

import com.marin.rickmortyencyclopedia.data.ApiException
import com.marin.rickmortyencyclopedia.data.CharacterDataSource
import com.marin.rickmortyencyclopedia.model.Character
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request


class CharacterNetworkDataSource(
    val baseApiUrl: String,
    val okHttpClient: OkHttpClient,
    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    val parsingDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : CharacterDataSource {

    override suspend fun getCharacter(id: Int): Result<Character> {
        val request = Request.Builder().url("$baseApiUrl/character/$id").build()
        return withContext(ioDispatcher) {
            try {
                val response = okHttpClient.newCall(request).execute()
                val responseBody = response.body
                if (response.isSuccessful && responseBody != null) {
                    // It's not nice to keep Dispatchers.IO busy with computationally-intensive work
                    withContext(parsingDispatcher) {
                        val character = Json.decodeFromString<Character>(responseBody.string())
                        Result.success(character)
                    }
                } else {
                    Result.failure(ApiException())
                }
            } catch (e: Exception) {
                Result.failure<Character>(ApiException(e.message))
            }
        }
    }
}