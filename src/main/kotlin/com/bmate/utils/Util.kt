package com.bmate.utils

import com.bmate.`object`.Apple
import com.bmate.`object`.Snake
import java.io.File
import java.io.FileWriter

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

/**
 * Writes out the map as text
 * @param snake head of the snake
 * @param apple apple object
 * @param boxCount square count per row/column
 */
fun saveMapAsText(snake: Snake, apple: Apple, boxCount: Int) {
    val fileWriter = FileWriter(File("map.txt"))
    for(i in boxCount*2 downTo 0)
        fileWriter.write("\u035F")
    fileWriter.write("\n")
    for(i in 0 until boxCount) {
        fileWriter.write("|")
        for(j in 0 until  boxCount) {
            if(snake.getSnakeVector().containsValue(j, i))
                fileWriter.write("S")
            else if(apple.vec == Vec2(j, i))
                fileWriter.write("A")
            else
                fileWriter.write(" ")
            fileWriter.write("|")
        }
        fileWriter.write("\n")
    }
    for(i in boxCount*2 downTo 0)
        fileWriter.write("\u035E")
    fileWriter.close()
}

fun ArrayList<Vec2>.containsValue(x: Int = -1, y: Int = -1) : Boolean {
    this.forEach {
        if(it.x == x && it.y == y) {
            return true
        }
    }
    return false
}