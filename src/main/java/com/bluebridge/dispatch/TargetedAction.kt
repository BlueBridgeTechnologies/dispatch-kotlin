package com.bluebridge.dispatch

/**
 * Created by thingsdoer on 26/04/2017.
 */

open class TargetedAction<A : Dispatch.Action>(val target: Dispatch.Listener<A>, val action: A) {
    override fun toString(): String {
        return target::class.simpleName + " :: " + action::class.simpleName
    }

    fun execute(): TargetedResult<A> {
        return TargetedResult(
            target,
            action,
            target.onAction(action)
        )
    }

    fun redirected(newTarget: Dispatch.Listener<A>) : TargetedAction<A> {
        return TargetedAction(newTarget, action)
    }

    fun reactioned(newAction: A) : TargetedAction<A> {
        return TargetedAction(target, newAction)
    }
}