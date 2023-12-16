package pl.appga.aoc2023.utils

import kotlin.math.abs
import kotlin.math.sign

data class Vector(val x: Int, val y: Int, val z: Int = 0) {
    operator fun unaryMinus() = Vector(-1 * x, -1 * y)
    operator fun plus(d: Vector) = Vector(x + d.x, y + d.y, z + d.z)
    operator fun minus(d: Vector) = Vector(x - d.x, y - d.y, z - d.z)
    fun directionTo(d: Vector) = (d - this).let { Vector(it.x.sign, it.y.sign, it.z.sign) }
    companion object {
        val N = Vector(0, -1)
        val S = Vector(0, 1)
        val E = Vector(1, 0)
        val W = Vector(-1, 0)
    }

    fun right() = when {
        this == N -> E
        this == E -> S
        this == S -> W
        this == W -> N
        else -> this
    }

    fun left() = when {
        this == N -> W
        this == W -> S
        this == S -> E
        this == E -> N
        else -> this
    }

}

typealias Point = Vector

fun Point.distanceTo(p: Point) = abs(p.x - x) + abs(p.y - y)
