package pl.appga.aoc2023.days

import pl.appga.aoc2023.utils.DayTask
import pl.appga.aoc2023.utils.Point
import pl.appga.aoc2023.utils.Vector

fun main() {
    Day14().run(
        runTest1 = true, runTask1 = true,
        runTest2 = true, runTask2 = true
    )
}

class Day14 : DayTask<Long>(14) {

    sealed class Rock(val pos: Point) {
        abstract fun at(newPos: Point): Rock
    }

    class RoundRock(pos: Point) : Rock(pos) {
        override fun at(newPos: Point) = RoundRock(newPos)
    }

    class CubeRock(pos: Point) : Rock(pos) {
        override fun at(newPos: Point) = CubeRock(pos)
    }

    class Platform(val rocks: List<Rock>, val size: Vector)

    class Cycle(val offset: Int, val length: Int, val pattern: List<Long>)

    override fun solveTask1(inputFile: List<String>): Long =
        calculateTotalLoad(Vector.N, tilt(Vector.N, inputFile.toPlatform()))

    override fun solveTask2(inputFile: List<String>): Long {
        val numOfCycles = 1000000000
        val cycleDir = listOf(Vector.N, Vector.W, Vector.S, Vector.E)
        val platform = inputFile.toPlatform()

        val sequence = sequence<Long> {
            var p = platform
            while (true) {
                cycleDir.forEach { dir -> p = tilt(dir, p) }
                yield(calculateTotalLoad(Vector.N, p))
            }
        }
        val cycle = detectCycle(sequence)
        return cycle.pattern[((numOfCycles - 1) - cycle.offset) % cycle.length]
    }

    private fun detectCycle(sequence: Sequence<Long>): Cycle {
        val sample = sequence.take(500).toList()
        val (offset, length) = floyd(sample)!!
        val pattern = sequence.drop(offset).take(length).toList()
        return Cycle(offset, length, pattern)
    }

    private fun tilt(dir: Vector, platform: Platform): Platform {
        val dirSign = if (dir.x == 0) dir.y else dir.x
        val colSize = if (dir.x == 0) platform.size.y else platform.size.x
        fun Point.pointToPos() = if (dir.x == 0) y else x
        fun posToIndex(pos: Int): Int = if (dirSign < 0) pos else (colSize - pos - 1)
        val nextBlockPos = mutableMapOf<Point, Point>()
        val pointsByCol = platform.rocks
            .groupBy { point -> if (dir.x == 0) point.pos.x else point.pos.y }
            .map { entry ->
                entry.key to entry.value.sortedBy {
                    if (dir.x == 0) (-1 * dir.y * it.pos.y) else (-1 * dir.x * it.pos.x)
                }
            }.toMap()
        pointsByCol.forEach { (_, line) ->
            var lastFreeIndex = 0
            line.forEach { rock ->
                if (rock is RoundRock) {
                    val nextPoint = Point(
                        x = if (dir.x == 0) rock.pos.x else posToIndex(lastFreeIndex),
                        y = if (dir.y == 0) rock.pos.y else posToIndex(lastFreeIndex)
                    )
                    nextBlockPos[rock.pos] = nextPoint
                    lastFreeIndex++
                } else {
                    lastFreeIndex = posToIndex(rock.pos.pointToPos()) + 1
                    nextBlockPos[rock.pos] = rock.pos
                }
            }
        }
        return Platform(
            rocks = platform.rocks.map { it.at(nextBlockPos[it.pos]!!) },
            size = platform.size
        )
    }

    private fun calculateTotalLoad(dir: Vector, platform: Platform): Long {
        return platform.rocks.filterIsInstance<RoundRock>()
            .map { it.pos }
            .groupBy { if (dir.x == 0) it.y else it.x }
            .entries
            .filter { it.value.isNotEmpty() }
            .sumOf { (row, points) ->
                val rowValue = platform.size.y - row
                rowValue.toLong() * points.size.toLong()
            }
    }

    private fun floyd(sample: List<Long>): Pair<Int, Int>? {
        var p1 = 1
        var p2 = 2
        do {
            while (p2 < sample.size && ((p2 - p1 < 2) || sample[p1] != sample[p2])) {
                p1 += 1
                p2 += 2
            }
            if (p2 >= sample.size) {
                return null
            }
            val allSame = true
        } while (!allSame)

        var offset = 0
        p1 = 0
        while (sample[p1] != sample[p2]) {
            p1 += 1
            p2 += 1
            if (p1 >= sample.size || p2 >= sample.size) {
                return null
            }
            offset += 1
        }

        var length = 1
        p2 = p1 + 1
        var verified = false
        do {
            while (!verified || (sample[p1] != sample[p2])) {
                verified = true
                p2 += 1
                length += 1
                if (p2 >= sample.size) {
                    return null
                }
            }
            verified = (offset..<offset + length).all { sample[it] == sample[it + length] }
        } while (!verified)

        return offset to length
    }

    private fun List<String>.toPlatform(): Platform {
        val rocks = mapIndexed { y, line ->
            line.mapIndexedNotNull { x, ch ->
                val point = Point(x, y)
                when (ch) {
                    'O' -> RoundRock(point)
                    '#' -> CubeRock(point)
                    else -> null
                }
            }
        }.flatten()
        return Platform(rocks, size = Point(x = get(0).length, y = size))
    }

    override val expectedTest1Result: Long = 136
    override val expectedTask1Result: Long = 107430

    override val expectedTest2Result: Long = 64
    override val expectedTask2Result: Long = 96317

}