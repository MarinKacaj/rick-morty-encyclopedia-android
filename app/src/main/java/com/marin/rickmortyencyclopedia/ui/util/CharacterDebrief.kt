/**
 * Created by Backbase RnD BV on 12/05/2025.
 */
package com.marin.rickmortyencyclopedia.ui.util

import com.marin.rickmortyencyclopedia.model.Character


fun Character.debrief(): String {
    // name, status, species, name of the origin and the total number of episodes
    return """
        Name: $name
        Status: $status
        Species: $species
        Name of the origin: ${origin.name}
        Total number of episodes: ${episode.size}
    """.trimIndent()
}