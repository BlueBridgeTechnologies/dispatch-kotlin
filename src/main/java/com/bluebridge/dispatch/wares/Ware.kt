package com.bluebridge.dispatch.wares

import io.reactivex.ObservableTransformer

/**
 * Created by thingsdoer on 26/04/2017.
 */

interface Ware<T> : ObservableTransformer<T, T>