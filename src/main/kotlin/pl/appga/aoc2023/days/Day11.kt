package pl.appga.aoc2023.days

import pl.appga.aoc2023.utils.DayTask
import pl.appga.aoc2023.utils.Point
import kotlin.math.abs

fun main() {
    Day11().run(
        runTest1 = true,
        runTask1 = true,
        runTest2 = false,
        runTask2 = true,
        runTaskForFailedTest = true
    )
}

class Day11 : DayTask<Long>(11) {

    private data class Space(val stars: List<Point>)
    private data class Point(val x: Long, val y: Long)

    override fun solveTask1(inputFile: List<String>): Long {
        val space = inputFile.toExpandedSpace()
        return sumOfDistances(space)
    }

    override fun solveTask2(inputFile: List<String>): Long {
        val space = inputFile.toExpandedSpace(scale = 1000000)
        return sumOfDistances(space)
    }

    private fun sumOfDistances(space: Space): Long {
        var distance = 0L
        for (i1 in 0..<space.stars.size) {
            val star1 = space.stars[i1]
            for (i2 in i1 + 1 ..<space.stars.size) {
                val star2 = space.stars[i2]
                val d = distance(star1, star2)
                distance += d
            }
        }
        return distance
    }

    private fun distance(p1: Point, p2: Point): Long =
        abs(p1.x - p2.x) + abs(p1.y - p2.y)

    private fun List<String>.toExpandedSpace(scale: Long = 1): Space {
        var sx = 0
        var sy = 0
        var rowsWithStars = mutableSetOf<Int>()
        var columnsWithStars = mutableSetOf<Int>()
        val stars = flatMapIndexed { y, line ->
            ++sy
            sx = line.length
            line.mapIndexedNotNull { x, ch ->
                if (ch == '#') {
                    rowsWithStars += y
                    columnsWithStars += x
                    Point(x, y)
                } else {
                    null
                }
            }
        }.toSet()

        var ey = 0L
        val extendBy = if (scale == 1L) 1L else scale - 1L
        val expandedStars = mutableListOf<Point>()
        repeat(sy) { y ->
            if (y !in rowsWithStars) {
                ey += extendBy
            } else {
                var ex = 0L
                repeat(sx) { x ->
                    if (x !in columnsWithStars) {
                        ex += extendBy
                    } else if (Point(x, y) in stars) {
                        val expandedStar = Point(x + ex, y + ey)
                        expandedStars += expandedStar
                    }
                }
            }
        }

        return Space(expandedStars)
    }

    override val expectedTest1Result: Long = 374
    override val expectedTask1Result: Long = 9723824

    override val expectedTest2Result: Long = 1030
    override val expectedTask2Result: Long = 731244261352

}