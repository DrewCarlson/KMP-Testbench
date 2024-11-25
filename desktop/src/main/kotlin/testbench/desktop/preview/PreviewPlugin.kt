package testbench.desktop.preview

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import testbench.plugin.desktop.DesktopPlugin
import testbench.plugin.desktop.UiHooks
import testbench.ui.TestbenchIcon
import testbench.ui.TestbenchTheme
import testbench.ui.components.ButtonStyle
import testbench.ui.components.ButtonType
import testbench.ui.testbench

@OptIn(ExperimentalLayoutApi::class)
class PreviewPlugin : DesktopPlugin<Unit, Unit> {
    override val id: String = "preview"
    override val name: String = "Preview Components"
    override val requiresClient: Boolean = false

    override suspend fun handleMessage(message: Unit) = Unit

    override val ui: UiHooks = UiHooks {
        MainPanel {
            val scrollState = rememberScrollState()
            Box(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(8.dp),
                ) {
                    testbench.Text(
                        text = "Buttons",
                        style = TestbenchTheme.textStyles.h1,
                    )

                    fun String.title() = lowercase().replaceFirstChar { it.uppercase() }
                    ButtonStyle.entries.forEach { buttonStyle ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            ButtonType.entries.forEach { buttonType ->
                                testbench.Button(
                                    onClick = {},
                                    buttonType = buttonType,
                                    buttonStyle = buttonStyle,
                                ) {
                                    testbench.Text("${buttonType.name.title()} ${buttonStyle.name.title()}")
                                }
                            }
                        }
                    }

                    testbench.HorizontalDivider()

                    testbench.Text(
                        text = "Text Fields",
                        style = TestbenchTheme.textStyles.h1,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        val searchTextState = rememberTextFieldState("my text!")
                        testbench.TextField(
                            state = searchTextState,
                            lineLimits = TextFieldLineLimits.SingleLine,
                            placeholder = { testbench.Text("Search ...") },
                            leadingIcon = { testbench.Icon(TestbenchIcon.SEARCH) },
                            trailingIcon = {
                                if (searchTextState.text.isNotEmpty()) {
                                    testbench.Icon(
                                        icon = TestbenchIcon.X_CIRCLE_FILL,
                                        modifier = Modifier
                                            .pointerHoverIcon(PointerIcon.Default)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null,
                                                role = Role.Button,
                                            ) {
                                                searchTextState.clearText()
                                            },
                                    )
                                }
                            },
                        )

                        testbench.TextField(
                            state = rememberTextFieldState(),
                            lineLimits = TextFieldLineLimits.SingleLine,
                            placeholder = { testbench.Text("Search ...") },
                            leadingIcon = { testbench.Icon(TestbenchIcon.SEARCH) },
                        )
                        testbench.TextField(
                            state = rememberTextFieldState(),
                            readOnly = true,
                            placeholder = { testbench.Text("Read Only") },
                        )
                        testbench.TextField(
                            state = rememberTextFieldState(),
                            enabled = false,
                            placeholder = { testbench.Text("Disabled") },
                        )
                        testbench.TextField(
                            state = rememberTextFieldState(),
                            lineLimits = TextFieldLineLimits.MultiLine(),
                            placeholder = {
                                testbench.Text(
                                    text = "Multiline ...",
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                )
                            },
                            leadingIcon = { testbench.Icon(TestbenchIcon.FOLDER_FILL) },
                        )
                    }

                    testbench.HorizontalDivider()

                    testbench.Text(
                        text = "Text",
                        style = TestbenchTheme.textStyles.h1,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        testbench.Text(
                            text = "Default",
                            style = TestbenchTheme.textStyles.default,
                        )
                        testbench.Text(
                            text = "The quick brown fox jumps over the lazy dog.",
                            style = TestbenchTheme.textStyles.default,
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        testbench.Text(
                            text = "h1",
                            style = TestbenchTheme.textStyles.h1,
                        )
                        testbench.Text(
                            text = "The quick brown fox jumps over the lazy dog.",
                            style = TestbenchTheme.textStyles.h1,
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        testbench.Text(
                            text = "h2",
                            style = TestbenchTheme.textStyles.h2,
                        )
                        testbench.Text(
                            text = "The quick brown fox jumps over the lazy dog.",
                            style = TestbenchTheme.textStyles.h2,
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        testbench.Text(
                            text = "h3",
                            style = TestbenchTheme.textStyles.h3,
                        )
                        testbench.Text(
                            text = "The quick brown fox jumps over the lazy dog.",
                            style = TestbenchTheme.textStyles.h3,
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        testbench.Text(
                            text = "h4",
                            style = TestbenchTheme.textStyles.h4,
                        )
                        testbench.Text(
                            text = "The quick brown fox jumps over the lazy dog.",
                            style = TestbenchTheme.textStyles.h4,
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        testbench.Text(
                            text = "h5",
                            style = TestbenchTheme.textStyles.h5,
                        )
                        testbench.Text(
                            text = "The quick brown fox jumps over the lazy dog.",
                            style = TestbenchTheme.textStyles.h5,
                        )
                    }

                    testbench.HorizontalDivider()

                    testbench.Text(
                        text = "Dropdown",
                        style = TestbenchTheme.textStyles.h1,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        testbench.Dropdown(
                            menu = {
                                testbench.Text("Test")
                            },
                        ) {
                            testbench.Text("Dropdown")
                        }
                    }

                    testbench.Text(
                        text = "Icons",
                        style = TestbenchTheme.textStyles.h1,
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            testbench.Icon(
                                TestbenchIcon.GITHUB,
                                tint = Color.Black,
                            )
                            testbench.Icon(
                                TestbenchIcon.ANDROID,
                                tint = TestbenchTheme.colors.success,
                            )
                            testbench.Icon(
                                TestbenchIcon.BROWSER_OPERA,
                                tint = Color(0xFFff1b2d),
                            )
                        }
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                        ) {
                            TestbenchIcon.entries.forEach { icon ->
                                testbench.Icon(icon)
                            }
                        }
                    }
                }
                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(scrollState),
                    modifier = Modifier.align(Alignment.CenterEnd),
                )
            }
        }
    }
}
