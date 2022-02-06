package ru.droply.feature.middleware

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import ru.droply.feature.scene.Scene

interface DroplyMiddleware {
    /**
     * Runs before forwarding user's request to the scene
     *
     * @param scene target scene
     * @param request serialized user request
     * @param session user websocket session
     */
    fun <T : Any> beforeForward(scene: Scene<T>, request: T, session: DefaultWebSocketSession)
}
