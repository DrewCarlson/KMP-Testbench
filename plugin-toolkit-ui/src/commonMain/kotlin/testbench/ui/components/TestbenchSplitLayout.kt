package testbench.ui.components

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation.Horizontal
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import kotlin.math.roundToInt


/**
 * Represents the state for a split layout, which is used to control the position of the divider and layout coordinates.
 *
 * @param initialSplitFraction The initial fraction value that determines the position of the divider.
 * @constructor Creates a [SplitLayoutState] with the given initial split fraction.
 */
public class SplitLayoutState(initialSplitFraction: Float) {
    /**
     * A mutable floating-point value representing the position of the divider within the split layout. The position is
     * expressed as a fraction of the total layout size, ranging from 0.0 (divider at the start) to 1.0 (divider at the
     * end). This allows dynamic adjustment of the layout's two panes, reflecting the current state of the divider's
     * placement.
     */
    public var dividerPosition: Float by mutableStateOf(initialSplitFraction.coerceIn(0f, 1f))

    /**
     * Holds the layout coordinates for the split layout. These coordinates are used to track the position and size of
     * the layout parts, facilitating the adjustment of the divider and the layout's panes during interactions like
     * dragging.
     */
    public var layoutCoordinates: LayoutCoordinates? by mutableStateOf(null)
}

/**
 * Remembers a [SplitLayoutState] instance with the provided initial split fraction.
 *
 * @param initialSplitFraction The initial fraction value that determines the position of the divider.
 * @return A remembered [SplitLayoutState] instance.
 */
@Composable
public fun rememberSplitLayoutState(initialSplitFraction: Float = 0.5f): SplitLayoutState = remember {
    SplitLayoutState(initialSplitFraction)
}

internal expect val HorizontalResizePointerIcon: PointerIcon
internal expect val VerticalResizePointerIcon: PointerIcon

@Composable
internal fun TestbenchSplitLayout(
    first: @Composable () -> Unit,
    second: @Composable () -> Unit,
    strategy: SplitLayoutStrategy,
    modifier: Modifier,
    draggableWidth: Dp,
    firstPaneMinWidth: Dp,
    secondPaneMinWidth: Dp,
    //dividerStyle: DividerStyle,
    state: SplitLayoutState,
) {
    val density = LocalDensity.current
    var isDragging by remember { mutableStateOf(false) }
    val resizePointerIcon = if (strategy.isHorizontal()) HorizontalResizePointerIcon else VerticalResizePointerIcon

    var dragOffset by remember { mutableFloatStateOf(0f) }

    val draggableState = rememberDraggableState { delta ->
        state.layoutCoordinates?.let { coordinates ->
            val size = if (strategy.isHorizontal()) coordinates.size.width else coordinates.size.height
            val minFirstPositionPx = with(density) { firstPaneMinWidth.toPx() }
            val minSecondPositionPx = with(density) { secondPaneMinWidth.toPx() }

            /**
             * Ensures that the divider in the split layout can be dragged, but it respects the minimum sizes of both
             * panes and adjusts the layout accordingly. The position is calculated, constrained, and then applied to
             * the dividerPosition in the state, ensuring a smooth and safe user experience during dragging
             * interactions.
             */
            if (minFirstPositionPx + minSecondPositionPx <= size) {
                dragOffset += delta
                val position = size * state.dividerPosition + dragOffset
                val newPosition = position.coerceIn(minFirstPositionPx, size - minSecondPositionPx)
                state.dividerPosition = newPosition / size
            }
        }
    }

    Layout(
        modifier =
            modifier
                .onGloballyPositioned { coordinates ->
                    state.layoutCoordinates = coordinates
                    // Reset drag offset when layout changes
                    dragOffset = 0f
                }
                .pointerHoverIcon(if (isDragging) resizePointerIcon else PointerIcon.Default),
        content = {
            Box(Modifier.layoutId("first")) { first() }
            Box(Modifier.layoutId("second")) { second() }

            val dividerInteractionSource = remember { MutableInteractionSource() }
            val dividerOrientation = if (strategy.isHorizontal()) Orientation.VERTICAL else Orientation.HORIZONTAL
            val fillModifier = if (strategy.isHorizontal()) Modifier.fillMaxHeight() else Modifier.fillMaxWidth()
            val orientation = if (strategy.isHorizontal()) Horizontal else Vertical

            Divider(
                orientation = dividerOrientation,
                modifier = fillModifier.layoutId("divider").focusable(false),
                /*color = dividerStyle.color,
                thickness = dividerStyle.metrics.thickness,*/
            )

            Box(
                Modifier.let { modifier ->
                    if (strategy.isHorizontal()) {
                        modifier.fillMaxHeight().width(draggableWidth)
                    } else {
                        modifier.fillMaxWidth().height(draggableWidth)
                    }
                }
                    .draggable(
                        orientation = orientation,
                        state = draggableState,
                        onDragStarted = {
                            isDragging = true
                            dragOffset = 0f
                        },
                        onDragStopped = {
                            isDragging = false
                            dragOffset = 0f
                        },
                        interactionSource = dividerInteractionSource,
                    )
                    .pointerHoverIcon(resizePointerIcon)
                    .layoutId("divider-handle")
                    .focusable(false)
            )
        },
    ) { measurables, constraints ->
        if (state.layoutCoordinates == null) {
            notReadyLayout(constraints)
        } else {
            doLayout(
                strategy,
                density,
                state,
                //dividerStyle,
                draggableWidth,
                firstPaneMinWidth,
                secondPaneMinWidth,
                constraints,
                measurables,
            )
        }
    }
}

