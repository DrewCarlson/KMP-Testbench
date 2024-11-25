package testbench.ui

import org.jetbrains.compose.resources.DrawableResource

public enum class TestbenchIcon(
    public val res: DrawableResource,
) {
    ANDROID(Res.drawable.android),
    APPLE(Res.drawable.apple),
    BOX(Res.drawable.box),
    BROWSER(Res.drawable.browser),
    BROWSER_CHROME(Res.drawable.browser_chrome),
    BROWSER_EDGE(Res.drawable.browser_edge),
    BROWSER_FIREFOX(Res.drawable.browser_firefox),
    BROWSER_SAFARI(Res.drawable.browser_safari),
    BROWSER_OPERA(Res.drawable.browser_opera),
    CAMERA(Res.drawable.camera),
    CAMERA_VIDEO(Res.drawable.camera_video),
    CHEVRON_BAR_CONTRACT(Res.drawable.chevron_bar_contract),
    CHEVRON_BAR_EXPAND(Res.drawable.chevron_bar_expand),
    CHEVRON_BAR_RIGHT(Res.drawable.chevron_bar_right),
    CHEVRON_COMPACT_RIGHT(Res.drawable.chevron_compact_right),
    CHEVRON_CONTRACT(Res.drawable.chevron_contract),
    CHEVRON_DOUBLE_RIGHT(Res.drawable.chevron_double_right),
    CHEVRON_EXPAND(Res.drawable.chevron_expand),
    CHEVRON_RIGHT(Res.drawable.chevron_right),
    DATABASE(Res.drawable.database),
    FILE_TEXT(Res.drawable.file_text),
    FOLDER_FILL(Res.drawable.folder_fill),
    GITHUB(Res.drawable.github),
    GLOBE(Res.drawable.globe),
    GLOBE_AMERICAS(Res.drawable.globe_americas),
    LINUX(Res.drawable.linux),
    NODEJS(Res.drawable.nodejs),
    SEARCH(Res.drawable.search),
    TREE(Res.drawable.tree),
    WINDOWS(Res.drawable.windows),
    X(Res.drawable.x),
    X_CIRCLE_FILL(Res.drawable.x_circle_fill),
    ;

    public companion object {
        public fun forName(pluginName: String): TestbenchIcon {
            return runCatching { valueOf(pluginName) }.getOrElse { BOX }
        }
    }
}
