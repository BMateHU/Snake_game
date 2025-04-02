package com.bmate

import com.bmate.Game.Direction
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class Snake(vec: Vec2) : Object(vec) {
    constructor(x: Int = 0, y: Int = 0) : this(Vec2(x, y))

    /**
     * Last position of the snake's part
     */
    private var last = Vec2(0, 0)

    /**
     * Linked list -> head first, after that the body
     */
    private var before: Snake? = null
    var score: Int = 0

    /**
     * Draw the object
     * @param box a square, changes drawing's size and placement (grid-like)
     */
    override fun draw(box: Double, graphicsContext: GraphicsContext, color: Color) {
        super.draw(box, graphicsContext, color)
        before?.draw(box, graphicsContext, color)
    }

    /**
     * Updates position for not-head snakes
     */
    private fun updatePosition(snake: Snake) {
        last.x = vec.x
        last.y = vec.y
        this.vec.x = snake.last.x
        this.vec.y = snake.last.y
        before?.updatePosition(this)
    }

    /**
     * @return all the snake pos vectors
     */
    fun getSnakeVector(): ArrayList<Vec2> {
        var temp: Snake? = this
        val snakes: ArrayList<Vec2> = arrayListOf(temp!!.vec)
        while(temp?.before != null) {
            temp = temp.before
            if(temp != null) {
                snakes.add(temp.vec)
            }
        }
        return snakes
    }

    /**
     * Checks collision with apple
     * @return true if collided
     */
    fun checkCollision(apple: Apple) : Boolean {
        if(apple.checkCollision(this)) {
            score+=10
            return true
        }
        return false
    }

    /**
     * Checks collision with itself
     * @return true if collided
     */
    fun checkCollision(): Boolean {
        var temp = getSnakeVector()
        temp.remove(this.vec)
        temp.forEach {
            if(it.x == vec.x && it.y == vec.y)
                return true
        }
        return false
    }

    /**
     * Adds new snake to the end
     */
    fun addSnake(snake: Snake) {
        var temp: Snake? = this
        while(temp?.before != null) {
            temp = temp.before
        }
        temp?.before = snake
    }

    /**
     * Updates snake head's position based on the direction, then updates the body's position
     */
    fun updatePosition(direction: Direction = Direction.DOWN) {
        last.x = vec.x
        last.y = vec.y
        if (direction == Direction.LEFT) {
            vec -= Vec2(1, 0)
            if(vec.x < 0)
                vec.x = Game.squareCount-1
        }
        if (direction == Direction.RIGHT) {
            vec += Vec2(1, 0)
            if(vec.x > Game.squareCount-1)
                vec.x = 0
        }
        if (direction == Direction.UP) {
            vec -= Vec2(0, 1)
            if(vec.y < 0)
                vec.y = Game.squareCount-1
        }
        if (direction == Direction.DOWN) {
            vec += Vec2(0, 1)
            if(vec.y > Game.squareCount-1)
                vec.y = 0
        }
        before?.updatePosition(this)
    }
}