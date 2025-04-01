package com.example

import com.example.Game.Direction
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class Snake {
    private var x = 0
    private var y = 0
    private var lastX = 0
    private var lastY = 0
    var before: Snake? = null
    var score: Int = 0

    fun draw(box: Double, graphicsContext: GraphicsContext) {
        graphicsContext.fill = Color.BLUE
        graphicsContext.fillRect(box * x, box * y, box, box)
        before?.draw(box, graphicsContext)
    }

    private fun updatePosition(snake: Snake) {
        lastX = x
        lastY = y
        this.x = snake.lastX
        this.y = snake.lastY
        before?.updatePosition(this)
    }

    fun getSnakeX(): ArrayList<Int> {
        var temp: Snake? = this
        val snakes: ArrayList<Int> = arrayListOf(temp!!.x)
        while(temp?.before != null) {
            temp = temp.before
            if(temp != null) {
                snakes.add(temp.x)
            }
        }
        return snakes
    }

    fun getSnakeY(): ArrayList<Int> {
        var temp: Snake? = this
        val snakes: ArrayList<Int> = arrayListOf(temp!!.y)
        while(temp?.before != null) {
            temp = temp.before
            if(temp != null) {
                snakes.add(temp.y)
            }
        }
        return snakes
    }

    fun checkCollision() : Boolean {
        var temp: Snake? = this
        val snakes: ArrayList<Snake> = arrayListOf(temp!!)
        while(temp?.before != null) {
            temp = temp.before
            if(temp != null) {
                snakes.add(temp)
            }
        }
        for(snake in snakes) {
            if(x == snake.lastX && y == snake.lastY) {
                return true
            }
        }
        return false
    }

    fun checkCollision(apple: Apple) : Boolean {
        if(apple.x == x && apple.y == y) {
            score += 10
            return true
        }
        return false
    }

    fun addSnake(snake: Snake) {
        var temp: Snake? = this
        while(temp?.before != null) {
            temp = temp.before
        }
        temp?.before = snake
    }

    fun updatePosition(direction: Direction = Direction.DOWN) {
        lastX = x
        lastY = y
        if (direction == Direction.LEFT) {
            x--
            if(x < 0)
                x = 19
        }
        if (direction == Direction.RIGHT) {
            x++
            if(x > 19)
                x = 0
        }
        if (direction == Direction.UP) {
            y--
            if(y < 0)
                y = 19
        }
        if (direction == Direction.DOWN) {
            y++
            if(y > 19)
                y = 0
        }
        before?.updatePosition(this)
    }
}