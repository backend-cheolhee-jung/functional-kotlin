#!/usr/bin/env kotlin
@file:Suppress("FunctionName")

import java.math.BigDecimal
import java.math.RoundingMode


inline fun BigDecimal(
    operation: BigDecimalConfiguration.() -> BigDecimal,
): BigDecimal {
    val config = BigDecimalConfiguration()
    return operation(config)
}

class BigDecimalConfiguration(
    var scale: Int = 2,
    var roundingMode: Int = RoundingMode.HALF_UP.ordinal,
) {
    operator fun BigDecimal.div(other: BigDecimal): BigDecimal =
        this.setScale(scale, RoundingMode.entries[roundingMode])
            .divide(
                other.setScale(
                    scale,
                    RoundingMode.entries[roundingMode]
                ),
                scale,
                RoundingMode.entries[roundingMode],
            )

    operator fun BigDecimal.times(other: BigDecimal): BigDecimal =
        this.setScale(scale, RoundingMode.entries[roundingMode])
            .multiply(
                other.setScale(
                    scale,
                    RoundingMode.entries[roundingMode],
                )
            )
}

val value = BigDecimal {
    scale = 2
    roundingMode = RoundingMode.DOWN.ordinal

    100.toBigDecimal() / 3.toBigDecimal()
}

println("Result: $value")