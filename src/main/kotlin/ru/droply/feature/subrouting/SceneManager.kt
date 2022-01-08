package ru.droply.feature.subrouting

interface SceneManager {
   operator fun get(path: String): Scene?
   operator fun set(path: String, scene: Scene)
}