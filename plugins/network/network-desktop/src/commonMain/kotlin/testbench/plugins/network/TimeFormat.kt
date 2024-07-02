package testbench.plugins.network

import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.char

internal val timeFormat: DateTimeFormat<DateTimeComponents> = DateTimeComponents.Format {
    hour()
    char(':')
    minute()
    char(':')
    second()
    char('.')
    secondFraction(3)
}
