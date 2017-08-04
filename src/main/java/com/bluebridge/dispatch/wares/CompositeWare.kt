package com.bluebridge.dispatch.wares

import io.reactivex.Observable

/**
 * Created by thingsdoer on 26/04/2017.
 */

class CompositeWare<T> : Ware<T> {
    private val wares = arrayListOf<Ware<T>>()

    override fun apply(t: Observable<T>): Observable<T> {
        var head = t
        for(ware in wares){
            head = head.compose(ware)
        }
        return head
    }

    fun add(ware: Ware<T>){
        wares.add(ware)
    }
}