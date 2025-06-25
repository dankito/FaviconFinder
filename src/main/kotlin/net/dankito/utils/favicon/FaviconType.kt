package net.dankito.utils.favicon


enum class FaviconType {

    /**
     * Primary favicon for browsers (tab icon, bookmarks, etc.).
     *
     * Can be multiple types: .ico, .png, .svg, .gif, etc.
     *
     * One can specify multiple <link rel="icon"> with different type and sizes. Browsers pick the most suitable one.
     *
     * Example:
     * `<link rel="icon" href="/favicon.ico" type="image/x-icon">`
     */
    Icon,

    /**
     * Originally for Internet Explorer 5–11. Legacy but still common.
     * Used by IE and Edge. Ignored by modern browsers unless it's the only icon.
     *
     * File is expected to be a `.ico`.
     *
     * Example:
     * `<link rel="shortcut icon" href="/favicon.ico">`
     */
    ShortcutIcon,

    /**
     * Shown on the home screen when a user installs a Progressive Web App (PWA) or the
     * user adds a site to home screen on Android.
     *
     * Must be declared in the `manifest.json` with purpose `"any"` (or omitted for default).
     * Image type should usually be PNG with sizes such as 192x192 or 512x512 pixels.
     */
    /**
     * Icon for Progressive Web Apps (PWAs) or Android when added to the home screen.
     *
     * Declared in `manifest.json` with `purpose: "any"` or omitted (default).
     * PNG recommended; typical sizes are 192x192 or 512x512.
     *
     * Example manifest entry:
     * `{ "src": "/android-chrome-192x192.png", "sizes": "192x192", "type": "image/png" }`
     */
    AndroidChrome,

    /**
     * Represents a maskable icon variant for Android Chrome web apps, defined in the Web App Manifest.
     *
     * Maskable icons allow full-bleed artwork that fits Android’s adaptive icon shapes (circles, squircles, etc.).
     * These icons must be declared in the `manifest.json` with `purpose: "maskable"` or `"any maskable"`.
     * Recommended size is 192x192 or higher, and image type should be PNG.
     *
     * Providing a maskable icon ensures better visual consistency on Android home screens and is highly recommended
     * for Progressive Web Apps.
     */
    /**
     * Maskable icon for PWAs and Android, allowing full-bleed artwork inside adaptive shapes (circle, squircle, etc.).
     *
     * Declared in `manifest.json` with `purpose: "maskable"` or `"any maskable"`.
     * Must be PNG; recommended size is 192x192 or larger.
     *
     * Example manifest entry:
     * `{ "src": "/android-chrome-maskable-192x192.png", "sizes": "192x192", "type": "image/png", "purpose": "maskable" }`
     */
    AndroidChromeMaskable,
    /**
     * Used by Safari on iOS/iPadOS home screen when "Add to Home Screen" is used.
     *
     * Supports only PNG.
     *
     * One can specify multiple with different `sizes`. If `sizes` is omitted, it’s used as a fallback.
     *
     * Example:
     * `<link rel="apple-touch-icon" href="..." sizes="...">`
     */
    AppleTouch,

    /**
     * Like [AppleTouch], but tells iOS not to apply any styling (like gloss or rounded corners).
     *
     * Deprecated. Used by iOS 6 and earlier, ignored on modern iOS.
     *
     * Image format: PNG only.
     *
     * Example:
     * `<link rel="apple-touch-icon-precomposed" ...>`
     */
    AppleTouchPrecomposed,

    /**
     * Monochrome SVG icon for Safari's pinned tabs feature (introduced in macOS El Capitan).
     * Safari can then color it using the color attribute.
     *
     * SVG only and only used by Safari and only for pinned tabs, not normal tabs.
     *
     * Example:
     * `<link rel="mask-icon" href="/safari-pinned-tab.svg" color="#5bbad5">`
     */
    SafariMaskIcon,

    /**
     * Used by Windows 8+ pinned sites on Start screen (Edge/IE).
     *
     * Niche use.
     */
    MsTileImage,

    /**
     * Social media preview image.
     */
    OpenGraphImage,

    /**
     * This is not a standardized usage — it's a custom class used for JavaScript-based favicon switching.
     *
     * Sites like GitHub change the favicon dynamically (e.g., when switching to a notification state).
     * The js- prefix is a convention indicating that the element is used by JavaScript.
     *
     * Example:
     * `<link rel="icon" class="js-site-favicon" href="/favicon.svg" type="image/svg+xml">`
     */
    JsSiteFavicon,

}