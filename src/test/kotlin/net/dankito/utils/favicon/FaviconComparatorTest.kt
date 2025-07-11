package net.dankito.utils.favicon

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import kotlin.test.Test


class FaviconComparatorTest {

    private val underTest = FaviconComparator()


    @Test
    fun getBestIconForWikipedia() {

        // given
        val bestIcon = Favicon("https://www.wikipedia.org/static/apple-touch/wikipedia.png", FaviconType.AppleTouch)
        val favicons = listOf(Favicon("https://www.wikipedia.org/static/favicon/wikipedia.ico", FaviconType.ShortcutIcon), bestIcon)


        // when
        val result = underTest.getBestIcon(favicons)


        // then
        assertThat(result).isEqualTo(bestIcon)
    }

    @Test
    fun getBestIconForGuardian() {

        // given
        val bestIcon = Favicon("https://assets.guim.co.uk/images/2170b16eb045a34f8c79761b203627b4/fallback-logo.png", FaviconType.OpenGraphImage)
        val favicons = listOf(Favicon("https://assets.guim.co.uk/images/favicons/451963ac2e23633472bf48e2856d3f04/152x152.png", FaviconType.AppleTouch, Size(152, 152)),
                Favicon("https://assets.guim.co.uk/images/favicons/1a3f98d8491f8cfdc224089b785da86b/144x144.png", FaviconType.AppleTouch, Size(144, 144)),
                Favicon("https://assets.guim.co.uk/images/favicons/cf23080600002e50f5869c72f5a904bd/120x120.png", FaviconType.AppleTouch, Size(120, 120)),
                Favicon("https://assets.guim.co.uk/images/favicons/f438f6041a4c1d0289e6debd112880c2/114x114.png", FaviconType.AppleTouch, Size(114, 114)),
                Favicon("https://assets.guim.co.uk/images/favicons/b5050517955e7cf1e493ccc53e64ca05/72x72.png", FaviconType.AppleTouch, Size(72, 72)),
                Favicon("https://assets.guim.co.uk/images/favicons/4fd650035a2cebafea4e210990874c64/57x57.png", FaviconType.AppleTouchPrecomposed),
                Favicon("https://assets.guim.co.uk/images/favicons/79d7ab5a729562cebca9c6a13c324f0e/32x32.ico", FaviconType.ShortcutIcon, imageMimeType = "image/png"),
                Favicon("https://assets.guim.co.uk/images/favicons/f06f6996e193d1ddcd614ea852322d25/windows_tile_144_b.png", FaviconType.MsTileImage),
                bestIcon)


        // when
        val result = underTest.getBestIcon(favicons)


        // then
        assertThat(result).isEqualTo(bestIcon)
    }

    @Test
    fun getBestIconForGuardianIncludingRssIcon() {

        // given
        val bestIcon = Favicon("https://assets.guim.co.uk/images/2170b16eb045a34f8c79761b203627b4/fallback-logo.png", FaviconType.OpenGraphImage)
        val favicons = listOf(Favicon("https://assets.guim.co.uk/images/guardian-logo-rss.c45beb1bafa34b347ac333af2e6fe23f.png", FaviconType.Icon, Size(250, 40)),
                Favicon("https://assets.guim.co.uk/images/favicons/451963ac2e23633472bf48e2856d3f04/152x152.png", FaviconType.AppleTouch, Size(152, 152)),
                Favicon("https://assets.guim.co.uk/images/favicons/1a3f98d8491f8cfdc224089b785da86b/144x144.png", FaviconType.AppleTouch, Size(144, 144)),
                Favicon("https://assets.guim.co.uk/images/favicons/cf23080600002e50f5869c72f5a904bd/120x120.png", FaviconType.AppleTouch, Size(120, 120)),
                Favicon("https://assets.guim.co.uk/images/favicons/f438f6041a4c1d0289e6debd112880c2/114x114.png", FaviconType.AppleTouch, Size(114, 114)),
                Favicon("https://assets.guim.co.uk/images/favicons/b5050517955e7cf1e493ccc53e64ca05/72x72.png", FaviconType.AppleTouch, Size(72, 72)),
                Favicon("https://assets.guim.co.uk/images/favicons/4fd650035a2cebafea4e210990874c64/57x57.png", FaviconType.AppleTouchPrecomposed),
                Favicon("https://assets.guim.co.uk/images/favicons/79d7ab5a729562cebca9c6a13c324f0e/32x32.ico", FaviconType.ShortcutIcon, imageMimeType = "image/png"),
                Favicon("https://assets.guim.co.uk/images/favicons/f06f6996e193d1ddcd614ea852322d25/windows_tile_144_b.png", FaviconType.MsTileImage),
                bestIcon)


        // when
        val result = underTest.getBestIcon(favicons, returnSquarishOneIfPossible = false)


        // then
        assertThat(result).isEqualTo(bestIcon)
    }

