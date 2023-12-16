package pl.appga.aoc2023.days

import java.util.PriorityQueue
import pl.appga.aoc2023.utils.BoundingBox
import pl.appga.aoc2023.utils.DayTask
import pl.appga.aoc2023.utils.Point
import pl.appga.aoc2023.utils.Vector
import pl.appga.aoc2023.utils.distanceTo

fun main() {
    Day17().run(
        runTest1 = true, runTask1 = true,
        runTest2 = true, runTask2 = true
    )
}

class Day17 : DayTask<Long>(17) {

    private class Area(val lossByPoint: Map<Point, Long>, val boundingBox: BoundingBox)

    private class Path(
        val prev: Path?, val point: Point,
        val g: Long, val f: Long
    ) {
        val direction: Vector? = when {
            prev == null -> null
            else -> (point - prev.point)
        }
        val sameDirLen: Int = when {
            prev != null && prev.direction == direction -> prev.sameDirLen + 1
            else -> 1
        }
    }

    private data class GKey(val p: Point, val dir: Vector, val sameDirLen: Int)

    private fun nextDir1(p: Path): List<Vector> {
        if (p.direction == null) {
            return listOf(Vector.E, Vector.S)
        }
        val sides = listOf(p.direction.left(), p.direction.right())
        return if (p.sameDirLen < 3) {
            sides + listOf(p.direction)
        } else {
            sides
        }
    }

    private fun nextDir2(p: Path): List<Vector> {
        if (p.direction == null) {
            return listOf(Vector.E, Vector.S)
        }
        val sides = listOf(p.direction.left(), p.direction.right())
        return when {
            p.sameDirLen < 4 -> listOf(p.direction)
            p.sameDirLen < 10 -> sides + listOf(p.direction)
            else -> sides
        }
    }


    override fun solveTask1(inputFile: List<String>): Long {
        val area = inputFile.toArea()
        return findLeastHeatLoss(
            area = area,
            startPoint = area.boundingBox.min,
            endPoint = area.boundingBox.max,
            nextDir = ::nextDir1
        )
    }

    override fun solveTask2(inputFile: List<String>): Long {
        val area = inputFile.toArea()
        return findLeastHeatLoss(
            area = area,
            startPoint = area.boundingBox.min,
            endPoint = area.boundingBox.max,
            nextDir = ::nextDir2
        )
    }

    private fun findLeastHeatLoss(
        area: Area,
        startPoint: Point,
        endPoint: Point,
        nextDir: (Path) -> List<Vector>
    ): Long {
        val openList = PriorityQueue<Path>(compareBy { it.f })

        val bestG = mutableMapOf<GKey, Long>()
        fun h(point: Point): Long = point.distanceTo(endPoint).toLong()

        openList += Path(null, startPoint, 0, 0)

        while (openList.isNotEmpty()) {
            val path = openList.remove()
            val current = path.point

            if (current == endPoint) {
                return path.g
            }

            nextDir(path).map { d -> current + d }
                .filter { p -> area.boundingBox.inRange(p) }
                .forEach { neighbor ->
                    val loss = area.lossByPoint.getValue(neighbor)
                    val gNeighbor = path.g + loss
                    val fNeighbor = gNeighbor + h(neighbor)
                    val nextPath = Path(path, neighbor, gNeighbor, fNeighbor)
                    val key = GKey(neighbor, neighbor - current, nextPath.sameDirLen)
                    val gBestSoFar = bestG[key] ?: Long.MAX_VALUE
                    if (gNeighbor < gBestSoFar) {
                        bestG[key] = gNeighbor
                        openList += nextPath
                    }
                }
        }

        return Long.MAX_VALUE
    }

    private fun List<String>.toArea(): Area {
        val area = mapIndexed { y, row ->
            row.mapIndexed { x, ch ->
                val loss = ch.digitToInt().toLong()
                Point(x, y) to loss
            }
        }.flatten().toMap()
        return Area(area, BoundingBox(get(0).length, size))
    }

    override val expectedTest1Result: Long = 102
    override val expectedTask1Result: Long = 1260

    override val expectedTest2Result: Long = 94
    override val expectedTask2Result: Long = 1416

}
