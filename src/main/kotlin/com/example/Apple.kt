package com.example

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class Apple(var x:Int, var y:Int) {
    fun draw(box: Double, graphicsContext: GraphicsContext) {
        graphicsContext.fill = Color.RED
        graphicsContext.fillRect(box * x, box * y, box, box)
    }
}