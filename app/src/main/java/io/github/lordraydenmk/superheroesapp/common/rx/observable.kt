package io.github.lordraydenmk.superheroesapp.common.rx

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.ReplaySubject
import timber.log.Timber

// Cached unit Observable
val unit: Observable<Unit> = Observable.just(Unit)

// Ignore output
fun <A : Any> Observable<A>.unit(): Observable<Unit> = flatMap { unit }

data class ObservableFiber<A : Any>(val join: Observable<A>, val cancel: Disposable)

/**
 * Fork an [Observable] to run within its own [ObservableFiber].
 * This allows you to de-couple an [Observable], from the original [Disposable].
 *
 * This is useful for long running tasks, that need to run regardless if the [Observable] gets disposed.
 * i.e. when a view bound task wants to run a network operation that shouldn't be autodisposed by the view.
 *
 * Source: https://github.com/47degrees/FunctionalStreamsSpringSample/blob/d79ef70e3ba11d8fda86466891d405c02a409472/StreamingAppDemo/app/src/main/java/com/fortyseven/degrees/streamingapp/predef.kt#L32
 */
fun <A : Any> Observable<A>.fork(
    scheduler: Scheduler,
    addToDisposable: (Disposable) -> Unit
): Observable<ObservableFiber<A>> =
    Observable.create { emitter ->
        val s: ReplaySubject<A> = ReplaySubject.create()

        val conn: Disposable =
            subscribeOn(scheduler)
                .subscribe(s::onNext, s::onError, s::onComplete)
                .also { addToDisposable(it) }

        emitter.onNext(ObservableFiber(s.hide(), conn))
        emitter.onComplete()
    }

/**
 * Similar to other automatically shifting operators in RxJava,
 * `evalOn` can be used to run the [Observable] on a given [Scheduler],
 *  and to return on another given [Scheduler], or [Schedulers.computation] by default.
 *
 *  Source: https://github.com/47degrees/FunctionalStreamsSpringSample/blob/d79ef70e3ba11d8fda86466891d405c02a409472/StreamingAppDemo/app/src/main/java/com/fortyseven/degrees/streamingapp/predef.kt#L71
 */
fun <A : Any> Observable<A>.evalOn(
    scheduler: Scheduler,
    returnOn: Scheduler = Schedulers.computation()
): Observable<A> = unit.observeOn(scheduler)
    .flatMap { this }
    .observeOn(returnOn)

/**
 * Uses [Timber] to log when an error happens.
 *
 * This does NOT handle the error
 */
fun <A : Any> Observable<A>.logOnError(msg: String? = null): Observable<A> =
    onErrorResumeNext { t: Throwable ->
        Completable.fromCallable { Timber.e(t, msg) }.andThen(Observable.error(t))
    }
