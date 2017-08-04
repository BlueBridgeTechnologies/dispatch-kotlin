package com.bluebridge.dispatch

import com.bluebridge.dispatch.wares.CompositeWare
import com.bluebridge.dispatch.wares.Middleware
import com.bluebridge.dispatch.wares.Postware
import com.bluebridge.dispatch.wares.Preware
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject

/**
 * Created by thingsdoer on 26/04/2017.
 */

abstract class Dispatch<ActionType : Dispatch.Action>(val scheduler: Scheduler) {

    interface Action

    interface Listener<ActionType> {
        fun onAction(action: ActionType): Boolean
    }

    class Receiver<ActionType> {

        private val subject: ReplaySubject<MutableSet<Listener<ActionType>>> = ReplaySubject.createWithSize(1)
        private val listeners: MutableSet<Listener<ActionType>> = mutableSetOf()

        val listenersObservable: Observable<Listener<ActionType>>
            get() = subject
                .flatMap { listeners -> Observable.fromIterable(listeners) }

        fun register(listener: Listener<ActionType>) {
            listeners.add(listener)
            subject.onNext(listeners)
        }
    }

    val receiver = Receiver<ActionType>()

    private var disposable: Disposable? = null
    private val subject = PublishSubject.create<ActionType>()

    // Operates on actions before they are dispatched.
    private val prewares: CompositeWare<ActionType> by lazy {
        val preware = CompositeWare<ActionType>()
        preware
    }

    // Operates before each listener receives the action.
    private val middlewares: CompositeWare<TargetedAction<ActionType>> by lazy {
        val middleware = CompositeWare<TargetedAction<ActionType>>()
        middleware
    }

    // Operates after the action is called.
    private val postwares: CompositeWare<TargetedResult<ActionType>> by lazy {
        val postware = CompositeWare<TargetedResult<ActionType>>()
        postware
    }

    init {
        resubscribe()
    }

    private fun resubscribe() {
        disposable?.dispose()
        disposable = subject
            .observeOn(scheduler)
            .compose(prewares)
            .flatMap { action ->
                receiver.listenersObservable
                    .map { listener ->
                        TargetedAction(listener, action)
                    }
            }
            .compose(middlewares)
            .map(TargetedAction<ActionType>::execute)
            .compose(postwares)
            .subscribe()
    }

    private fun handleError(error: Throwable) {
        System.err.println("Dispatch: Error during dispatch chain, perhaps your middleware is faulty? : " + error.message)
    }

    fun dispatch(action: ActionType) {
        subject.onNext(action)
    }

    fun addPreware(ware: Preware<ActionType>){
        prewares.add(ware)
        resubscribe()
    }

    fun addMiddleware(ware: Middleware<ActionType>){
        middlewares.add(ware)
        resubscribe()
    }

    fun addPostware(ware: Postware<ActionType>){
        postwares.add(ware)
        resubscribe()
    }
}