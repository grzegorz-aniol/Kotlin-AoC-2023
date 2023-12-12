package pl.appga.aoc2023.days

import pl.appga.aoc2023.utils.DayTask

fun main() {
    Day13().also { it.test() }.run(
        runTest1 = true, runTask1 = true,
        runTest2 = true, runTask2 = true
    )
}

class Day13 : DayTask<Long>(13) {

    private class World(
        val rows: List<String>,
        val rowCheckSum: List<Int> = emptyList(),
        val colCheckSum: List<Int> = emptyList()
    )

    override fun solveTask1(inputFile: List<String>): Long {
        return inputFile.asGroups().asWorlds().sumOf { world -> world.findSymmetries().sum() }
    }

    override fun solveTask2(inputFile: List<String>): Long {
        return inputFile.asGroups().sumOf { groups ->
            val originalWorld = groups.toWorld()
            val symmetry = originalWorld.findSymmetries().toSet()
            val newSymmetry = originalWorld.rows.withTransformations().flatMap { transformation ->
                transformation.toWorld().findSymmetries().filter { it !in symmetry }
            }.first { it !in symmetry }
            newSymmetry
        }
    }

    private fun World.findSymmetries(): List<Long> {
        return (findSymmetry(colCheckSum).map { it.toLong() } +
                findSymmetry(rowCheckSum).map { 100L * it })
    }

    private fun List<String>.withTransformations(): Sequence<List<String>> = sequence {
        val pattern = this@withTransformations
        pattern.indices.forEach { y ->
            pattern[y].withIndex().forEach { (pos, x) ->
                val copy = pattern.toMutableList()
                val newChar = if (x == '.') '#' else '.'
                copy[y] = copy[y].replaceRange(pos, pos + 1, newChar.toString())
                yield(copy)
            }
        }
    }

    private fun checkSymmetry(pos: Int, numbers: List<Int>): Boolean {
        if (pos !in numbers.indices) {
            return false
        }
        val len = pos.coerceAtMost(numbers.size - pos - 2)
        if (len < 0) {
            return false
        }
        return (0..len).all { d -> numbers[pos - d] == numbers[pos + 1 + d] }
    }

    private fun findSymmetry(numbers: List<Int>): List<Int> =
        (0..<numbers.size - 1)
            .filter { n -> n in numbers.indices && checkSymmetry(n, numbers) }
            .map { it + 1 }

    private fun Iterable<String>.asGroups(): Sequence<List<String>> = sequence {
        val rows = mutableListOf<String>()
        forEach { line ->
            if (line.isBlank()) {
                yield(rows)
                rows.clear()
            } else {
                rows += line
            }
        }
        if (rows.isNotEmpty()) {
            yield(rows)
        }
    }

    private fun List<String>.toWorld(): World {
        val rowCheckSums = map { it.hashCode() }
        val columnCheckSums = (0..<this[0].length).map { columnIndex ->
            this.map { it[columnIndex] }.joinToString(separator = "").hashCode()
        }
        return World(this, rowCheckSums, columnCheckSums)
    }

    private fun Sequence<List<String>>.asWorlds(): Sequence<World> = map { it.toWorld() }

    fun test() {
        check(findSymmetry(listOf(2, 3, 2, 4, 2, 3)).isEmpty())
        check(findSymmetry(listOf(2, 3, 2, 4, 4, 2, 3)).containsAll(listOf(4)))
        check(findSymmetry(listOf(2, 3, 3, 2, 4, 4)).containsAll(listOf(2, 5)))
        check(findSymmetry(listOf(2, 3, 2, 4, 4)).containsAll(listOf(4)))
        check(findSymmetry(listOf(5, 5, 3, 1, 1, 3, 7)).containsAll(listOf(1)))
        check(findSymmetry(listOf(5, 5, 3, 1, 1)).containsAll(listOf(1, 4)))
        check(findSymmetry(listOf(1, 1, 3, 3, 1, 1)).containsAll(listOf(3)))
        check(findSymmetry(listOf(1, 1, 6, 6, 1, 1)).containsAll(listOf(3)))
        check(findSymmetry(listOf(1, 2, 2, 1, 1, 2, 2, 1)).containsAll(listOf(4)))
    }

    override val expectedTest1Result: Long = 405
    override val expectedTask1Result: Long = 37025

    override val expectedTest2Result: Long = 400
    override val expectedTask2Result: Long = 32854

}