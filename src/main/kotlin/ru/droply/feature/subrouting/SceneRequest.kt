package ru.droply.feature.subrouting

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class SceneRequest @JsonCreator constructor(@JsonProperty("path") var path: String)