    @Test
    fun getBestSquareIconForGuardianIncludingRssIcon() {

        // given
        val bestIcon = Favicon("https://assets.guim.co.uk/images/favicons/451963ac2e23633472bf48e2856d3f04/152x152.png", FaviconType.AppleTouch, Size(152, 152))
        val favicons = listOf(Favicon("https://assets.guim.co.uk/images/guardian-logo-rss.c45beb1bafa34b347ac333af2e6fe23f.png", FaviconType.Icon, Size(250, 40)),
                Favicon("https://assets.guim.co.uk/images/favicons/1a3f98d8491f8cfdc224089b785da86b/144x144.png", FaviconType.AppleTouch, Size(144, 144)),
                Favicon("https://assets.guim.co.uk/images/favicons/cf23080600002e50f5869c72f5a904bd/120x120.png", FaviconType.AppleTouch, Size(120, 120)),
                Favicon("https://assets.guim.co.uk/images/favicons/f438f6041a4c1d0289e6debd112880c2/114x114.png", FaviconType.AppleTouch, Size(114, 114)),
                Favicon("https://assets.guim.co.uk/images/favicons/b5050517955e7cf1e493ccc53e64ca05/72x72.png", FaviconType.AppleTouch, Size(72, 72)),
                Favicon("https://assets.guim.co.uk/images/favicons/4fd650035a2cebafea4e210990874c64/57x57.png", FaviconType.AppleTouchPrecomposed),
                Favicon("https://assets.guim.co.uk/images/favicons/79d7ab5a729562cebca9c6a13c324f0e/32x32.ico", FaviconType.ShortcutIcon, imageMimeType = "image/png"),
                Favicon("https://assets.guim.co.uk/images/favicons/f06f6996e193d1ddcd614ea852322d25/windows_tile_144_b.png", FaviconType.MsTileImage),
                Favicon("https://assets.guim.co.uk/images/2170b16eb045a34f8c79761b203627b4/fallback-logo.png", FaviconType.OpenGraphImage),
                bestIcon)


        // when
        val result = underTest.getBestIcon(favicons, returnSquarishOneIfPossible = true)


        // then
        assertThat(result).isEqualTo(bestIcon)
    }

    @Test
    fun getBestIconForNewYorkTimes() {

        // given
        val bestIcon = Favicon("https://static01.nyt.com/images/icons/t_logo_291_black.png", FaviconType.OpenGraphImage)
        val favicons = listOf(Favicon("https://static01.nyt.com/favicon.ico", FaviconType.ShortcutIcon),
                                        Favicon("https://static01.nyt.com/images/icons/ios-ipad-144x144.png", FaviconType.AppleTouchPrecomposed, Size(144, 144)),
                                        Favicon("https://static01.nyt.com/images/icons/ios-iphone-114x144.png", FaviconType.AppleTouchPrecomposed, Size(114, 114)),
                                        Favicon("https://static01.nyt.com/images/icons/ios-default-homescreen-57x57.png", FaviconType.AppleTouchPrecomposed),
                                        bestIcon)


        // when
        val result = underTest.getBestIcon(favicons)


        // then
        assertThat(result).isEqualTo(bestIcon)
    }

    @Test
    fun getBestIconWithMaxSize152ForNewYorkTimes() {

        // given
        val bestIcon = Favicon("https://static01.nyt.com/images/icons/ios-ipad-144x144.png", FaviconType.AppleTouchPrecomposed, Size(144, 144))
        val favicons = listOf(Favicon("https://static01.nyt.com/favicon.ico", FaviconType.ShortcutIcon),
                Favicon("https://static01.nyt.com/images/icons/t_logo_291_black.png", FaviconType.OpenGraphImage),
                Favicon("https://static01.nyt.com/images/icons/ios-iphone-114x144.png", FaviconType.AppleTouchPrecomposed, Size(114, 114)),
                Favicon("https://static01.nyt.com/images/icons/ios-default-homescreen-57x57.png", FaviconType.AppleTouchPrecomposed),
                bestIcon)


        // when
        val result = underTest.getBestIcon(favicons, minSize = 32, maxSize = 152)


        // then
        assertThat(result).isEqualTo(bestIcon)
    }

