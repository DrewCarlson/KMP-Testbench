package testbench.plugins.logs

import kotlinx.datetime.Instant

public data class LogEntry(
    val timestamp: Instant,
    val tag: String,
    val pid: String,
    val message: String,
    val logLevel: LogLevel,
)

public enum class LogLevel {
    TRACE,
    VERBOSE,
    DEBUG,
    INFO,
    WARNING,
    ERROR,
}
