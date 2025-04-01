package com.bmate

import java.io.File

/*fun getResource(filename: String): String {
    return Game::class.java.getResource(filename).toString()
}*/

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

fun ArrayList<Vec2<Int>>.containsValue(x: Int = -1, y: Int = -1) : Boolean {
    this.forEach {
        if(it.x == x || it.y == y) {
            return true
        }
    }
    return false
}