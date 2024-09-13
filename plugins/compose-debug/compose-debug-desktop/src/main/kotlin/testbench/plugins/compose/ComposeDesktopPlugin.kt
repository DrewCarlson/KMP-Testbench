package testbench.plugins.compose

import androidx.compose.runtime.*
import androidx.compose.runtime.tooling.CompositionGroup
import org.jetbrains.jewel.ui.component.Text
import testbench.plugin.desktop.DesktopPlugin
import testbench.plugin.desktop.UiHooks

public class ComposeDesktopPlugin :
    ComposePlugin(),
    DesktopPlugin<Unit, Unit> {
    override val ui: UiHooks = UiHooks {
        MainPanel { modifier ->
            var a by remember { mutableStateOf(false) }
            Text("test")
            printGroups(currentComposer.compositionData.compositionGroups)
        }
    }

    private fun printGroups(groups: Iterable<CompositionGroup>) {
        groups.forEach { group ->
            printGroup(group)
        }
    }

    private fun printGroup(group: CompositionGroup) {
        println("${group.key}: ${group.data.joinToString { it.toString() }}")
        println("sourceInfo: ${group.sourceInfo}")
        println("node: ${group.node}")
        println("identity: ${group.identity}")
        println("slotsSize: ${group.slotsSize}")
        println("compositionGroups:")
        printGroups(group.compositionGroups)
        println("\n\n")
    }

    override suspend fun handleMessage(message: Unit) {
    }
}
