# Dispatch

Simple reactive action dispatcher utilising [RxKotlin](https://github.com/ReactiveX/RxKotlin).

## Installation

### JitPack

Follow the instructions on [JitPack](https://jitpack.io/) to add this library to a gradle project.

Add this line to dependencies for the current version:

```
compile 'com.github.BlueBridgeTechnologies:dispatch-kotlin:1.0.0'
```

## Usage

### Basic Usage

Create an instance of Dispatch using an Rx Scheduler


Define a dispatchable action (can optionally provide properties to the class for passing extra information)
```kotlin
class MyAction(data: Int) : Dispatch.Action
```

Register a class with dispatcher using the receiver property stored within the Dispatch instance, and provide functionality for actions the class wishes to receive
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

## License

This project is licensed under the Apache License - see the [LICENSE.md](LICENSE.md) file for details
