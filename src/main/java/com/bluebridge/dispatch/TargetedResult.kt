package com.bluebridge.dispatch

/**
 * Created by thingsdoer on 26/04/2017.
 */

class TargetedResult<A : Dispatch.Action>(val target: Dispatch.Listener<A>, val action: A, val result: Boolean){
    override fun toString(): String {
        return target::class.simpleName + " :: " + action::class.simpleName + " == " + result.toString()
    }
}