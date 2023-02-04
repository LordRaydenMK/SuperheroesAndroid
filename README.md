# Superheroes App

A sample project using the Marvel API to show a list of superheroes and some stats about them.

| Superheroes List                             | Superhero Details                         | Error & Retry                       |
|----------------------------------------------|-------------------------------------------|-------------------------------------|
| ![Superheroes List](/images/superheroes.png) | ![Superhero Details](/images/details.png) | ![Error Loading](/images/error.png) |

## Marvel API KEY

The project need `marvel_public_api_key` and `marvel_private_api_key` to build. You can add them to your home level `gradle.properties` file (located in `~/.gradle` on Unix based systems):

```
marvel_public_api_key=<PUBLIC API KEY HERE>
marvel_private_api_key=<PRIVATE API KEY HERE>
```

or using `-Pmarvel_public_api_key=<PUBLIC API KEY HERE> -Pmarvel_private_api_key=<PRIVATE API KEY HERE>` to each gradle command e.g.:

```
./gradlew assembleDebug -Pmarvel_public_api_key=<PUBLIC API KEY HERE> -Pmarvel_private_api_key=<PRIVATE API KEY HERE>
``` 

Check out the [Marvel Developer portal][mdp] for more info.

## App Architecture

The app uses a reactive architecture built atop Flow. The app follows a layered architecture with data, domain and presentation layer. The package structure is an attempt to package by feature, however both screens share the data and domain layers. The app uses a single activity + fragments and the Jetpack Navigation Component.

### Data Layer

The API call is modeled using Retrofit, KotlinX Serialization as the converter. The data layer converts the DTO objects to Domain objects. Any expected errors that happen up to this point are mapped to a sealed class `SuperheroError`. A custom exception `SuperheroException` that contains a property of the error is delivered as an `Flow` error in the stream.

### Domain Layer

The main class here is `Superhero`. It has a static `create` function that converts the string that comes from the API (as a thumbnail) into a `HttpUrl` also making sure it's https (so it works on Android).

### Presentation Layer

Each screen is represented by a `Fragment` which plays the role of glue code. It's responsible for DI, forwarding actions from the view (Compose) to the `ViewModel` and forwarding state from the `ViewModel` to the view. It also handles any side effect emitted by the `ViewModel` e.g. navigation events.

Each fragment has a Jetpack ViewModel that:

- exposes a single `Flow<ViewState>` backed by a `MutableStateFlow` (caching the last item) describing the state of the view at a given time
- exposes a single `Flow<Effect>` backed by a `Channel` for side effects like navigation, Snackbar or similar. Event that happen when no-one is subscribed are cached. All events are delivered when subscribed
- exposes a `CoroutineScope` with operations tied to it's lifecycle

The Fragment observes the `Flow<ViewState>` between `onStart` and `onStop` and updates the `Sceen`. The Fragment observes `Flow<Effect>` between `onStart` and `onStop` making sure fragment transactions are executed only when the view is active.

The Fragment observes the `Flow<Action>` from `onStart` until `onStop`. However any network calls that result from those interactions are de-coupled from this lifecycle. The operations triggered by the view actions are coupled to the `ViewModel` lifecycle and are only disposed in the `ViewMode.onDispose()` function. Check the [fork() function][fork] for more details. 

The logic is written as extension functions on top of a module (collection of dependencies).

### Dependency Injection

The sample uses the DI approach from [Simple Kotlin DI][simple-di]. The dependencies with Singleton scope live in the app as `AppModule`. Each fragment uses the `AppModule` dependencies and can add it's own (e.g. the `ViewModel`) that are un-scoped or use Jetpack for scoping (e.g. `ViewModel`).

The logic is written as extension functions on top of a module (collection of dependencies).

### Testing

This sample uses [kotest][kotest] as a testing library. The presentation logic is tested by mocking the Retrofit Service and using a `TestViewModel` that uses `MutableSharedFlow` instead of `MutableStateFlow` and remembers all events. Tests use the real schedulers and Turbine for testing `Flow`.

The view is tested in isolation using Espresso, by setting a ViewState and verifying the correct elements are displayed/hidden and the text matches the expected. 

There is also one E2E (black box) test using Maestro that tests both fragments + activity together.

## Acknowledgments

Approaches is this sample are heavily inspired by open source code I have read. It is impossible to list them all, but two samples that were key are:

- [47degrees/FunctionalStreamsSpringSample][fun-stream]
- [Simple Kotlin DI][simple-di]

[mdp]: https://developer.marvel.com/
[fun-stream]: https://github.com/47degrees/FunctionalStreamsSpringSample
[simple-di]: https://gist.github.com/raulraja/97e2d5bf60e9d96680cf1fddcc90ee67
[view-binding]: https://developer.android.com/topic/libraries/view-binding
[fork]: app/src/main/java/io/github/lordraydenmk/superheroesapp/common/observable.kt
[kotest]: https://github.com/kotest/kotest
