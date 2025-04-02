package com.bmate.`object`

import com.bmate.Game
import com.bmate.utils.Vec2
import com.bmate.utils.containsValue
import kotlin.random.Random

class Apple(vec: Vec2) : Object(vec) {
    constructor(x: Int = 0, y: Int = 0) : this(Vec2(x, y))

    companion object {
        /**
         * Generates a new apple
         * @param snake generates apple based on snakes whereabouts
         * @return new apple object
         */
        fun generateApple(snake: Snake): Apple {
            var x = Random.nextInt(from = 0, until = Game.squareCount)
            var y = Random.nextInt(from = 0, until = Game.squareCount)
            while(snake.getSnakeVector().containsValue(x = x, y = y)) {
                x = Random.nextInt(from = 0, until = Game.squareCount)
                y = Random.nextInt(from = 0, until = Game.squareCount)
            }
            return Apple(x, y)
        }
    }
}