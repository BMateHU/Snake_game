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
import kotlin.random.Random

class Game : Application() {

    companion object {
        private const val WIDTH = 520
        private const val HEIGHT = 520
    }

    private lateinit var mainScene: Scene
    private lateinit var graphicsContext: GraphicsContext
    private lateinit var continueText : Text
    private lateinit var bestScoreText : Text
    private lateinit var scoreText : Text

    private lateinit var snake: Snake
    private lateinit var apple : Apple

    private var box = (HEIGHT /20).toDouble()

    private var time: Long = 0
    private var paused: Boolean = false
    private var moved: Boolean = false
    private var bestScore : Int = 0

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

        continueText = Text()
        continueText.x = (WIDTH/2).toDouble()
        continueText.y = (HEIGHT/2).toDouble()
        continueText.font = Font(30.0)
        root.children.add(continueText)

        bestScore = loadScore()

        bestScoreText = Text()
        bestScoreText.x = (WIDTH/2).toDouble()
        bestScoreText.y = (HEIGHT/2).toDouble()
        bestScoreText.font = Font(30.0)
        root.children.add(bestScoreText)

        scoreText = Text()
        scoreText.x = (WIDTH/2).toDouble()
        scoreText.y = (HEIGHT/2).toDouble()
        scoreText.font = Font(20.0)
        root.children.add(scoreText)

        prepareActionHandlers()

        graphicsContext = canvas.graphicsContext2D

        snake = Snake()
        apple = Apple(Random.nextInt(from = 0, until = 20), Random.nextInt(from = 0, until = 20))

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
        if(event.code == KeyCode.ESCAPE && continueText.text != "Press R to restart!")
            paused = !paused

        if(paused && continueText.text != "Press R to restart!") {
            continueText.text = "Press Esc to continue!\nPress R to restart!"
            continueText.x = WIDTH/2 - continueText.boundsInLocal.width/2
            continueText.y = HEIGHT/2 - continueText.boundsInLocal.height/2

            bestScoreText.text = "Best score: $bestScore"
            bestScoreText.x = WIDTH/2 - bestScoreText.boundsInLocal.width/2
            bestScoreText.y = HEIGHT/2 - bestScoreText.boundsInLocal.height/2 + continueText.boundsInLocal.height + 20
        }
        else {
            continueText.text = ""
            bestScoreText.text = ""
        }
    }

    private fun tickAndRender(currentNanoTime: Long) {
        // the time elapsed since the last frame, in nanoseconds
        // can be used for physics calculation, etc
        val elapsedNanos = currentNanoTime - lastFrameTime
        lastFrameTime = currentNanoTime
        time += elapsedNanos
        if(time / 100_000_000 >= 1 && !paused) {
            snake.updatePosition(currentDirection)
            moved = true
            if(snake.checkCollision()) {
                continueText.text = "Press R to restart!"
                continueText.x = WIDTH/2 - continueText.boundsInLocal.width/2
                continueText.y = HEIGHT/2 - continueText.boundsInLocal.height/2

                bestScoreText.text = "Best score: $bestScore"
                bestScoreText.x = WIDTH/2 - bestScoreText.boundsInLocal.width/2
                bestScoreText.y = HEIGHT/2 - bestScoreText.boundsInLocal.height/2 + continueText.boundsInLocal.height + 20

                paused = true
                if(bestScore < snake.score)
                    saveScore(snake.score)
            }
            time = 0
        }

        scoreText.text = "Score: ${snake.score}"
        scoreText.x = WIDTH - scoreText.boundsInLocal.width - 10
        scoreText.y = scoreText.boundsInLocal.height

        if(snake.checkCollision(apple)) {
            var x = Random.nextInt(from = 0, until = 20)
            var y = Random.nextInt(from = 0, until = 20)
            while(snake.getSnakeX().contains(x))
                x = Random.nextInt(from = 0, until = 20)
            while(snake.getSnakeY().contains(y))
                y = Random.nextInt(from = 0, until = 20)
            apple = Apple(x, y)
            snake.addSnake(Snake())
        }

        // clear canvas
        graphicsContext.clearRect(0.0, 0.0, WIDTH.toDouble(), HEIGHT.toDouble())

        currentDirection = changeDirection()

        // draw background
        graphicsContext.fill = Color.GRAY
        graphicsContext.fillRect(0.0, 0.0, WIDTH.toDouble(), HEIGHT.toDouble())

        snake.draw(box, graphicsContext)
        apple.draw(box, graphicsContext)

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
                continueText.text = ""
                bestScoreText.text = ""
                bestScore = loadScore()
            }
        }
    }

    enum class Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private fun changeDirection() : Direction {
        if(moved) {
            if ((currentlyActiveKeys?.equals(KeyCode.W) == true || currentlyActiveKeys?.equals(KeyCode.UP) == true) && currentDirection != Direction.DOWN) {
                moved = false
                return Direction.UP
            } else if ((currentlyActiveKeys?.equals(KeyCode.DOWN) == true || currentlyActiveKeys?.equals(KeyCode.S) == true) && currentDirection != Direction.UP) {
                moved = false
                return Direction.DOWN
            } else if ((currentlyActiveKeys?.equals(KeyCode.LEFT) == true || currentlyActiveKeys?.equals(KeyCode.A) == true) && currentDirection != Direction.RIGHT) {
                moved = false
                return Direction.LEFT
            } else if ((currentlyActiveKeys?.equals(KeyCode.RIGHT) == true || currentlyActiveKeys?.equals(KeyCode.D) == true) && currentDirection != Direction.LEFT) {
                moved = false
                return Direction.RIGHT
            }
        }
        return currentDirection
    }

}
