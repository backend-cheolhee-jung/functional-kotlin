#!/usr/bin/env kotlin

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

interface Service {
    fun work(message: String): String
}

class RealService : Service {
    override fun work(message: String): String {
        println("실제 로직 실행: $message")
        return "결과: $message"
    }
}

class ProxyBuilder<T : Any>(
    private val target: T,
    private val interfaceClass: Class<T>,
) {
    private var before: ((Method, Array<Any>) -> Unit)? = null
    private var after: ((Method, Any) -> Unit)? = null

    fun invokeBefore(block: (Method, Array<Any>) -> Unit) {
        before = block
    }

    fun invokeAfter(block: (Method, Any) -> Unit) {
        after = block
    }

    fun build(): T {
        val handler = InvocationHandler { _, method, args ->
            val actualArgs: Array<Any> = args?.map { it as Any }?.toTypedArray() ?: emptyArray()

            before?.invoke(method, actualArgs)

            val result = method.invoke(target, *actualArgs)

            after?.invoke(method, result as Any)
            result
        }

        @Suppress("UNCHECKED_CAST")
        return Proxy.newProxyInstance(
            interfaceClass.classLoader,
            arrayOf(interfaceClass),
            handler
        ) as T
    }
}

inline fun <reified T : Any> aopProxy(target: T, block: ProxyBuilder<T>.() -> Unit): T {
    val builder = ProxyBuilder(target, T::class.java)
    builder.block()
    return builder.build()
}

val service = aopProxy<Service>(RealService()) {
    invokeBefore { method, args ->
        println("👉 ${method.name} 호출 전: 인자=${args?.toList()}")
    }

    invokeAfter { method, result ->
        println("✅ ${method.name} 호출 후: 결과=$result")
    }
}

val result = service.work("코틀린 DSL로 AOP 흉내내기")
println(result)
