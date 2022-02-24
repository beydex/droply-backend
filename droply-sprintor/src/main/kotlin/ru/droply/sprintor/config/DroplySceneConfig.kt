package ru.droply.sprintor.config

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.droply.sprintor.scene.Scene
import ru.droply.sprintor.scene.annotation.DroplyScene
import ru.droply.sprintor.spring.attract
import ru.droply.sprintor.spring.filterKeyNotNull
import kotlin.reflect.full.findAnnotations

@Configuration
class DroplySceneConfig {
    @Autowired
    private lateinit var context: ApplicationContext
    private val logger = KotlinLogging.logger {}

    @Bean
    fun sceneList(): List<Scene<*>> {
        return context.getBeansWithAnnotation(DroplyScene::class.java).values
            .also { notifyIncorrectAnnotationUsage(it) }
            .filterIsInstance<Scene<*>>()
    }

    @Bean
    fun sceneMap(): Map<String, Scene<*>> {
        return sceneList()
            .map { scene -> extractPath(scene) to scene }
            // Filter scenes with unknown path
            .filterKeyNotNull()
            // Handle duplicates
            .also { notifyPathDuplicates(it) }
            .distinctBy { (path, _) -> path }
            .toMap()
    }

    // Warn when bean annotated with scene annotation is not actually a scene
    private fun notifyIncorrectAnnotationUsage(scenes: Collection<Any>) {
        scenes
            .filter { scene -> scene !is Scene<*> }
            .forEach { logger.warn { "Bean annotated with scene annotation is not a scene: $it" } }
    }

    private fun notifyPathDuplicates(info: Collection<Pair<String, Scene<*>>>) {
        // Attract path to all scenes that it belongs to
        // Get duplicates and error them
        info.attract()
            .filter { (_, values) -> values.size > 1 }
            .forEach { (path, scenes) ->
                logger.error(RuntimeException()) {
                    "Found duplicates for path $path: ${scenes.map { it::class }}"
                }
            }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun extractPath(scene: Scene<*>): String? {
        val annotations = scene::class.findAnnotations(DroplyScene::class)
        if (annotations.isEmpty()) {
            logger.error { "Extracting path info from scene ${scene::class} failed: no scene annotation found" }
            return null
        }

        return annotations.first().path
    }
}
