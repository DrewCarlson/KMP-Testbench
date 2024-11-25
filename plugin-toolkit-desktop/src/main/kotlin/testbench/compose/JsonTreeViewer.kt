package testbench.compose

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.json.*

private data class JsonKeyValue(
    val key: String,
    val value: JsonElement,
)

@Composable
public fun JsonTreeViewer(
    rootElement: JsonElement,
    modifier: Modifier = Modifier,
) {
    /*
    val tree = remember(rootElement) { jsonToTree(rootElement) }
    val treeState = rememberTreeState()
    val openNodes = remember(treeState.openNodes) { treeState.openNodes }

    val defaultTextStyle = JewelTheme.defaultTextStyle
    val monospaceTextStyle = remember(defaultTextStyle) {
        defaultTextStyle.copy(fontFamily = FontFamily.Monospace)
    }
    CompositionLocalProvider(
        LocalTextStyle provides monospaceTextStyle,
    ) {
        LazyTree(
            tree = tree,
            treeState = treeState,
            modifier = modifier,
            onElementClick = { },
            onElementDoubleClick = { },
        ) { element ->
            val key = element.data.key.takeUnless { it.isBlank() }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                when (val jsonElement = element.data.value) {
                    is JsonArray -> {
                        JsonArrayNode(
                            key = key,
                            jsonArray = jsonElement,
                            isExpanded = openNodes.contains(element.id),
                        )
                    }

                    is JsonObject -> {
                        JsonObjectNode(
                            element = element,
                            isExpanded = openNodes.contains(element.id),
                            modifier = Modifier.weight(1f),
                        )
                    }

                    is JsonNull -> {
                        JsonNullNode(
                            key = key,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    is JsonPrimitive -> {
                        JsonPrimitiveNode(
                            element = element,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }*/
}
/*
@Composable
private fun JsonArrayNode(
    key: String?,
    jsonArray: JsonArray,
    isExpanded: Boolean,
) {
    if (key != null) {
        Text(
            text = "$key:",
        )
        Spacer(Modifier.width(2.dp))
    }

    Text("[")

    Text(
        text = "${jsonArray.size} items",
        fontSize = 10.sp,
        modifier = Modifier
            .border(1.dp, JewelTheme.globalColors.borders.normal, RoundedCornerShape(2.dp))
            .padding(2.dp),
    )

    if (!isExpanded) {
        Text(text = "]")
    }
}

@Composable
private fun JsonObjectNode(
    element: Tree.Element<JsonKeyValue>,
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val key = element.data.key.takeUnless { it.isBlank() }
    val size = element.data
        .value
        .jsonObject
        .size
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (key == null) {
            Text("{")
        } else {
            Text("$key: {")
        }
        if (!isExpanded) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$size Props",
                fontSize = 10.sp,
                modifier = Modifier
                    .border(1.dp, JewelTheme.globalColors.borders.normal, RoundedCornerShape(2.dp))
                    .padding(2.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("}")
        }
    }
}

@Composable
private fun JsonPrimitiveNode(
    element: Tree.Element<JsonKeyValue>,
    modifier: Modifier = Modifier,
) {
    val jsonPrimitive = element.data.value.jsonPrimitive

    if (element.data.key.isNotBlank()) {
        Text(
            text = "${element.data.key}:",
        )
    }

    when {
        jsonPrimitive.isString ->
            Text(
                text = "\"${jsonPrimitive.content}\"",
                modifier = modifier,
                color = JewelTheme.colorPalette.orange(7),
            )

        jsonPrimitive.intOrNull != null ||
            jsonPrimitive.floatOrNull != null ||
            jsonPrimitive.doubleOrNull != null ->
            Text(
                text = jsonPrimitive.content,
                modifier = modifier,
                color = JewelTheme.colorPalette.green(7),
            )

        jsonPrimitive.booleanOrNull != null ->
            Text(
                text = jsonPrimitive.content,
                modifier = modifier,
                color = JewelTheme.colorPalette.blue(7),
            )
    }
}

@Composable
private fun JsonNullNode(
    key: String?,
    modifier: Modifier = Modifier,
) {
    if (key != null) {
        Text(
            text = "$key:",
            style = JewelTheme.defaultTextStyle.copy(fontFamily = FontFamily.Monospace),
        )
    }

    Text(
        text = "null",
        modifier = modifier,
        color = JewelTheme.colorPalette.purple(7),
    )
}

private fun jsonToTree(jsonElement: JsonElement) = buildTree<JsonKeyValue> {
    when (jsonElement) {
        is JsonObject -> addJsonObjectNode(jsonElement, null)
        is JsonArray -> addJsonArrayNode(jsonElement, null)
        else -> addLeaf(JsonKeyValue("", jsonElement), "")
    }
}

private fun TreeBuilder<JsonKeyValue>.addJsonObjectNode(
    jsonObject: JsonObject,
    parentId: Any?,
) {
    val nodeData = JsonKeyValue("", jsonObject)
    addNode(nodeData, parentId) {
        jsonObject.forEach { (childKey, jsonElement) ->
            when (jsonElement) {
                is JsonObject -> addJsonObjectNode(childKey, jsonElement, "$parentId.$childKey")
                is JsonArray -> addJsonArrayNode(childKey, jsonElement, "$parentId.$childKey")
                else -> addLeaf(JsonKeyValue(childKey, jsonElement), "$parentId.$childKey")
            }
        }
    }
}

private fun ChildrenGeneratorScope<JsonKeyValue>.addJsonObjectNode(
    key: String,
    jsonObject: JsonObject,
    parentId: Any?,
) {
    val nodeData = JsonKeyValue(key, jsonObject)
    addNode(nodeData, parentId) {
        jsonObject.forEach { (childKey, jsonElement) ->
            when (jsonElement) {
                is JsonObject -> addJsonObjectNode(childKey, jsonElement, "$parentId.$key.$childKey")
                is JsonArray -> addJsonArrayNode(childKey, jsonElement, "$parentId.$key.$childKey")
                else -> addLeaf(JsonKeyValue(childKey, jsonElement), "$parentId.$key.$childKey")
            }
        }
    }
}

private fun TreeBuilder<JsonKeyValue>.addJsonArrayNode(
    jsonArray: JsonArray,
    parentId: Any?,
) {
    addNode(JsonKeyValue("", jsonArray), parentId) {
        jsonArray.forEachIndexed { index, jsonElement ->
            val childKey = index.toString()
            when (jsonElement) {
                is JsonObject -> addJsonObjectNode(childKey, jsonElement, "$parentId[$childKey]")
                is JsonArray -> addJsonArrayNode(childKey, jsonElement, "$parentId[$childKey]")
                else -> addLeaf(JsonKeyValue(childKey, jsonElement), "$parentId[$childKey]")
            }
        }
    }
}

private fun ChildrenGeneratorScope<JsonKeyValue>.addJsonArrayNode(
    key: String,
    jsonArray: JsonArray,
    parentId: Any?,
) {
    addNode(JsonKeyValue(key, jsonArray), parentId) {
        jsonArray.forEachIndexed { index, jsonElement ->
            val childKey = index.toString()
            when (jsonElement) {
                is JsonObject -> addJsonObjectNode(childKey, jsonElement, "$parentId.$key[$childKey]")
                is JsonArray -> addJsonArrayNode(childKey, jsonElement, "$parentId.$key[$childKey]")
                else -> addLeaf(JsonKeyValue(childKey, jsonElement), "$parentId.$key[$childKey]")
            }
        }
    }
}

@Preview
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
