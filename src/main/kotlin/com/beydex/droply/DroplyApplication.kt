package com.beydex.droply

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class DroplyApplication

fun main(args: Array<String>) {
    runApplication<DroplyApplication>(*args)
}
