package ru.droply.feature.subrouting

class MemorySceneManager: SceneManager {
    private val map: MutableMap<String, Scene> = HashMap()

    override fun get(path: String): Scene? {
        return map[path]
    }

    override fun set(path: String, scene: Scene) {
        map[path] = scene
    }
}