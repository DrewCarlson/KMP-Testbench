package testbench.ui

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


public data class TestbenchTextStyles(
    val default: TextStyle = TextStyle.Default,
    val h1: TextStyle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
    ),
    val h2: TextStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
    ),
    val h3: TextStyle = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
    ),
    val h4: TextStyle = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
    ),
    val h5: TextStyle = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
    ),
)
