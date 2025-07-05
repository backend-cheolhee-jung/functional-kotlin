#!/usr/bin/env kotlin

import java.math.BigDecimal

fun <A, B, C> curry(
    block: (A, B) -> C,
) =
    { a: A ->
        { b: B ->
            block(a, b)
        }
    }

fun <A, B, C> uncurry(
    block: (A) -> (B) -> C,
) =
    { a: A, b: B ->
        block(a)(b)
    }

fun <A, B, C> compose(
    block: (A) -> B,
    block2: (B) -> C,
) =
    { a: A ->
        block2(block(a))
    }

val c = curry { a: Int, b: Int ->
    a + b
}

println(
    c(1)(2)
)

val calculateTax = curry { price: BigDecimal, taxRate: BigDecimal ->
    price - (price * taxRate)
}

val money = calculateTax(100.toBigDecimal())
val apply10PercentTax = money(0.10.toBigDecimal())
val apply25PercentTax = money(0.25.toBigDecimal())

println("100원에 10% 세금 적용: $apply10PercentTax")
println("100원에 25% 세금 적용: $apply25PercentTax")

val d = uncurry { a: Int ->
    { b: Int ->
        a + b
    }
}

println(
    d(1, 2)
)

val calculateTaxV2 = uncurry { price: BigDecimal ->
    { taxRate: BigDecimal ->
        price - (price * taxRate)
    }
}

val apply10PercentTax2 = calculateTaxV2(100.toBigDecimal(), 0.10.toBigDecimal())
val apply25PercentTax2 = calculateTaxV2(100.toBigDecimal(), 0.25.toBigDecimal())

println("100원에 10% 세금 적용: $apply10PercentTax2")
println("100원에 25% 세금 적용: $apply25PercentTax2")

val e = compose(
    { a: Int -> a + 1 },
    { b: Int -> b * 2 }
)

println(
    e(1)
)

val calculateTaxV3 = compose(
    { price: BigDecimal -> price - (price * 0.10.toBigDecimal()) },
    { tax: BigDecimal -> tax + 5.toBigDecimal() }
)

val tax = calculateTaxV3(100.toBigDecimal())

println("100원에 10% 세금 적용 후 5원 추가: $tax")