    @Test
    fun getBestIconWithMaxSize112ForNewYorkTimes() {

        // given
        val bestIcon = Favicon("https://static01.nyt.com/images/icons/ios-default-homescreen-57x57.png", FaviconType.AppleTouchPrecomposed)
        val favicons = listOf(Favicon("https://static01.nyt.com/favicon.ico", FaviconType.ShortcutIcon),
                Favicon("https://static01.nyt.com/images/icons/t_logo_291_black.png", FaviconType.OpenGraphImage),
                Favicon("https://static01.nyt.com/images/icons/ios-ipad-144x144.png", FaviconType.AppleTouchPrecomposed, Size(144, 144)),
                Favicon("https://static01.nyt.com/images/icons/ios-iphone-114x144.png", FaviconType.AppleTouchPrecomposed, Size(114, 114)),
                bestIcon)


        // when
        val result = underTest.getBestIcon(favicons, minSize = 32, maxSize = 112)


        // then
        assertThat(result).isEqualTo(bestIcon)
    }

    @Test
    fun getBestIconForDieZeit() {

        // given
        val bestIcon = Favicon("https://img.zeit.de/static/img/ZO-ipad-114x114.png", FaviconType.MsTileImage)
        val favicons = listOf(
                Favicon("https://www.zeit.de/favicon.ico", FaviconType.ShortcutIcon, Size(32, 32)),
                Favicon("https://img.zeit.de/static/img/ZO-ipad-114x114.png", FaviconType.MsTileImage),
                bestIcon
        )


        // when
        val result = underTest.getBestIcon(favicons)


        // then
        assertThat(result).isEqualTo(bestIcon)
    }

    @Test
    fun getBestIconForDieZeitIncludingRssIcon() {

        // given
        val bestIcon = Favicon("https://img.zeit.de/bilder/elemente_01_06/logos/homepage_top.gif", FaviconType.Icon)
        val favicons = listOf(
                Favicon("https://img.zeit.de/static/img/zo-icon-win8-144x144.png", FaviconType.MsTileImage),
                Favicon("https://www.zeit.de/favicon.ico", FaviconType.ShortcutIcon, Size(32, 32)),
                Favicon("https://img.zeit.de/static/img/ZO-ipad-114x114.png", FaviconType.AppleTouchPrecomposed),
                bestIcon
        )


        // when
        val result = underTest.getBestIcon(favicons)


        // then
        assertThat(result).isEqualTo(bestIcon)
    }

    @Test
    fun getBestSquareIconForDieZeitIncludingRssIcon() {

        // given
        val bestIcon = Favicon("https://img.zeit.de/static/img/ZO-ipad-114x114.png", FaviconType.MsTileImage)
        val favicons = listOf(
                Favicon("https://static.zeit.de/p/zeit.web/icons/favicon.svg", FaviconType.Icon),
                Favicon("https://www.zeit.de/favicon.ico", FaviconType.ShortcutIcon, Size(32, 32)),
                Favicon("https://img.zeit.de/static/img/ZO-ipad-114x114.png", FaviconType.MsTileImage),
                bestIcon
        )


        // when
        val result = underTest.getBestIcon(favicons, returnSquarishOneIfPossible = true)


        // then
        assertThat(result).isEqualTo(bestIcon)
    }

    @Test
    fun getBestIconForHeise() {

        // given
        val bestIcon = Favicon("https://www.heise.de/icons/ho/heise_online_facebook_social_graph.png", FaviconType.OpenGraphImage)
        val favicons = listOf(
                Favicon("https://www.heise.de/icons/ho/apple-touch-icon-152.png", FaviconType.AppleTouchPrecomposed, Size(152, 152)),
                Favicon("https://www.heise.de/favicon.ico", FaviconType.Icon),
                Favicon("https://www.heise.de/icons/ho/apple-touch-icon-60.png", FaviconType.AppleTouch, Size(60, 60)),
                Favicon("https://www.heise.de/icons/ho/apple-touch-icon-120.png", FaviconType.AppleTouch, Size(120, 120)),
                Favicon("https://www.heise.de/icons/ho/apple-touch-icon-76.png", FaviconType.AppleTouch, Size(76, 76)),
                bestIcon
        )


        // when
        val result = underTest.getBestIcon(favicons)


        // then
        assertThat(result).isEqualTo(bestIcon)
    }

