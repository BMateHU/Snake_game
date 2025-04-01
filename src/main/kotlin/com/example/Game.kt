package com.example

import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.stage.Stage

class Game : Application() {

    companion object {
        private const val WIDTH = 520
        private const val HEIGHT = 520
    }

    private lateinit var mainScene: Scene
    private lateinit var graphicsContext: GraphicsContext

    private lateinit var snake: Snake

    private var box = (HEIGHT /20).toDouble()

    private var time: Long = 0
    private var currentDirection: Direction = Direction.DOWN

    private var lastFrameTime: Long = System.nanoTime()

    // use a set so duplicates are not possible
    private var currentlyActiveKeys : KeyCode? = null

    override fun start(mainStage: Stage) {
        mainStage.title = "Event Handling"

        val root = Group()
        mainScene = Scene(root)
        mainStage.scene = mainScene

        val canvas = Canvas(WIDTH.toDouble(), HEIGHT.toDouble())
        root.children.add(canvas)

        prepareActionHandlers()

        graphicsContext = canvas.graphicsContext2D

        snake = Snake()
        snake.addSnake(Snake())
        snake.addSnake(Snake())

        // Main loop
        object : AnimationTimer() {
            override fun handle(currentNanoTime: Long) {
                tickAndRender(currentNanoTime)
            }
        }.start()

        mainStage.show()
    }

    private fun prepareActionHandlers() {
        mainScene.onKeyPressed = EventHandler { event ->
            currentlyActiveKeys = event.code
        }
        mainScene.onKeyReleased = EventHandler { event ->
            currentlyActiveKeys = null
        }
    }

    private fun tickAndRender(currentNanoTime: Long) {
        // the time elapsed since the last frame, in nanoseconds
        // can be used for physics calculation, etc
        val elapsedNanos = currentNanoTime - lastFrameTime
        lastFrameTime = currentNanoTime
        time += elapsedNanos
        if(time / 300_000_000 >= 1) {
            snake.updatePosition(currentDirection)
            time = 0
        }

        // clear canvas
        graphicsContext.clearRect(0.0, 0.0, WIDTH.toDouble(), HEIGHT.toDouble())

        currentDirection = changeDirection()

        // draw background
        graphicsContext.fill = Color.GRAY
        graphicsContext.fillRect(0.0, 0.0, WIDTH.toDouble(), HEIGHT.toDouble())

        snake.draw(box, graphicsContext)

        // display crude fps counter
        val elapsedMs = elapsedNanos / 1_000_000
        if (elapsedMs != 0L) {
            graphicsContext.fill = Color.WHITE
            graphicsContext.fillText("${1000 / elapsedMs} fps", 10.0, 10.0)
        }
    }

    enum class Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private fun changeDirection() : Direction {
        if ((currentlyActiveKeys?.equals(KeyCode.W) == true || currentlyActiveKeys?.equals(KeyCode.UP) == true) && currentDirection != Direction.DOWN) {
            return Direction.UP
        }
        else if((currentlyActiveKeys?.equals(KeyCode.DOWN) == true || currentlyActiveKeys?.equals(KeyCode.S) == true) && currentDirection != Direction.UP) {
            return Direction.DOWN
        }
        else if((currentlyActiveKeys?.equals(KeyCode.LEFT) == true || currentlyActiveKeys?.equals(KeyCode.A) == true) && currentDirection != Direction.RIGHT) {
            return Direction.LEFT
        }
        else if((currentlyActiveKeys?.equals(KeyCode.RIGHT) == true || currentlyActiveKeys?.equals(KeyCode.D) == true) && currentDirection != Direction.LEFT) {
            return Direction.RIGHT
        }
        return currentDirection
    }

}
