package ru.droply.sprintor.scene

interface SceneManager {
    operator fun <T : Any> get(path: String): Scene<T>?
    operator fun <T : Any> set(path: String, scene: Scene<T>)
}
