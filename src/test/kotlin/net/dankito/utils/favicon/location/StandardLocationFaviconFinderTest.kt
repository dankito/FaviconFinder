package net.dankito.utils.favicon.location

import assertk.assertThat
import assertk.assertions.isEqualByComparingTo
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import net.dankito.utils.favicon.FaviconType
import kotlin.test.Test

class StandardLocationFaviconFinderTest {

    private val underTest = StandardLocationFaviconFinder()


    @Test
    fun tryToFindStandardFavicon_nonFoundForSubdomain_TriesDomain() {
        val result = underTest.tryToFindStandardFavicon("https://staging.codinux.net", emptyList())

        assertThat(result).isNotNull()
        assertThat(result!!::url).isEqualTo("https://codinux.net/favicon.ico")
        assertThat(result::iconType).isEqualByComparingTo(FaviconType.ShortcutIcon)
    }

}