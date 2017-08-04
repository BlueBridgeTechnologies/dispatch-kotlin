package com.bluebridge.dispatch.wares

import com.bluebridge.dispatch.Dispatch
import com.bluebridge.dispatch.TargetedAction

/**
 * Created by thingsdoer on 26/04/2017.
 */

interface Middleware<A: Dispatch.Action> : Ware<TargetedAction<A>>