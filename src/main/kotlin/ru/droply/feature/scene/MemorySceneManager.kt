package ru.droply.feature.scene

class MemorySceneManager : SceneManager {
    private val map: MutableMap<String, Scene<*>> = HashMap()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(path: String): Scene<T>? {
        return map[path] as? Scene<T>?
    }

    override fun <T : Any> set(path: String, scene: Scene<T>) {
        map[path] = scene
    }
}
