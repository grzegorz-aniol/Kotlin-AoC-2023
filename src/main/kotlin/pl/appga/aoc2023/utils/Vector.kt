package pl.appga.aoc2023.utils

import kotlin.math.sign

data class Vector(val x: Int, val y: Int, val z: Int = 0) {
    operator fun plus(d: Vector) = Vector(x + d.x, y + d.y, z + d.z)
    operator fun minus(d: Vector) = Vector(x - d.x, y - d.y, z - d.z)
    fun directionTo(d: Vector) = (d - this).let { Vector(it.x.sign, it.y.sign, it.z.sign) }
}

typealias Point = Vector
