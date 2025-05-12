## Rick and Morty Encyclopedia

Rick and Morty is a popular show that aired 51 iconic episodes.
Using https://rickandmortyapi.com/, this app shows all episodes info you may ever need.

### Technical rationales
Why some things are done the way they're done in this app?

#### Pagination of episodes

The API returns next page urls instead of pages or page keys as is most common.
Therefore, next page requests for episodes are carried out as full URL GET requests in okhttp.

#### Plain okhttp vs retrofit

##### Episodes
Since new pages are fetched through their full URL, provided by the REST API, 
Retrofit's typesafe mapping of kotlin/Java params/methods/return types, etc to REST API elements
is not that useful, at least for episodes, the most contrived task.
Hence, going with plain okhttp.
Even `HttpUrlConnection` is not a bad choice at this point, 
but okhttp is just a bit easier to work with (subjective choice).

##### Character details
Here, retrofit would have helped, because we have to pass a certain id to the URL as a path param.
However, I decided to not add retrofit just for this scenario, 
as a plain string concat or interpolation for the URL, is more than enough.
Besides, retrofit produces a slightly large object in memory that should be initialised only once.
I think it's best if kept simple.

#### No DI library
Considering the simple use cases in this app, I decided to not add a dependency injection library.
This way, it's also simpler to understand and does not require knowledge of 3rd-party libs.

#### coil3 for image fetching
Fetching images over the network takes a lot of effort and edge cases mitigations.
For the purpose of this app, coil3 for compose was deemed helpful and easy to use, while not bloated.
* 
* Easy interoperability with compose
* Simple API
* Relatively small
* Popular

This is why I picked coil over other libraries I know, such as Glide or Picasso (is this even a thing anymore?).

### Unit tests

Unfortunately, there are none right now.
It was just a matter of available time.
However, the code is organised in such a way that makes unit testing easy.
See the repositories: how they get their "dependencies" injected via the constructor.
Also, see how most dependencies are on interfaces rather than concrete implementations.
For example: data sources in repositories and the error logger in `EpisodesViewModel`.
In the latter, an interface is used for the logger so as to avoid `android.log.Log`, 
which is relatively tricky to mock and test.
Most functions are small enough to test easily.

### Cache

While, again due to time, I was unable to add something like Room for persistence, in episodes,
there's an in-memory cache that behaves similarly to what DAOs would, not in terms of database logic,
but as single-source-of-truth.

### Background updates of episodes

Episodes are updated in the background, even if the user is scrolling through them and fetching
next pages as the scroll goes on.
There's a "deconfliction" mechanism, which always goes for the newer episode model instance.
The last refresh timestamp is there in the UI due to the background updater, not due to persistence.
