# Favicon Finder

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.dankito.utils/favicon-finder/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.dankito.utils/favicon-finder)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Getting a site's favicon can be cumbersome.
There are multiple favicon sources and formats (see [Favicon sources and formats](#favicon-sources-and-formats)),
icons may are specified but do not exist (return a 404),
it may is hard to determine a favicon's size and type, and and and.

This library helps to easily get a website's favicons, 
either by returning the best matching favicon or a list of available favicons.


## Setup

### Gradle

```
implementation("net.dankito.utils:favicon-finder:1.5.2")
```

### Maven

```xml
<dependency>
   <groupId>net.dankito.utils</groupId>
   <artifactId>favicon-finder</artifactId>
   <version>1.5.2</version>
</dependency>
```

## Usage

### Get list of site's favicons

```kotlin
List<Favicon> = FaviconFinder().getFaviconsForUrl("wikipedia.org")
```

This returns a list of `Favicon` containing its `url`, `type` and if available `size` amd `mimeType`.


### Get best matching favicon for a site

If you want to get the image bytes of the best matching favicon directly,
you can use one of our many `FaviconFetcher` implementations like `GoogleFaviconFetcher`, `DuckDuckGoFaviconFetcher`, ...:

```kotlin
ByteArray? imageBytes = GoogleFaviconFetcher().fetch("wikipedia.org", 32)
```

Or use `FaviconFetcherSelector` that selects best fitting `FaviconFetcher`:

```kotlin
ByteArray? imageBytes = FaviconFetcherSelector().firstMatching("wikipedia.org", 32)
```

For all `FaviconFetcher` implementations see folder [src/main/kotlin/net/dankito/utils/favicon/fetcher](./src/main/kotlin/net/dankito/utils/favicon/fetcher).


## Favicon sources and formats

It can contain multiple favicons for different purposes, sizes and formats:

- `<link rel="icon" href="/favicon.ico" type="image/x-icon">`  
Nowadays default way to declare a favicon used by browser e.g. for tab icon, bookmarks, ...     
There can be multiple with different sized and image types like `.ico`, `.png`, `.svg`, `.gif`, etc.

- `<link rel="shortcut icon" href="/favicon.ico">`  
Former standard way introduced by Internet Explorer, legacy nowadays.  
File is expected to be a `.ico`.

- `<link rel="manifest" href="/site.webmanifest" />`  
Web Manifest, a `.json` with multiple icons used for PWAs and add to Home Screen on Android.  
`PNG` recommended, supports `maskable`.

- `<link rel="apple-touch-icon" href="/apple-touch-icon.png"/>`  
Used on iOS/iPadOS when a site gets added home screen.  
There can be multiple of them with different `sizes` attribute. Supports only `PNG`.

- `<link rel="apple-touch-icon-precomposed" ...>`  
Deprecated, used by iOS 6 and earlier. Like `apple-touch-icon`, but tells iOS not to apply any styling (like gloss or rounded corners).

- `<link rel="mask-icon" href="..." color="...">`  
Pinned tabs in Safari (macOS/iOS).  
SVG only. Monochrome; Safari fills the path with the given `color`.

- `<meta name="msapplication-TileImage" content="...">`  
Windows 8+ tile image used when pinned to start screen.

- `<meta property="og:image" ...>`  
Social media preview image.

- `/favicon.ico` (implicit)  
Automatic fallback by all browsers if no icons are defined.

- `<link class="js-site-favicon" ...>`  
Dynamically changed favicons via JS (e.g. GitHub switches between favicon + notification icon).

- `<meta name="msapplication-config" content="/browserconfig.xml">`  
Windows 8 and 10 live tiles, largely obsolete. Not implemented by Favicon Finder.


## License
```
Copyright 2017 dankito

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```