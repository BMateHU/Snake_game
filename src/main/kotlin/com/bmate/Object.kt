package com.bmate

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

open class Object(var vec: Vec2<Int>) {
    constructor(x: Int = 0, y: Int = 0): this(Vec2(x, y))

    open fun draw(box: Double, graphicsContext: GraphicsContext, color: Color) {
        graphicsContext.fill = color
        graphicsContext.fillRect(box * vec.x, box * vec.y, box, box)
    }

    fun checkCollision(obj: Object) : Boolean {
        return obj.vec.x == vec.x && obj.vec.y == vec.y
    }
}