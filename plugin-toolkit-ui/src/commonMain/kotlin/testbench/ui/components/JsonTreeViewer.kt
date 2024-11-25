package testbench.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import com.sebastianneubauer.jsontree.JsonTree
import com.sebastianneubauer.jsontree.defaultDarkColors
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import testbench.ui.TestbenchTheme

@Composable
public fun JsonTreeViewer(
    rootElement: JsonElement,
    modifier: Modifier = Modifier,
) {
    val defaultTextStyle = TestbenchTheme.textStyles.default
    val monospaceTextStyle = remember(defaultTextStyle) {
        defaultTextStyle.copy(fontFamily = FontFamily.Monospace)
    }
    JsonTree(
        modifier = modifier,
        json = Json.encodeToString(rootElement),
        textStyle = monospaceTextStyle,
        colors = defaultDarkColors,
        onLoading = {},
        onError = { _ -> }
    )
}

/*@Preview
@Composable
public fun JsonTreeViewerPreview() {
    val json = Json.decodeFromString<JsonObject>(
        """
        {
          "number": 1,
          "string": "value",
          "boolean": true,
          "null": null,
          "array_obj": [
            { "key": 1, "value": "variable" },
            { "key": 2, "value": "variable" }
          ],
          "child": {
            "number": 1,
            "string": "value",
            "subchild": {
                "number": 1,
                "string": "value",
                "array_obj": [
                  { "key": 1, "value": "variable" },
                  { "key": 2, "value": "variable" }
                ]
            },
            "array_obj": [
              { "key": 1, "value": "variable" },
              { "key": 2, "value": "variable" }
            ]
          }
        }
        """.trimIndent(),
    )

    IntUiTheme(isDark = true) {
        Column {
            JsonTreeViewer(
                rootElement = json,
                modifier = Modifier.size(400.dp),
            )

            JsonTreeViewer(
                rootElement = buildJsonArray {
                    add(json)
                },
                modifier = Modifier.size(400.dp),
            )
        }
    }
}
*/
