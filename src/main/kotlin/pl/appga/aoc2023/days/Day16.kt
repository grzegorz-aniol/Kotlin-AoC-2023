package pl.appga.aoc2023.days

import pl.appga.aoc2023.utils.DayTask
import pl.appga.aoc2023.utils.Point
import pl.appga.aoc2023.utils.Vector

fun main() {
    Day16().run(
        runTest1 = true, runTask1 = true,
        runTest2 = true, runTask2 = true
    )
}

class Day16 : DayTask<Long>(16) {
    data class Place(val point: Point, val type: Char)
    data class Trail(val point: Point, val dir: Vector)
    class Layout(val points: Map<Point, Place>, val size: Vector)

    override fun solveTask1(inputFile: List<String>): Long {
        val layout = inputFile.toLayout()
        return countEnergizedPlaces(Trail(Point(0, 0), Vector.E), layout)
    }

    override fun solveTask2(inputFile: List<String>): Long {
        val layout = inputFile.toLayout()
        val trails1 = (0..<layout.size.x).flatMap { x ->
            listOf(Trail(Point(x, 0), Vector.S), Trail(Point(x, layout.size.y - 1), Vector.N))
        }
        val trails2 = (0..<layout.size.y).flatMap { y ->
            listOf(Trail(Point(0, y), Vector.E), Trail(Point(layout.size.x - 1, y), Vector.E))
        }
        return (trails1 + trails2).maxOf { trail -> countEnergizedPlaces(trail, layout) }
    }

    private fun countEnergizedPlaces(startingTrail: Trail, layout: Layout): Long {
        fun inRange(p: Point) = p.x in 0..<layout.size.x && p.y in 0..<layout.size.y
        val visited = mutableSetOf<Trail>()
        val energized = mutableSetOf<Point>()
        val trails = mutableListOf<Trail>()
        fun addToCheck(p: Point, d: Vector) {
            (p + d).takeIf(::inRange)?.let { trails += Trail(it, d) }
        }
        trails += startingTrail
        while (trails.isNotEmpty()) {
            val trail = trails.removeAt(0)
            val dir = trail.dir
            val point = trail.point
            if (trail !in visited) {
                visited += trail
                energized += point
                val nextDirections = layout.points[point]?.directionChange(dir) ?: listOf(dir)
                nextDirections.forEach { nextDir -> addToCheck(point, nextDir) }
            }
        }
        return energized.size.toLong()
    }

    private fun Place.directionChange(d: Vector) = when (type) {
        '|' -> when (d) {
            Vector.N -> listOf(Vector.N)
            Vector.E -> listOf(Vector.N, Vector.S)
            Vector.S -> listOf(Vector.S)
            else -> listOf(Vector.N, Vector.S)
        }

        '-' -> when (d) {
            Vector.N -> listOf(Vector.E, Vector.W)
            Vector.E -> listOf(Vector.E)
            Vector.S -> listOf(Vector.E, Vector.W)
            else -> listOf(Vector.W)
        }

        '/' -> when (d) {
            Vector.N -> listOf(Vector.E)
            Vector.E -> listOf(Vector.N)
            Vector.S -> listOf(Vector.W)
            else -> listOf(Vector.S)
        }

        else -> when (d) {
            Vector.N -> listOf(Vector.W)
            Vector.E -> listOf(Vector.S)
            Vector.S -> listOf(Vector.E)
            else -> listOf(Vector.N)
        }
    }

    private fun List<String>.toLayout(): Layout {
        val points = mapIndexed { y, line ->
            line.mapIndexedNotNull { x, ch ->
                ch.takeIf { it != '.' }?.let { Place(Point(x, y), ch) }
            }
        }.flatten()
        return Layout(points = points.associateBy { it.point }, size = Vector(x = get(0).length, y = size))
    }

    override val expectedTest1Result: Long = 46
    override val expectedTask1Result: Long = 7543

    override val expectedTest2Result: Long = 51
    override val expectedTask2Result: Long = 8231

}