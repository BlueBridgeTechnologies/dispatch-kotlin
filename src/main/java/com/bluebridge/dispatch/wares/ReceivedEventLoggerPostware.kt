package com.bluebridge.dispatch.wares

import com.bluebridge.dispatch.Dispatch
import com.bluebridge.dispatch.TargetedResult
import io.reactivex.Observable

/**
 * Created by thingsdoer on 26/04/2017.
 */

class ReceivedEventLoggerPostware<A : Dispatch.Action> : Postware<A> {
    override fun apply(t: Observable<TargetedResult<A>>): Observable<TargetedResult<A>> {
        return t
            .filter(TargetedResult<A>::result)
            .doOnNext {
                result ->
                println(ReceivedEventLoggerPostware::class.simpleName + ": " + result.toString())
            }
    }
}