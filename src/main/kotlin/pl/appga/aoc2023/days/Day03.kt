package pl.appga.aoc2023.days

import pl.appga.aoc2023.utils.Area
import pl.appga.aoc2023.utils.DayTask
import pl.appga.aoc2023.utils.Point
import pl.appga.aoc2023.utils.contains
import pl.appga.aoc2023.utils.mulOf

fun main() {
    Day03().run(
        runTest1 = true, runTask1 = true,
        runTest2 = true, runTask2 = true
    )
}

class Day03 : DayTask<Int>(3) {
    override fun solveTask1(inputFile: List<String>): Int {
        val schema = inputFile.toSchema()
        return schema.lines.sumOf { line ->
            line.numbers.sumOf { number ->
                when {
                    schema.isAdjacentToSymbol(line, number) -> number.num
                    else -> 0
                }
            }
        }
    }

    override fun solveTask2(inputFile: List<String>): Int {
        val schema = inputFile.toSchema()
        return schema.lines.sumOf { line ->
            line.symbols.sumOf { schemaSymbol ->
                when {
                    schemaSymbol.isGear() -> schema.findAllAdjacentNumbers(line, schemaSymbol)
                        .takeIf { it.size == 2 }
                        ?.mulOf { it.num } ?: 0
                    else -> 0 as Int
                }
            }
        }
    }

    private data class SchemaSymbol(val symbol: String, val position: Point) {
        fun isGear() = symbol == "*"
        infix fun adjacentTo(schemaNumber: SchemaNumber) = position in schemaNumber.area
    }

    private data class SchemaNumber(val num: Int, val area: Area)

    private data class SchemaLine(val index: Int, val numbers: List<SchemaNumber>, val symbols: List<SchemaSymbol>)

    private data class Schema(val lines: List<SchemaLine>) {
        private fun List<SchemaLine>.withNeighbours(index: Int): List<SchemaLine> =
            listOfNotNull(get(index), getOrNull(index - 1), getOrNull(index + 1))

        fun isAdjacentToSymbol(line: SchemaLine, schemaNumber: SchemaNumber): Boolean =
            lines.withNeighbours(line.index).flatMap { it.symbols }.any { it adjacentTo schemaNumber }

        fun findAllAdjacentNumbers(line: SchemaLine, schemaSymbol: SchemaSymbol): List<SchemaNumber> =
            lines.withNeighbours(line.index).flatMap { it.numbers }.filter { schemaSymbol adjacentTo it }
    }

    private fun List<String>.toSchema(): Schema {
        val searchNumber = Regex("\\d+")
        val searchSymbol = Regex("[^.\\d]")
        val lines = mapIndexed { index, line ->
            SchemaLine(
                index = index,
                numbers = searchNumber.findAll(line)
                    .map { SchemaNumber(it.value.toInt(), Area(it.range.first - 1, index - 1, it.range.last + 1, index + 1)) }
                    .toList(),
                symbols = searchSymbol.findAll(line)
                    .map { SchemaSymbol(it.value, Point(it.range.first, index)) }
                    .toList()
            )
        }
        return Schema(lines)
    }

    override val expectedTest1Result: Int = 4361
    override val expectedTask1Result: Int = 521515

    override val expectedTest2Result: Int = 467835
    override val expectedTask2Result: Int = 69527306

}