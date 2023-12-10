package pl.appga.aoc2023.days

import pl.appga.aoc2023.utils.DayTask
import pl.appga.aoc2023.utils.Point
import pl.appga.aoc2023.utils.Vector

fun main() {
    Day10().run(
        runTest1 = true,
        runTask1 = true,
        runTest2 = true, sameTestFileForTest2 = false,
        runTask2 = true
    )
}

private class Tile(val type: Char, val point: Point, val move: Pair<Vector, Vector>?)

private class Pipes(val tiles: Array<Array<Tile>>, val start: Point, val size: Point) {
    fun get(p: Point): Tile = tiles[p.y][p.x]
}

class Day10 : DayTask<Long>(10) {

    override fun solveTask1(inputFile: List<String>): Long {
        val pipes = inputFile.loadPipes()
        return getLoop(pipes).size / 2L
    }

    override fun solveTask2(inputFile: List<String>): Long {
        val pipes = inputFile.loadPipes()
        val loop = getLoop(pipes)
        val crossTypes = setOf('|','F', '7')
        return loop.groupBy { it.point.y }
            .values
            .sumOf { row ->
                val rowPointsByX = row.associateBy { it.point.x }
                var inside = false
                var count = 0L
                for (x in 0..<pipes.size.x) {
                    val p = rowPointsByX[x]
                    if (p != null) {
                        if (p.type in crossTypes) {
                            inside = !inside
                        }
                    } else {
                        if (inside) {
                            ++count
                        }
                    }
                }
                count
            }
    }

    private fun getLoop(pipes: Pipes): List<Tile> = sequence<Tile> {
        var p1 = pipes.start
        var count = 0L
        var dir = pipes.get(pipes.start).move!!.first
        do {
            val t1 = pipes.get(p1)
            yield(t1)
            val move = t1.move!!
            if (move.first == dir) {
                dir = move.second
                p1 += dir
            } else if (move.second == -dir) {
                dir = -move.first
                p1 += dir
            } else {
                throw RuntimeException("Unknown direction")
            }
            count++
        } while (p1 != pipes.start)
    }.toList()

    private val pipeVectors = mapOf(
        '|' to (-Vector.N to Vector.S),
        '-' to (-Vector.E to Vector.W),
        'L' to (-Vector.N to Vector.E),
        'J' to (-Vector.N to Vector.W),
        '7' to (-Vector.S to Vector.W),
        'F' to (-Vector.S to Vector.E),
    )

    private fun List<String>.loadPipes(): Pipes {
        assert(-Vector.N == Vector.S)
        var start: Point? = null
        val tiles = mapIndexed { y, row ->
            row.mapIndexed { x, ch ->
                val point = Point(x,y)
                if (ch == 'S') {
                    start = point
                }
                Tile(ch, point, pipeVectors[ch])
            }.toTypedArray()
        }.toTypedArray()
        val size = Point(tiles[0].size, tiles.size)
        val startType = if (size.y < 20) {
            'F'
        } else {
            'L'
        }
        val startTile = tiles[start!!.y][start!!.x]
        tiles[start!!.y][start!!.x] = Tile(startType, startTile.point, pipeVectors[startType])
        return Pipes(tiles, start!!, size)
    }

    override val expectedTest1Result: Long = 8
    override val expectedTask1Result: Long = 6886

    override val expectedTest2Result: Long = 8
    override val expectedTask2Result: Long = 371

}