private fun MeasureScope.notReadyLayout(constraints: Constraints) =
    layout(constraints.minWidth, constraints.minHeight) {}

private fun MeasureScope.doLayout(
    strategy: SplitLayoutStrategy,
    density: Density,
    state: SplitLayoutState,
    //dividerStyle: DividerStyle,
    draggableWidth: Dp,
    firstPaneMinWidth: Dp,
    secondPaneMinWidth: Dp,
    constraints: Constraints,
    measurables: List<Measurable>,
): MeasureResult {
    val firstMeasurable = measurables.find { it.layoutId == "first" } ?: error("No first component found.")
    val secondMeasurable = measurables.find { it.layoutId == "second" } ?: error("No second component found.")
    val dividerMeasurable = measurables.find { it.layoutId == "divider" } ?: error("No divider component found.")
    val dividerHandleMeasurable =
        measurables.find { it.layoutId == "divider-handle" } ?: error("No divider-handle component found.")

    val splitResult = strategy.calculateSplitResult(density = density, layoutDirection = layoutDirection, state = state)

    val gapOrientation = splitResult.gapOrientation

    val dividerWidth = with(density) { 2.dp.roundToPx()/*dividerStyle.metrics.thickness.roundToPx()*/ }
    val handleWidth = with(density) { draggableWidth.roundToPx() }

    // The visual divider itself. It's a thin line that separates the two panes
    val dividerPlaceable =
        dividerMeasurable.measure(
            when (gapOrientation) {
                Orientation.VERTICAL -> {
                    constraints.copy(
                        minWidth = dividerWidth,
                        maxWidth = dividerWidth,
                        minHeight = constraints.minHeight,
                        maxHeight = constraints.maxHeight,
                    )
                }

                Orientation.HORIZONTAL -> {
                    constraints.copy(
                        minWidth = constraints.minWidth,
                        maxWidth = constraints.maxWidth,
                        minHeight = dividerWidth,
                        maxHeight = dividerWidth,
                    )
                }
            }
        )

    // This is an invisible, wider area around the divider that can be dragged by the user
    // to resize the panes
    val dividerHandlePlaceable =
        dividerHandleMeasurable.measure(
            when (gapOrientation) {
                Orientation.VERTICAL -> {
                    constraints.copy(
                        minWidth = handleWidth,
                        maxWidth = handleWidth,
                        minHeight = constraints.minHeight,
                        maxHeight = constraints.maxHeight,
                    )
                }

                Orientation.HORIZONTAL -> {
                    constraints.copy(
                        minWidth = constraints.minWidth,
                        maxWidth = constraints.maxWidth,
                        minHeight = handleWidth,
                        maxHeight = handleWidth,
                    )
                }
            }
        )

    val availableSpace =
        if (gapOrientation == Orientation.VERTICAL) {
            (constraints.maxWidth - dividerWidth).coerceAtLeast(1)
        } else {
            (constraints.maxHeight - dividerWidth).coerceAtLeast(1)
        }

    val minFirstPaneSizePx = with(density) { firstPaneMinWidth.roundToPx() }
    val minSecondPaneSizePx = with(density) { secondPaneMinWidth.roundToPx() }

    // Calculate initial sizes based on divider position
    val initialFirstSize = (availableSpace * state.dividerPosition).roundToInt()
    val initialSecondSize = availableSpace - initialFirstSize

    val (adjustedFirstSize, adjustedSecondSize) =
        calculateAdjustedSizes(
            availableSpace,
            initialFirstSize,
            initialSecondSize,
            minFirstPaneSizePx,
            minSecondPaneSizePx,
        )

    // Update state.dividerPosition to match adjusted sizes
    state.dividerPosition = adjustedFirstSize.toFloat() / availableSpace.toFloat()

    // Use the adjusted sizes directly for constraints
    val firstConstraints =
        when (gapOrientation) {
            Orientation.VERTICAL -> constraints.copy(minWidth = adjustedFirstSize, maxWidth = adjustedFirstSize)
            Orientation.HORIZONTAL -> constraints.copy(minHeight = adjustedFirstSize, maxHeight = adjustedFirstSize)
        }

    val secondConstraints =
        when (gapOrientation) {
            Orientation.VERTICAL -> constraints.copy(minWidth = adjustedSecondSize, maxWidth = adjustedSecondSize)
            Orientation.HORIZONTAL -> constraints.copy(minHeight = adjustedSecondSize, maxHeight = adjustedSecondSize)
        }

    val firstPlaceable = firstMeasurable.measure(firstConstraints)
    val secondPlaceable = secondMeasurable.measure(secondConstraints)

    return layout(constraints.maxWidth, constraints.maxHeight) {
        when (gapOrientation) {
            Orientation.VERTICAL -> {
                firstPlaceable.placeRelative(0, 0)
                dividerPlaceable.placeRelative(adjustedFirstSize, 0)
                dividerHandlePlaceable.placeRelative(adjustedFirstSize - handleWidth / 2, 0)
                secondPlaceable.placeRelative(adjustedFirstSize + dividerWidth, 0)
            }
            Orientation.HORIZONTAL -> {
                firstPlaceable.placeRelative(0, 0)
                dividerPlaceable.placeRelative(0, adjustedFirstSize)
                dividerHandlePlaceable.placeRelative(0, adjustedFirstSize - handleWidth / 2)
                secondPlaceable.placeRelative(0, adjustedFirstSize + dividerWidth)
            }
        }
    }
}