    @Test
    fun getBestIconForPostillon() {

        // given
        val bestIcon = Favicon("http://4.bp.blogspot.com/-46xU6sntzl4/UVHLh1NGfwI/AAAAAAAAUlY/RiARs4-toWk/s800/Logo.jpg", FaviconType.OpenGraphImage)
        val favicons = listOf(Favicon("http://www.der-postillon.com/favicon.ico", FaviconType.Icon, imageMimeType = "image/x-icon"), bestIcon)


        // when
        val result = underTest.getBestIcon(favicons)


        // then
        assertThat(result).isEqualTo(bestIcon)
    }

    @Test
    fun getBestIconForPostillon_NoSquarishIconAvailable_ReturnsIconWithBestSizeThen() {

        // given
        val bestIcon = Favicon("http://4.bp.blogspot.com/-46xU6sntzl4/UVHLh1NGfwI/AAAAAAAAUlY/RiARs4-toWk/s800/Logo.jpg", FaviconType.OpenGraphImage)
        val favicons = listOf(Favicon("http://www.der-postillon.com/favicon.ico", FaviconType.Icon, imageMimeType = "image/x-icon"), bestIcon)


        // when
        val result = underTest.getBestIcon(favicons, returnSquarishOneIfPossible = true)


        // then
        assertThat(result).isEqualTo(bestIcon)
    }


    @Test
    fun excludeIco() {

        // given
        val bestIcon = Favicon("https://www.targobank.de/de/images/favicon/favicon.png", FaviconType.Icon)

        val favicons = listOf(
                Favicon("https://www.targobank.de/favicon.ico", FaviconType.ShortcutIcon),
                bestIcon
        )


        // when
        val result = underTest.getBestIcon(favicons, 32, 32 + 32, true, listOf(".ico"))


        // then
        assertThat(result).isEqualTo(bestIcon)
    }


    @Test
    fun getBestIconForPreferredSizes() {

        // given
        val bestIcon = Favicon("https://www.heise.de/icons/ho/apple-touch-icon-152.png", FaviconType.AppleTouchPrecomposed, Size(152, 152))
        val favicons = listOf(
            Favicon("https://www.heise.de/icons/ho/apple-touch-icon-120.png", FaviconType.AppleTouch, Size(120, 120)),
            Favicon("https://www.heise.de/icons/ho/apple-touch-icon-76.png", FaviconType.AppleTouch, Size(76, 76)),
            bestIcon,
            Favicon("https://www.heise.de/icons/ho/heise_online_facebook_social_graph.png", FaviconType.OpenGraphImage, Size(500, 500))
        )


        // when
        val result = underTest.getBestIcon(favicons, listOf(16, 32, 64, 128, 152))


        // then
        assertThat(result).isEqualTo(bestIcon)
    }

    @Test
    fun getBestIconForPreferredSizes_NoMatch() {

        // given
        val favicons = listOf(
            Favicon("https://www.heise.de/icons/ho/apple-touch-icon-120.png", FaviconType.AppleTouch, Size(120, 120)),
            Favicon("https://www.heise.de/icons/ho/apple-touch-icon-76.png", FaviconType.AppleTouch, Size(76, 76)),
            Favicon("https://www.heise.de/icons/ho/apple-touch-icon-152.png", FaviconType.AppleTouchPrecomposed, Size(152, 152)),
            Favicon("https://www.heise.de/icons/ho/heise_online_facebook_social_graph.png", FaviconType.OpenGraphImage, Size(500, 500))
        )


        // when
        val result = underTest.getBestIcon(favicons, listOf(16, 32, 64, 128))


        // then
        assertThat(result).isNull()
    }

    @Test
    fun getBestIconForPreferredSizes_NoMatch_ignoreParametersAsLastResort() {

        // given
        val bestIcon = Favicon("https://www.heise.de/icons/ho/apple-touch-icon-120.png", FaviconType.AppleTouch, Size(120, 120)) // matches closest 128
        val favicons = listOf(
            bestIcon,
            Favicon("https://www.heise.de/icons/ho/apple-touch-icon-76.png", FaviconType.AppleTouch, Size(76, 76)),
            Favicon("https://www.heise.de/icons/ho/apple-touch-icon-152.png", FaviconType.AppleTouchPrecomposed, Size(152, 152)),
            Favicon("https://www.heise.de/icons/ho/heise_online_facebook_social_graph.png", FaviconType.OpenGraphImage, Size(500, 500))
        )


        // when
        val result = underTest.getBestIcon(favicons, listOf(128, 256), ignoreParametersAsLastResort = true)


        // then
        assertThat(result).isEqualTo(bestIcon)
    }

}