package ru.droply.scene

import io.ktor.http.cio.websocket.*
import kotlinx.serialization.Serializable
import ru.droply.feature.ktor.ctx
import ru.droply.feature.scene.RestScene

@Serializable
data class HelloRequest(val name: String)

@Serializable
data class HelloResponse(val message: String)

class HelloScene : RestScene<HelloRequest, HelloResponse>(HelloRequest.serializer(), HelloResponse.serializer()) {
    override fun DefaultWebSocketSession.handle(request: HelloRequest): HelloResponse {
        ctx["name"] = request.name
        return HelloResponse("Welcome to Droply, ${request.name}")
    }
}