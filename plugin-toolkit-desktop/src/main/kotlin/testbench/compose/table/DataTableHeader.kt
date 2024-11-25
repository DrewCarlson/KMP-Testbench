package testbench.compose.table

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import testbench.ui.testbench

@Composable
public fun DataTableHeader(
    text: String,
    maxLines: Int = 1,
    modifier: Modifier = Modifier.padding(6.dp),
    fontSize: TextUnit = 12.sp,
    fontWeight: FontWeight = FontWeight.Bold,
) {
    testbench.Text(
        text = text,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
        style = TextStyle.Default.copy(
            fontSize = fontSize,
            fontWeight = fontWeight,
        ),
    )
}
