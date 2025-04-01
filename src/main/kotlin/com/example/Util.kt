package com.example

import java.io.File

fun getResource(filename: String): String {
    return Game::class.java.getResource(filename).toString()
}

fun loadScore(): Int {
    if(!File("bestScore.txt").exists()) {
        File("bestScore.txt").createNewFile()
        File("bestScore.txt").writeText("0")
    }
    return File("bestScore.txt").readLines()[0].toInt()
}

fun saveScore(score: Int) {
    if(!File("bestScore.txt").exists())
        File("bestScore.txt").createNewFile()
    File("bestScore.txt").writeText("$score")
}