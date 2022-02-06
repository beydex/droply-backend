package ru.droply.feature.scene.variety

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer

abstract class OutRestScene<T : Any>(responseSerializer: KSerializer<T>) :
    RestScene<Unit, T>(Unit.serializer(), responseSerializer)
