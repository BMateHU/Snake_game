package com.example

import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.stage.Stage
import kotlin.system.exitProcess

class Game : Application() {

    companion object {
        private const val WIDTH = 520
        private const val HEIGHT = 520
    }

    private lateinit var mainScene: Scene
    private lateinit var graphicsContext: GraphicsContext
    private lateinit var textField : Text
    private lateinit var textField2 : Text

    private lateinit var snake: Snake

    private var box = (HEIGHT /20).toDouble()

    private var time: Long = 0
    private var paused: Boolean = false

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

        textField = Text()
        textField.x = (WIDTH/2).toDouble()
        textField.y = (HEIGHT/2).toDouble()
        textField.font = Font(30.0)
        root.children.add(textField)

        textField2 = Text()
        textField2.x = (WIDTH/2).toDouble()
        textField2.y = (HEIGHT/2).toDouble()
        textField2.font = Font(30.0)
        root.children.add(textField2)

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
            onKeyReleased(event)
        }
    }

    private fun onKeyReleased(event: KeyEvent) {
        currentlyActiveKeys = null
        if(event.code == KeyCode.ESCAPE)
            paused = !paused

        if(paused) {
            textField.text = "Press Esc to continue!"
            textField.x = WIDTH/2 - textField.boundsInLocal.width/2
            textField.y = HEIGHT/2 - textField.boundsInLocal.height/2

            textField2.text = "Press R to restart!"
            textField2.x = WIDTH/2 - textField2.boundsInLocal.width/2
            textField2.y = HEIGHT/2 - textField2.boundsInLocal.height/2 + textField.boundsInLocal.height + 20
        }
        else {
            textField.text = ""
            textField2.text = ""
        }
    }

    private fun tickAndRender(currentNanoTime: Long) {
        // the time elapsed since the last frame, in nanoseconds
        // can be used for physics calculation, etc
        val elapsedNanos = currentNanoTime - lastFrameTime
        lastFrameTime = currentNanoTime
        time += elapsedNanos
        if(time / 300_000_000 >= 1 && !paused) {
            snake.updatePosition(currentDirection)
            if(snake.checkCollision()) {
                exitProcess(1)
            }
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

        if(paused) {
            if(currentlyActiveKeys?.equals(KeyCode.R) == true) {
                snake = Snake()
                currentDirection = Direction.DOWN
                paused = false
                textField.text = ""
                textField2.text = ""
            }
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
