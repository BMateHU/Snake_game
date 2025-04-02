package com.bmate

import com.bmate.`object`.Apple
import com.bmate.`object`.Snake
import com.bmate.`object`.SnakeBody
import com.bmate.utils.loadScore
import com.bmate.utils.saveScore
import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.TextInputDialog
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.stage.Stage
import java.util.*
import kotlin.properties.Delegates
import kotlin.random.Random

class Game : Application() {

    companion object {
        const val WIDTH = 520
        const val HEIGHT = 520
        var squareCount: Int = -1
    }

    private lateinit var mainScene: Scene
    private lateinit var graphicsContext: GraphicsContext
    private lateinit var continueText : Text
    private lateinit var bestScoreText : Text
    private lateinit var scoreText : Text

    private lateinit var snake: Snake
    private lateinit var apple : Apple

    //map contains squareCount*squareCount squares
    private var box by Delegates.notNull<Double>()

    private var time: Long = 0
    private var paused: Boolean = false
    private var moved: Boolean = false
    private var bestScore : Int = 0

    /**
     * Direction where the snake goes
     */
    private var currentDirection: Direction = Direction.DOWN

    private var lastFrameTime: Long = System.nanoTime()

    // use a set so duplicates are not possible
    private var currentlyActiveKeys : KeyCode? = null

    override fun start(mainStage: Stage) {
        mainStage.title = "Snake"

        var mapSize: Optional<String>
        while(squareCount < 0) {
            val tid = TextInputDialog()
            tid.title = "Choose map size!"
            tid.contentText = "Choose map size"
            tid.headerText = ""
            tid.graphic = null
            mapSize = tid.showAndWait()
            if (mapSize.get().matches("[0-9]+".toRegex()))
                squareCount = mapSize.get().toInt()
        }

        box = (HEIGHT / squareCount).toDouble()

        val root = Group()
        mainScene = Scene(root)
        mainStage.scene = mainScene

        val canvas = Canvas(WIDTH.toDouble(), HEIGHT.toDouble())
        root.children.add(canvas)

        continueText = Text()
        continueText.font = Font(30.0)
        root.children.add(continueText)

        bestScore = loadScore()

        bestScoreText = Text()
        bestScoreText.font = Font(30.0)
        root.children.add(bestScoreText)

        scoreText = Text()
        scoreText.font = Font(20.0)
        root.children.add(scoreText)

        prepareActionHandlers()

        graphicsContext = canvas.graphicsContext2D

        snake = Snake()
        apple = Apple(Random.nextInt(from = 0, until = squareCount), Random.nextInt(from = 0, until = squareCount))

        // Main loop
        object : AnimationTimer() {
            override fun handle(currentNanoTime: Long) {
                tickAndRender(currentNanoTime)
            }
        }.start()

        mainStage.show()
    }

    /**
     * Changes menu text to the desired (best score cant be changed)
     * @param text Upper text field's text
     */
    private fun changeText(text: String) {
        continueText.text = text
        continueText.x = WIDTH/2 - continueText.boundsInLocal.width/2
        continueText.y = HEIGHT/2 - continueText.boundsInLocal.height/2

        bestScoreText.text = "Best score: $bestScore"
        bestScoreText.x = WIDTH/2 - bestScoreText.boundsInLocal.width/2
        bestScoreText.y = HEIGHT/2 - bestScoreText.boundsInLocal.height/2 + continueText.boundsInLocal.height + 20
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
            changeText("Press Esc to continue!\nPress R to restart!")
        }
        else if(!paused && continueText.text != "Press R to restart!") {
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

        //snake moves every 0.1 sec
        if(time / 200_000_000 >= 1 && !paused) {
            snake.updatePosition(currentDirection)
            //if moved, you can change direction
            moved = true
            //collision -> end of game
            if(snake.checkCollision()) {
                changeText("Press R to restart!")

                paused = true
                if(bestScore < snake.score)
                    saveScore(snake.score)
            }
            time = 0
        }

        //shows score real-time
        scoreText.text = "Score: ${snake.score}"
        scoreText.x = WIDTH - scoreText.boundsInLocal.width - 10
        scoreText.y = scoreText.boundsInLocal.height

        //Snake eats apple -> create new apple with random coords, if snake is on that generate new random
        if(snake.checkCollision(apple)) {
            apple = Apple.generateApple(snake)
            snake.addSnakeBody(SnakeBody(-1, -1))
        }

        // clear canvas
        graphicsContext.clearRect(0.0, 0.0, WIDTH.toDouble(), HEIGHT.toDouble())

        currentDirection = changeDirection()

        // draw background
        graphicsContext.fill = Color.GRAY
        graphicsContext.fillRect(0.0, 0.0, WIDTH.toDouble(), HEIGHT.toDouble())

        //draw objects
        snake.draw(box, graphicsContext, Color.GREEN)
        apple.draw(box, graphicsContext, Color.RED)

        // display crude fps counter
        val elapsedMs = elapsedNanos / 1_000_000
        if (elapsedMs != 0L) {
            graphicsContext.fill = Color.WHITE
            graphicsContext.fillText("${1000 / elapsedMs} fps", 10.0, 10.0)
        }

        //game can be paused, with R reset
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
