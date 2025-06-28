#!/usr/bin/env kotlin
@file:Suppress("UNCHECKED_CAST") // <- 요거 개꿀팁이니까 알아가세용
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.reflect.KClass

interface Service {
    fun work(message: String): String
}

class ServiceImpl : Service {
    override fun work(message: String): String {
        println("실제 로직 실행: $message")
        return "결과: $message"
    }
}

class ServiceProxy(
    private val service: Service,
) : Service {
    override fun work(message: String): String {
        println("work 호출 전: 인자=$message")
        val result = service.work(message)
        println("work 호출 후: 결과=$result")
        return result
    }
}

open class ConcreteService {
    open fun work(message: String): String {
        println("실제 로직 실행: $message")
        return "결과: $message"
    }
}

class ConcreteServiceProxy(
    private val concreteService: ConcreteService = ConcreteService() // <-요기보면 기본 생성자가 필요하죠?
    // 클래스에 붙은 proxy는 나중에 기본 생성자에 setter 방식처럼 넣어야하니까 기본생성자 없으면 프록시 안만들어지는 이유에용.
): ConcreteService() {
    override fun work(message: String): String {
        println("work 호출 전: 인자=$message")
        val result = concreteService.work(message)
        println("work 호출 후: 결과=$result")
        return result
    }
}

class ProxyBuilder<out T : Any>(
    private val target: T,
    private val interfaceClass: KClass<T>,
) {
    private lateinit var before: ((Method, Array<Any>) -> Unit)
    private lateinit var after: ((Method, Any) -> Unit)

    fun invokeBefore(block: (Method, Array<Any>) -> Unit) {
        before = block
    }

    fun invokeAfter(block: (Method, Any) -> Unit) {
        after = block
    }

    fun build(): T {
        val handler = InvocationHandler { _, method, args ->
            val actualArgs: Array<Any> = args.map { it as Any }.toTypedArray()

            before.invoke(method, actualArgs)

            val result = method.invoke(target, *actualArgs)

            after.invoke(method, result as Any)
            result
        }

        return Proxy.newProxyInstance(
            interfaceClass.java.classLoader,
            arrayOf(interfaceClass.java),
            handler
        ) as T
    }
}

inline fun <reified T : Any> aopProxy(target: T, block: ProxyBuilder<T>.() -> Unit): T {
    val builder = ProxyBuilder(target, T::class)
    builder.block()
    return builder.build()
}

val service = aopProxy<Service>(ServiceImpl()) {
    invokeBefore { method, args ->
        println("${method.name} 호출 전: 인자=${args.toList()}")
    }

    invokeAfter { method, result ->
        println("${method.name} 호출 후: 결과=$result")
    }
}

val result = service.work("코틀린으로 프록스 생성해보기")
println(result)

/**
 * 아래 주석을 실행하면 에러나는데 이유가 인터페이스가 아닌 일반 클래스를 Proxy.newProxyInstance로 만들면 에러나요.
 * Proxy.newProxyInstance로 만드는 프록시 객체는 Proxy 클래스를 상속하고 반환됩니다.
 *
 * Proxy.newProxyInstance로 만드는 클래스의 정보를 보면
 * 1. interface -> class ServiceProxy extends Proxy(), Service
 * 2. concrete class -> class ConcreteServiceProxy extends Proxy(), ConcreteService() <- 다중 상속 형태
 *
 * Java는 다중 상속을 허용하지 않는데, 일반 클래스에서 프록시를 만들면 Proxy 클래스를 상속받을 수 없어서 에러납니당~
 * 아까 문제 낼 때 말했듯이 JDK Dynamic Proxy가 프록시 만들어줄 때 Proxy.newProxyInstance를 사용하기 떄문에 일반 클래스는 CGLIB를 사용해요
 */
//val concreteService = aopProxy<ConcreteService>(ConcreteService()) {
//    invokeBefore { method, args ->
//        println("${method.name} 호출 전: 인자=${args.toList()}")
//    }
//
//    invokeAfter { method, result ->
//        println("${method.name} 호출 후: 결과=$result")
//    }
//}
//
//val result2 = concreteService.work("코틀린으로 프록스 생성해보기2")
//println(result2)
