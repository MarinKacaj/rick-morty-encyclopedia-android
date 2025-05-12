/**
 * Created by Backbase RnD BV on 12/05/2025.
 */
package com.marin.rickmortyencyclopedia.ui.episode

import com.marin.rickmortyencyclopedia.model.Episode

fun fullyJoinEpisodesPreferNew(existing: List<Episode>, new: List<Episode>): List<Episode> {
    // easy lookups in new episodes to check if any has been updated
    // why?
    // the background worker could have updated any episode(s) as we load new pages
    val idNewEpisodes: MutableMap<Int, Episode> =
        new.associate { it.id to it }
            .toMutableMap()

    val combined = existing + new
    val mapOfIds = combined.groupBy { it.id }
    val fullyJoinedEpisodes: List<Episode> =
        mapOfIds.entries.map { entry: Map.Entry<Int, List<Episode>> ->
            // setting as a val for clarity
            val id: Int = entry.key
            // there's at least one, otherwise how did we get the id first place?
            val existingEpisode = entry.value.first()
            // is there any episode withe same id in the new list?
            val newEpisodeWithId: Episode? = idNewEpisodes[id]
            // update episode if in the new list or use existing otherwise
            newEpisodeWithId ?: existingEpisode
        }

    return fullyJoinedEpisodes
}