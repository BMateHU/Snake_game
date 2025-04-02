package com.bmate.`object`

import com.bmate.utils.Vec2
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class SnakeBody(vec: Vec2) : Object(vec) {
    constructor(x: Int = 0, y: Int = 0) : this(Vec2(x, y))

    var last = Vec2(0, 0)

    /**
     * Linked list -> head first, after that the body
     */
    var before: SnakeBody? = null

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
    fun updatePosition(snake: SnakeBody) {
        last.x = vec.x
        last.y = vec.y
        this.vec.x = snake.last.x
        this.vec.y = snake.last.y
        before?.updatePosition(this)
    }
}