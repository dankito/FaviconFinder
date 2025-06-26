package net.dankito.utils.favicon.location

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualByComparingTo
import assertk.assertions.isEqualTo
import kotlinx.coroutines.test.runTest
import net.dankito.utils.favicon.FaviconType
import net.dankito.web.client.KtorWebClient
import kotlin.test.Test

class StandardLocationFaviconFinderTest {

    private val underTest = StandardLocationFaviconFinder(KtorWebClient())


    @Test
    fun tryToFindStandardFavicon_nonFoundForSubdomain_TriesDomain() = runTest {
        val result = underTest.tryToFindStandardFavicon("https://staging.codinux.net", emptyList())

        assertThat(result).hasSize(1)

        val favicon = result.first()
        assertThat(favicon::url).isEqualTo("https://codinux.net/favicon.ico")
        assertThat(favicon::iconType).isEqualByComparingTo(FaviconType.ShortcutIcon)
    }

}