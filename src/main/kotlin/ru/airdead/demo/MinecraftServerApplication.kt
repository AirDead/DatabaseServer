package ru.airdead.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MinecraftServerApplication

fun main(args: Array<String>) {
    runApplication<MinecraftServerApplication>(*args)
}
