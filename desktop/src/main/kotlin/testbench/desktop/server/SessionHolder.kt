package testbench.desktop.server

import io.ktor.util.collections.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import testbench.desktop.plugins.PluginRegistry
import testbench.device.DeviceInfo

class SessionHolder {
    private val sessionMap = ConcurrentMap<String, SessionData>()
    private val _sessions = MutableStateFlow(sessionMap.toMap())
    val sessions: StateFlow<Map<String, SessionData>> = _sessions.asStateFlow()

    private val defaultSessionData = SessionData(
        sessionId = "default",
        isConnected = false,
        deviceInfo = DeviceInfo.host,
        pluginRegistry = PluginRegistry(emptyList()),
    )

    private val _activeSession = MutableStateFlow(defaultSessionData)

    val activeSession: StateFlow<SessionData> = _activeSession.asStateFlow()

    fun setActiveSession(id: String) {
        sessionMap[id]?.let { newActiveSession ->
            _activeSession.update { newActiveSession }
        }
    }

    fun get(id: String): SessionData? = sessionMap[id]

    fun has(id: String): Boolean = sessionMap.containsKey(id)

    fun update(
        id: String,
        block: (SessionData) -> SessionData,
    ): SessionData? = sessionMap[id]?.run(block)?.also { set(id, it) }

    fun updateOrCreate(
        id: String,
        update: (SessionData) -> SessionData,
        create: () -> SessionData,
    ): SessionData = (sessionMap[id]?.run(update) ?: create())
        .also { set(id, it) }

    fun set(
        id: String,
        data: SessionData,
    ) {
        if (activeSession.value.sessionId == id) {
            _activeSession.update { data }
        }
        _sessions.update {
            sessionMap[id] = data
            sessionMap.toMap()
        }
    }

    fun remove(id: String) {
        _sessions.update {
            sessionMap.remove(id)
            if (activeSession.value.sessionId == id) {
                if (sessionMap.isEmpty()) {
                    _activeSession.update { defaultSessionData }
                } else {
                    _activeSession.update { sessionMap.values.last() }
                }
            }
            sessionMap.toMap()
        }
    }
}

data class SessionData(
    val sessionId: String,
    val deviceInfo: DeviceInfo,
    val isConnected: Boolean,
    val pluginRegistry: PluginRegistry,
) {
    val isDefault = sessionId == "default"

    val title: String = "${deviceInfo.model} - $sessionId"
}
