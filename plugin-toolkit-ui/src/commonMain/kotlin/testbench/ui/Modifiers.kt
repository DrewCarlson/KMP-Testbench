package testbench.ui

import androidx.compose.ui.Modifier

public fun Modifier.thenIf(condition: Boolean, body: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        Modifier.then(body())
    } else {
        this
    }
}
