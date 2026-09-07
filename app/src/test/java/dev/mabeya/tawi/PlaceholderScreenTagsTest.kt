package dev.mabeya.tawi

import dev.mabeya.tawi.ui.PlaceholderScreenTags
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * JVM-side guard for the test tags the UI tests match on: a duplicated or blank tag would make the
 * instrumented assertions match the wrong node (or ambiguously fail) rather than fail loudly here.
 */
class PlaceholderScreenTagsTest {

    private val tags = listOf(
        PlaceholderScreenTags.ROOT,
        PlaceholderScreenTags.HEADLINE,
        PlaceholderScreenTags.TAGLINE
    )

    @Test
    fun tagsAreNotBlank() {
        assertTrue(tags.none { it.isBlank() })
    }

    @Test
    fun tagsAreUnique() {
        assertEquals(tags.size, tags.distinct().size)
    }
}
