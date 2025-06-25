# Favicon Finder

## Setup

### Gradle

```
implementation("net.dankito.utils:favicon-finder:1.5.0")
```

### Maven

```xml
<dependency>
   <groupId>net.dankito.utils</groupId>
   <artifactId>favicon-finder</artifactId>
   <version>1.5.0</version>
</dependency>
```


## Favicon formats and sources

Getting a site's favicon can be cumbersome.

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
