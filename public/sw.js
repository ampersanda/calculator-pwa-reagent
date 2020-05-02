self.addEventListener('install', (event) => {
  console.log('# Service worker is installed! 🎉')
  event.waitUntil(
    caches.open('static').then((cache) => {
      cache.addAll([
        '/',
        '/css/style.css',
        '/favicon.ico',
        '/favicon/android-chrome-192x192.png',
        '/favicon/android-chrome-256x256.png',
        '/favicon/apple-touch-icon.png',
        '/favicon/favicon-16x16.png',
        '/favicon/favicon-32x32.png',
        '/favicon/mstile-150x150.png',
        '/favicon/safari-pinned-tab.svg',
        '/images/loader.gif',
        '/index.html',
        '/js/compiled/main.js',
      ])
    }),
  )
})

self.addEventListener('activate', () => {
  console.log('# Service worker is active! 🎉')
})

self.addEventListener('fetch', (event) => {
  event.respondWith(
    caches.match(event.request).then((res) => {
      if (res) return res
      return fetch(event.request)
    }),
  )
})
