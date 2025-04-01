package com.bmate

import com.bmate.Game.Direction
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class Snake(vec: Vec2<Int>) : Object(vec) {
    constructor(x: Int = 0, y: Int = 0) : this(Vec2(x, y))

    private var last = Vec2(0, 0)
    private var before: Snake? = null
    var score: Int = 0

    override fun draw(box: Double, graphicsContext: GraphicsContext, color: Color) {
        super.draw(box, graphicsContext, color)
        before?.draw(box, graphicsContext, color)
    }

    private fun updatePosition(snake: Snake) {
        last.x = vec.x
        last.y = vec.y
        this.vec.x = snake.last.x
        this.vec.y = snake.last.y
        before?.updatePosition(this)
    }

    fun getSnakeVector(): ArrayList<Vec2<Int>> {
        var temp: Snake? = this
        val snakes: ArrayList<Vec2<Int>> = arrayListOf(temp!!.vec)
        while(temp?.before != null) {
            temp = temp.before
            if(temp != null) {
                snakes.add(temp.vec)
            }
        }
        return snakes
    }

    fun checkCollision(apple: Apple) : Boolean {
        if(apple.checkCollision(this)) {
            score+=10
            return true
        }
        return false
    }

    fun checkCollision(): Boolean {
        var temp = getSnakeVector()
        temp.remove(this.vec)
        temp.forEach {
            if(it.x == vec.x && it.y == vec.y)
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
        last.x = vec.x
        last.y = vec.y
        if (direction == Direction.LEFT) {
            vec.x--
            if(vec.x < 0)
                vec.x = 19
        }
        if (direction == Direction.RIGHT) {
            vec.x++
            if(vec.x > 19)
                vec.x = 0
        }
        if (direction == Direction.UP) {
            vec.y--
            if(vec.y < 0)
                vec.y = 19
        }
        if (direction == Direction.DOWN) {
            vec.y++
            if(vec.y > 19)
                vec.y = 0
        }
        before?.updatePosition(this)
    }
}