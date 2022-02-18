package ru.droply.sprintor.middleware

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import ru.droply.sprintor.scene.Scene
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotations

abstract class AnnotationMiddleware(private val annotationClass: KClass<out Annotation>) : DroplyMiddleware {
    @OptIn(ExperimentalStdlibApi::class)
    override fun <T : Any> beforeForward(scene: Scene<T>, request: T, session: DefaultWebSocketSession) {
        if (scene::class.findAnnotations(annotationClass).isNotEmpty()) {
            handleBeforeForward(scene, request, session)
        }
    }

    abstract fun <T : Any> handleBeforeForward(scene: Scene<T>, request: T, session: DefaultWebSocketSession)
}
