package com.example

import com.example.Game.Direction
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class Snake {
    private var snakeX = 0
    private var snakeY = 0
    private var lastX = 0;
    private var lastY = 0;
    var before: Snake? = null

    fun draw(box: Double, graphicsContext: GraphicsContext) {
        graphicsContext.fill = Color.BLUE
        graphicsContext.fillRect(box * snakeX, box * snakeY, box, box)
        before?.draw(box, graphicsContext)
    }

    private fun updatePosition(snake: Snake) {
        lastX = snakeX
        lastY = snakeY
        this.snakeX = snake.lastX
        this.snakeY = snake.lastY
        before?.updatePosition(this)
    }

    fun addSnake(snake: Snake) {
        var temp: Snake? = this
        while(temp?.before != null) {
            temp = temp.before
        }
        temp?.before = snake
    }

    fun updatePosition(direction: Direction = Direction.DOWN) {
        lastX = snakeX
        lastY = snakeY
        if (direction == Direction.LEFT) {
            snakeX--
            if(snakeX < 0)
                snakeX = 19
        }
        if (direction == Direction.RIGHT) {
            snakeX++
            if(snakeX > 19)
                snakeX = 0
        }
        if (direction == Direction.UP) {
            snakeY--
            if(snakeY < 0)
                snakeY = 19
        }
        if (direction == Direction.DOWN) {
            snakeY++
            if(snakeY > 19)
                snakeY = 0
        }
        before?.updatePosition(this)
    }
}