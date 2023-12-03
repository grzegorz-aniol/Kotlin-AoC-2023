package pl.appga.aoc2023.days

import pl.appga.aoc2023.utils.DayTask

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
                if (schema.isAdjacentToSymbol(line, number)) {
                    number.num
                } else {
                    0
                }
            }
        }
    }

    override fun solveTask2(inputFile: List<String>): Int {
        val schema = inputFile.toSchema()
        return schema.lines.sumOf { line ->
            line.symbols.sumOf { schemaSymbol ->
                if (schemaSymbol.symbol == "*") {
                    schema.findAllAdjacentNumbers(line, schemaSymbol).takeIf { it.size == 2 }?.fold(1) { acc, it -> acc * it.num } ?: 0
                } else {
                    0 as Int
                }
            }
        }
    }

    private data class SchemaNumber(val num: Int, val linePosition: ClosedRange<Int>, val bufferPosition: ClosedRange<Int>)
    private data class SchemaSymbol(val symbol: String, val linePosition: Int)
    private data class SchemaLine(val index: Int, val numbers: List<SchemaNumber>, val symbols: List<SchemaSymbol>)
    private data class Schema(val lines: List<SchemaLine>) {
        fun isAdjacentToSymbol(line: SchemaLine, schemaNumber: SchemaNumber): Boolean {
            val prevLine = lines.getOrNull(line.index - 1)
            val nextLine = lines.getOrNull(line.index + 1)
            return when {
                line.symbols.any { it.linePosition == schemaNumber.linePosition.start - 1 } -> true
                line.symbols.any { it.linePosition == schemaNumber.linePosition.endInclusive + 1 } -> true
                prevLine?.symbols?.any { schemaNumber.bufferPosition.contains(it.linePosition) } ?: false -> true
                nextLine?.symbols?.any { schemaNumber.bufferPosition.contains(it.linePosition) } ?: false -> true
                else -> false
            }
        }
        fun findAllAdjacentNumbers(line: SchemaLine, schemaSymbol: SchemaSymbol): List<SchemaNumber> {
            val inLine = line.numbers.filter { number -> schemaSymbol.linePosition == number.linePosition.start -1 || schemaSymbol.linePosition == number.linePosition.endInclusive + 1 }
            val prevLine = lines.getOrNull(line.index - 1)
            val nextLine = lines.getOrNull(line.index + 1)
            val inPrevLine = prevLine?.numbers?.filter { number -> number.bufferPosition.contains(schemaSymbol.linePosition) } ?: emptyList()
            val inNextLine = nextLine?.numbers?.filter { number -> number.bufferPosition.contains(schemaSymbol.linePosition) } ?: emptyList()
            return inPrevLine + inLine + inNextLine
        }
    }

    private fun List<String>.toSchema(): Schema {
        val searchNumber = Regex("\\d+")
        val searchSymbol = Regex("[^.]")
        val lines = mapIndexed { index, line ->
            SchemaLine(
                index = index,
                numbers = searchNumber.findAll(line).map {
                    val bufferedPosition = it.range.first - 1..it.range.last + 1
                    SchemaNumber(it.value.toInt(), it.range, bufferedPosition)
                }.toList(),
                symbols = searchSymbol.findAll(line).map { SchemaSymbol(it.value, it.range.first) }.toList()
            )
        }
        return Schema(lines)
    }

    override val expectedTest1Result: Int = 4361
    override val expectedTask1Result: Int = 521515

    override val expectedTest2Result: Int = 467835
    override val expectedTask2Result: Int = 69527306

}