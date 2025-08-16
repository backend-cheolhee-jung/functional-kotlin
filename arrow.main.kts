#!/usr/bin/env kotlin
@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
@file:DependsOn("io.arrow-kt:arrow-core:2.1.2")
@file:DependsOn("io.arrow-kt:arrow-fx-coroutines:2.1.2")

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.Option
import arrow.core.flatMap

fun parseInt(str: String) =
    str.toIntOrNull()?.let(::Right) ?: Left("정수가 아님")

val parseSuccess = parseInt("42")
    .flatMap { n -> Right(n * 2) }

println(parseSuccess)

val parseFailure = parseInt("abcd")
    .flatMap { n -> Right(n * 2) }

println(parseFailure)

fun <T: Any> T?.toOption() =
    Option.fromNullable(this)

val someValue: String? = "Hello"
val noneValue: String? = null
val someOption = someValue.toOption()
val noneOption = noneValue.toOption()
val options = listOf(someOption, noneOption)

println(someOption)
println(noneOption)

options.forEach { option ->
    option.fold(
        ifEmpty = {
            println("값이 없습니다.")
        },
        ifSome = {
            println("값이 있습니다: $it")
        }
    )
}

val either = someOption.toEither(ifEmpty = { "값이 없습니다." })
println(either.leftOrNull())
either.onRight {
    println("값이 있습니다: $it")
}