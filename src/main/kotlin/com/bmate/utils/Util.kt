package com.bmate.utils

import java.io.File

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

fun ArrayList<Vec2>.containsValue(x: Int = -1, y: Int = -1) : Boolean {
    this.forEach {
        if(it.x == x && it.y == y) {
            return true
        }
    }
    return false
}