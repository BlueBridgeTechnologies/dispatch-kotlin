package com.bluebridge.dispatch.wares

import com.bluebridge.dispatch.Dispatch
import io.reactivex.Observable
import org.junit.Assert
import org.junit.Test

/**
 * Created by thingsdoer on 26/04/2017.
 */

class CompositeWareTest {

    class TestAction(val value: String) : Dispatch.Action {
        fun append(input: String) : TestAction {
            return TestAction(value + input)
        }
        fun reverse() : TestAction {
            return TestAction(value.reversed())
        }
    }

    class AppenderMiddleware(private val string: String) : Ware<TestAction> {
        override fun apply(t: Observable<TestAction>): Observable<TestAction> {
            return t.map { action -> action.append(string) }
        }
    }

    class ReverserMiddleware : Ware<TestAction> {
        override fun apply(t: Observable<TestAction>): Observable<TestAction> {
            return t.map { action -> action.reverse() }
        }
    }

    @org.junit.Test
    fun zeroMiddlewares_isNoOp() {
        val middleware = CompositeWare<String>()
        val result = Observable.just("hi").compose(middleware).blockingFirst()
        org.junit.Assert.assertEquals("hi", result)
    }

    @org.junit.Test
    fun singleMiddleware_executes() {
        val middleware = CompositeWare<TestAction>()
        val action = TestAction("hello-")
        middleware.add(AppenderMiddleware("world"))
        val result = Observable.just(action).compose(middleware).blockingFirst()
        org.junit.Assert.assertEquals("hello-world", result.value)
    }

    @org.junit.Test
    fun variedMiddlewares_execute() {
        val middleware = CompositeWare<TestAction>()
        middleware.add(ReverserMiddleware())
        middleware.add(AppenderMiddleware("world"))
        val action = TestAction("-hello")
        val result = Observable.just(action).compose(middleware).blockingFirst()
        org.junit.Assert.assertEquals("olleh-world", result.value)
    }
}