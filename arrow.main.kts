#!/usr/bin/env kotlin
@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
@file:DependsOn("io.arrow-kt:arrow-core:2.1.2")
@file:DependsOn("io.arrow-kt:arrow-fx-coroutines:2.1.2")

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.flatMap

fun parseInt(str: String) =
    str.toIntOrNull()?.let(::Right) ?: Left("정수가 아님")

val parseSuccess = parseInt("42")
    .flatMap { n -> Right(n * 2) }

println(parseSuccess)

val parseFailure = parseInt("abcd")
    .flatMap { n -> Right(n * 2) }

println(parseFailure)
