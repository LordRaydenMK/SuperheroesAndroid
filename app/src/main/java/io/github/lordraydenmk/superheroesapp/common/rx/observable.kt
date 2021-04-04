package io.github.lordraydenmk.superheroesapp.common.rx

import io.reactivex.Completable
import io.reactivex.Observable
import timber.log.Timber

// Cached unit Observable
val unit: Observable<Unit> = Observable.just(Unit)

// Ignore output
fun <A : Any> Observable<A>.unit(): Observable<Unit> = flatMap { unit }

/**
 * Uses [Timber] to log when an error happens.
 *
 * This does NOT handle the error
 */
fun <A : Any> Observable<A>.logOnError(msg: String? = null): Observable<A> =
    onErrorResumeNext { t: Throwable ->
        Completable.fromCallable { Timber.e(t, msg) }.andThen(Observable.error(t))
    }
