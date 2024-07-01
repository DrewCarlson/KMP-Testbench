package testbench.service

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

internal val META_INF_BUILD_DIR =
    CompilerConfigurationKey<String>("The path to write META-INF/services to.")

@OptIn(ExperimentalCompilerApi::class)
class TestBenchCommandLineProcessor : CommandLineProcessor {
    internal companion object {
        val META_INF_BUILD_DIR_OPTION =
            CliOption(
                optionName = "meta_inf_build_dir",
                valueDescription = "The full path to the build directory",
                description = META_INF_BUILD_DIR.toString(),
                required = true,
                allowMultipleOccurrences = false,
            )
    }

    override val pluginId: String = "org.drewcarlson.testbench.service.compiler"

    override val pluginOptions: Collection<AbstractCliOption> =
        listOf(
            META_INF_BUILD_DIR_OPTION,
        )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration,
    ) {
        when (option.optionName) {
            "meta_inf_build_dir" -> configuration.put(META_INF_BUILD_DIR, value)
            else -> error("Undefined plugin option: ${option.optionName}")
        }
    }
}
