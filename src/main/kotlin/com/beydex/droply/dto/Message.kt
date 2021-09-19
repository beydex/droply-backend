package com.beydex.droply.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.jetbrains.annotations.NotNull

data class Message(@JsonProperty("message") @NotNull val message: String)
