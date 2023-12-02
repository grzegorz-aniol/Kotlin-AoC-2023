package pl.appga.aoc2023.days

import pl.appga.aoc2023.utils.DayTask

fun main() {
    Day01().run(
        runTest1 = true, runTask1 = true,
        runTest2 = true, runTask2 = true
    )
}

private class Day01 : DayTask<Int>(1) {
    val digits = arrayOf("zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

    override fun solveTask1(inputFile: List<String>): Int {
        return inputFile.sumOf { line ->
            val value = line.first { it.isDigit() }.toString() + line.last { it.isDigit() }.toString()
            value.toInt()
        }
    }

    override fun solveTask2(inputFile: List<String>): Int {
        return inputFile.fold(0) { acc, line ->
            val value1 = line.withIndex().firstNotNullOf { (index, _) -> findDigit(line, index) }
            val value2 = line.withIndex().firstNotNullOf { (index, _) -> findDigit(line, line.length - index - 1) }
            acc + (10 * value1 + value2)
        }
    }

    fun findDigit(text: String, startIndex: Int): Int? {
        val digit = digits.withIndex().find { (digit, digitValue) ->
            text.substring(startIndex).startsWith(digit.toString()) || text.substring(startIndex).startsWith(digitValue)
        }
        return digit?.index
    }

    override val expectedTest1Result: Int = 142
    override val expectedTask1Result: Int = 55971
    override val expectedTest2Result: Int = 281
    override val expectedTask2Result: Int = 54719
}
