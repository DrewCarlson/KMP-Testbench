package testbench.service

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.isInterface
import org.jetbrains.kotlin.name.FqName
import java.io.File

private const val SERVICE_PLUGIN_FQN = "testbench.plugin.server.ServerPlugin"

/**
 * Scans for all classes and generates a `ServerPlugin` service file in the [servicesDir].
 */
class CustomPluginIrGenerationExtension(
    private val servicesDir: File,
) : IrGenerationExtension {
    private val servicePluginFqn = FqName(SERVICE_PLUGIN_FQN)

    private val IrClass.isPluginType: Boolean
        get() = superTypes.any { it.classFqName == servicePluginFqn }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext,
    ) {
        val pluginClasses = moduleFragment.files
            .flatMap { file ->
                file.declarations
                    .filterIsInstance<IrClass>()
                    .mapNotNull { irClass ->
                        irClass
                            .takeIf { it.isPluginType && !it.isInterface }
                            ?.fqNameWhenAvailable
                            ?.toString()
                    }
            }

        if (pluginClasses.isNotEmpty()) {
            generateServiceFile(pluginClasses)
        }
    }

    private fun generateServiceFile(classes: List<String>) {
        servicesDir.mkdirs()

        val serviceFile = File(servicesDir, SERVICE_PLUGIN_FQN)

        serviceFile.bufferedWriter().use { writer ->
            classes.forEach { className ->
                writer.write(className)
                writer.write('\n'.code)
            }
        }
    }
}