internal class SplitResult(val gapOrientation: Orientation, val gapBounds: Rect)

internal interface SplitLayoutStrategy {
    fun calculateSplitResult(density: Density, layoutDirection: LayoutDirection, state: SplitLayoutState): SplitResult

    fun isHorizontal(): Boolean
}

internal fun horizontalTwoPaneStrategy(gapWidth: Dp = 0.dp): SplitLayoutStrategy =
    object : SplitLayoutStrategy {
        override fun calculateSplitResult(
            density: Density,
            layoutDirection: LayoutDirection,
            state: SplitLayoutState,
        ): SplitResult {
            val layoutCoordinates = state.layoutCoordinates ?: return SplitResult(Orientation.VERTICAL, Rect.Zero)
            val availableWidth = layoutCoordinates.size.width.toFloat().coerceAtLeast(1f)
            val splitWidthPixel = with(density) { gapWidth.toPx() }

            val dividerPosition = state.dividerPosition.coerceIn(0f, 1f)
            val splitX = (availableWidth * dividerPosition).coerceIn(0f, availableWidth)

            return SplitResult(
                gapOrientation = Orientation.VERTICAL,
                gapBounds =
                    Rect(
                        left = splitX - splitWidthPixel / 2f,
                        top = 0f,
                        right = (splitX + splitWidthPixel / 2f).coerceAtMost(availableWidth),
                        bottom = layoutCoordinates.size.height.toFloat().coerceAtLeast(1f),
                    ),
            )
        }

        override fun isHorizontal(): Boolean = true
    }

