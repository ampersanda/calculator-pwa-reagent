self.addEventListener('install', function (event) {
    console.log('service worker installed');
    event.waitUntil(
        caches.open('static')
            .then(function (cache) {
                cache.addAll([
                    '/',
                    '/index.html',
                    '/js/compiled/main.js',
                    '/images/loader.gif',
                    '/css/style.css',
                    '/favicon.ico',
                    '/favicon/favicon-32x32.png',
                    '/favicon/favicon-16x16.png',
                ]);
            })
    );
});

self.addEventListener('activate', function () {
    console.log('service worker active')
});

self.addEventListener('fetch', function (event) {
    event.respondWith(
        caches.match(event.request)
            // response from cache
            .then(function(res){
                if (res) {
                    return res;
                } else {
                    return fetch(event.request)
                }
            })
    );
});