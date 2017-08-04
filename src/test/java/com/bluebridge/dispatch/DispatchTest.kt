package com.bluebridge.dispatch

import com.bluebridge.dispatch.wares.Middleware
import com.bluebridge.dispatch.wares.Postware
import com.bluebridge.dispatch.wares.Preware
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import org.junit.Assert
import org.junit.Test

/**
 * Created by thingsdoer on 26/04/2017.
 */

class DispatchTest {

    interface TestAction : Dispatch.Action
    sealed class TestActions {
        class TEST_ACTION_1(val payload: String) : TestAction
        class TEST_ACTION_2 : TestAction
    }

    class TestDispatcher(scheduler: TestScheduler) : Dispatch<TestAction>(scheduler)

    class TestListener : Dispatch.Listener<TestAction> {

        var result: String? = null

        override fun onAction(action: TestAction): Boolean {
            when (action) {
                is TestActions.TEST_ACTION_1 -> return onTestAction1(action)
                else -> return false
            }
        }

        private fun onTestAction1(action: TestActions.TEST_ACTION_1): Boolean {
            result = action.payload
            return true
        }
    }

    @org.junit.Test
    fun dispatchingAction_callsListenerWithCorrectAction() {
        val scheduler = TestScheduler()
        val dispatcher = TestDispatcher(scheduler)
        val listener = TestListener()
        dispatcher.receiver.register(listener)
        dispatcher.dispatch(TestActions.TEST_ACTION_1("success"))
        scheduler.triggerActions()
        org.junit.Assert.assertEquals(listener.result, "success")
    }

    @org.junit.Test
    fun dispatcher_callsAllWares() {
        val scheduler = TestScheduler()
        val dispatcher = TestDispatcher(scheduler)
        val listener = TestListener()

        var finalValue: TargetedResult<TestAction>? = null

        val preware = object: Preware<TestAction> {
            override fun apply(t: Observable<TestAction>?): Observable<TestAction> {
                return Observable.just(TestActions.TEST_ACTION_1("from-preware"))
            }
        }
        val middleware = object: Middleware<TestAction> {
            override fun apply(t: Observable<TargetedAction<TestAction>>): Observable<TargetedAction<TestAction>> {
                return t.map { value -> value.reactioned(TestActions.TEST_ACTION_1("from-middleware")) }
            }
        }
        val postware = object: Postware<TestAction> {
            override fun apply(t: Observable<TargetedResult<TestAction>>): Observable<TargetedResult<TestAction>> {
                return t.doOnNext { next -> finalValue = next }
            }
        }

        dispatcher.addPreware(preware)
        dispatcher.addMiddleware(middleware)
        dispatcher.addPostware(postware)
        dispatcher.receiver.register(listener)
        dispatcher.dispatch(TestActions.TEST_ACTION_2())
        scheduler.triggerActions()
        org.junit.Assert.assertEquals("from-middleware", listener.result)
        org.junit.Assert.assertEquals("from-middleware", (finalValue?.action as TestActions.TEST_ACTION_1).payload)
    }
}