internal fun verticalTwoPaneStrategy(gapHeight: Dp = 0.dp): SplitLayoutStrategy =
    object : SplitLayoutStrategy {
        override fun calculateSplitResult(
            density: Density,
            layoutDirection: LayoutDirection,
            state: SplitLayoutState,
        ): SplitResult {
            val layoutCoordinates = state.layoutCoordinates ?: return SplitResult(Orientation.HORIZONTAL, Rect.Zero)
            val availableHeight = layoutCoordinates.size.height.toFloat().coerceAtLeast(1f)
            val splitHeightPixel = with(density) { gapHeight.toPx() }

            val dividerPosition = state.dividerPosition.coerceIn(0f, 1f)
            val splitY = (availableHeight * dividerPosition).coerceIn(0f, availableHeight)

            return SplitResult(
                gapOrientation = Orientation.HORIZONTAL,
                gapBounds =
                    Rect(
                        left = 0f,
                        top = splitY - splitHeightPixel / 2f,
                        right = layoutCoordinates.size.width.toFloat().coerceAtLeast(1f),
                        bottom = (splitY + splitHeightPixel / 2f).coerceAtMost(availableHeight),
                    ),
            )
        }

        override fun isHorizontal(): Boolean = false
    }

private fun calculateAdjustedSizes(
    availableSpace: Int,
    initialFirstSize: Int,
    initialSecondSize: Int,
    minFirstPaneSizePx: Int,
    minSecondPaneSizePx: Int,
): Pair<Int, Int> {
    val totalMinSize = minFirstPaneSizePx + minSecondPaneSizePx

    if (availableSpace <= totalMinSize) {
        // Distribute space proportionally based on minimum sizes
        val firstRatio = minFirstPaneSizePx.toFloat() / totalMinSize
        val adjustedFirstSize = (availableSpace * firstRatio).roundToInt()
        val adjustedSecondSize = availableSpace - adjustedFirstSize
        return adjustedFirstSize to adjustedSecondSize
    }

    var adjustedFirstSize = initialFirstSize
    var adjustedSecondSize = initialSecondSize

    // Adjust first pane size if it's below minimum
    if (adjustedFirstSize < minFirstPaneSizePx) {
        adjustedFirstSize = minFirstPaneSizePx
        adjustedSecondSize = availableSpace - adjustedFirstSize
    }

    // Adjust second pane size if it's below minimum
    if (adjustedSecondSize < minSecondPaneSizePx) {
        adjustedSecondSize = minSecondPaneSizePx
        adjustedFirstSize = availableSpace - adjustedSecondSize
    }

    // Ensure sizes are within constraints
    adjustedFirstSize = adjustedFirstSize.coerceIn(minFirstPaneSizePx, availableSpace - minSecondPaneSizePx)
    adjustedSecondSize = adjustedSecondSize.coerceIn(minSecondPaneSizePx, availableSpace - adjustedFirstSize)

    return adjustedFirstSize to adjustedSecondSize
}
