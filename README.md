# Dispatch

Simple reactive action dispatcher utilising [RxKotlin](https://github.com/ReactiveX/RxKotlin).

## Installation

### JitPack

Follow the instructions on [JitPack](https://jitpack.io/) to add this library to a gradle project.

Add this line to dependencies block to get the most recent version:

```
compile 'com.github.BlueBridgeTechnologies:dispatch-kotlin:1.0.0'
```

## Usage

### Basic Usage

Create an instance of `Dispatch` using an Rx Scheduler.


Define a dispatchable action (can optionally provide properties to the class for passing extra information).
```kotlin
class MyAction(data: Int) : Dispatch.Action
```

Register a class with dispatcher using the receiver property stored within the Dispatch instance, and provide functionality for actions the class wishes to receive.
```kotlin
class MyListener(receiver: Dispatch.Receiver) : Dispatch.Listener<Dispatch.Action> {
    init {
        receiver.register(this)
    }

    override fun onAction(action: Dispatch.Action): Boolean {
        when (action) {
            is MyAction -> return this.onMyActionReceived()
            else -> return false
        }
    }

    fun onMyActionReceived(action: Dispatch.Action): Boolean {
        // functionality based on MyAction being received
    }
}
```

Dispatch an action from the Dispatch instance
```
dispatcher.dispatch(MyAction(1))
```

### Wares

The `Dispatch` instance can be provided with wares to operate on actions at specific points during dispatch.

`Preware`: Operates on actions before they are dispatched.  
`Middleware`: Operates before each listener receives the action.  
`Postware`: Operates after the action is called.

## License

This project is licensed under the Apache License - see the [LICENSE.md](LICENSE.md) file for details
