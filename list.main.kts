#!/usr/bin/env kotlin

sealed interface List<out T> {
    data class Cons<T>(val head: T, val tail: List<T>) : List<T>
    data object Nil : List<Nothing>

    fun isEmpty(): Boolean = this is Nil

    fun <R> map(transform: (T) -> R) =
        when (this) {
            is Cons -> Cons(transform(head), tail.map(transform))
            Nil -> Nil
        }

    fun sum(): Int =
        when (this) {
            is Cons -> head as Int + tail.sum()
            Nil -> 0
        }

    fun product(): Double =
        when (this) {
            is Cons -> (head as Double) * tail.product()
            Nil -> 1.0
        }

    fun sum2(): Int = foldLeft(this, 0) { acc, value -> acc + (value as Int) }
    fun product2(): Double = foldLeft(this, 1.0) { acc, value -> acc * (value as Double) }

    fun tail() =
        when (this) {
            is Cons -> tail
            Nil -> this
        }

    fun setHead(newHead: @UnsafeVariance T): List<T> =
        when (this) {
            is Cons -> Cons(newHead, tail)
            Nil -> Cons(newHead, Nil)
        }

    companion object {
        fun <T> of(vararg elements: T): List<T> {
            val tail = elements.sliceArray(1 until elements.size)
            return if (elements.isEmpty()) Nil
            else Cons(elements[0], of(*tail))
        }
    }
}

fun <T, U> foldLeft(
    list: List<T>,
    initial: U,
    operation: (U, T) -> U
): U = when (list) {
    is List.Cons -> foldLeft(list.tail, operation(initial, list.head), operation)
    is List.Nil -> initial
}

val numbers = List.of(1, 2, 3, 4, 5)
println("Sum: ${numbers.sum()}")
println("Product: ${numbers.product()}")
println("Sum2: ${numbers.sum2()}")
println("Product2: ${numbers.product2()}")
println("Tail: ${numbers.tail()}")
println("SetHead: ${numbers.setHead(10)}")
