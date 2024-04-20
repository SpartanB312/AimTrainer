package net.spartanb312.everett.utils.event

interface ICancellable {
    var cancelled: Boolean

    fun cancel() {
        cancelled = true
    }
}

open class Cancellable : ICancellable {
    override var cancelled = false
        set(value) {
            field = field || value
        }
}

open class ListenerOwner : IListenerOwner {
    private val listeners = ArrayList<Listener>()
    private val parallelListeners = ArrayList<ParallelListener>()

    override fun register(listener: Listener) {
        listeners.add(listener)
    }

    override fun register(listener: ParallelListener) {
        parallelListeners.add(listener)
    }

    override fun subscribe() {
        for (listener in listeners) {
            listener.eventBus.subscribe(listener)
        }
        for (listener in parallelListeners) {
            listener.eventBus.subscribe(listener)
        }
    }

    override fun unsubscribe() {
        for (listener in listeners) {
            listener.eventBus.unsubscribe(listener)
        }
        for (listener in parallelListeners) {
            listener.eventBus.unsubscribe(listener)
        }
    }
}

interface AlwaysListening : IListenerOwner {
    override fun register(listener: Listener) {
        listener.eventBus.subscribe(listener)
    }

    override fun register(listener: ParallelListener) {
        listener.eventBus.subscribe(listener)
    }

    override fun subscribe() {

    }

    override fun unsubscribe() {

    }
}

interface IListenerOwner {
    fun register(listener: Listener)

    fun register(listener: ParallelListener)

    fun subscribe()

    fun unsubscribe()
}

interface Event : EventPosting {
    fun post() {
        post(this)
    }
}

interface EventPosting {
    val eventBus: EventBus

    fun post(event: Any)
}