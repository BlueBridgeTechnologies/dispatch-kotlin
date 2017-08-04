package com.bluebridge.dispatch.wares

import com.bluebridge.dispatch.Dispatch
import com.bluebridge.dispatch.TargetedResult

/**
 * Created by thingsdoer on 26/04/2017.
 */

interface Postware<A : Dispatch.Action> : Ware<TargetedResult<A>>