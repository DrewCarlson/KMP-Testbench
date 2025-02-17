package testbench.client

import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Provides control over how [TestBenchClient] decides when to reconnect.
 */
public interface ReconnectHandler {
    /**
     * Called before an attempt to reconnect with the attempt number (at least 1),
     * a [delay] is expected and reconnection will occur immediately after.
     */
    public suspend fun awaitTimeout(attempt: Int)

    public companion object {
        /**
         * A simple 5-second delay between reconnection attempts.
         */
        public fun default(): ReconnectHandler {
            return DelayReconnectHandler(5.seconds)
        }

        /**
         * A simple static delay between reconnection attempts with a custom [delay].
         */
        public fun withDelay(delay: Duration): ReconnectHandler {
            return DelayReconnectHandler(delay)
        }

        /**
         * Exponential backoff for reconnection attempts multiplying [delay]
         * each attempt up to [maxDelay].
         */
        public fun withExponentialBackoff(
            delay: Duration,
            maxDelay: Duration,
        ): ReconnectHandler {
            return ExponentialReconnectHandler(
                delay = delay,
                maxDelay = maxDelay,
            )
        }

        /**
         * Prevents the client from ever reconnecting.
         */
        public fun never(): ReconnectHandler {
            return DelayReconnectHandler(Duration.INFINITE)
        }
    }
}

private class DelayReconnectHandler(
    private val delay: Duration,
) : ReconnectHandler {
    override suspend fun awaitTimeout(attempt: Int) {
        delay(delay)
    }
}

private class ExponentialReconnectHandler(
    private val delay: Duration,
    private val maxDelay: Duration,
) : ReconnectHandler {
    override suspend fun awaitTimeout(attempt: Int) {
        delay((delay * attempt).coerceAtMost(maxDelay))
    }
}
