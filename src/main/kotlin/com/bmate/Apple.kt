package com.bmate

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
            var x = Random.nextInt(from = 0, until = 20)
            var y = Random.nextInt(from = 0, until = 20)
            while(snake.getSnakeVector().containsValue(x = x))
                x = Random.nextInt(from = 0, until = 20)
            while(snake.getSnakeVector().containsValue(y = y))
                y = Random.nextInt(from = 0, until = 20)
            return Apple(x, y)
        }
    }
}