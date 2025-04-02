package com.bmate.utils

class Vec2(var x: Int, var y: Int) {
    operator fun plus(other: Vec2): Vec2 {
        return Vec2(x + other.x, y + other.y)
    }

    operator fun minus(other: Vec2): Vec2 {
        return Vec2(x - other.x, y - other.y)
    }

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        other as Vec2
        return other.x == x && other.y == y
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}