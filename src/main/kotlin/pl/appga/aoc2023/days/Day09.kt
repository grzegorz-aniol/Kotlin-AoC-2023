package pl.appga.aoc2023.days

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import pl.appga.aoc2023.utils.DayTask

fun main() {
    Day09().run(
        runTest1 = true, runTask1 = true,
        runTest2 = true, runTask2 = true
    )
}

class Day09 : DayTask<Long>(9) {

    class History(val numbers: List<Long>)

    override fun solveTask1(inputFile: List<String>): Long = runBlocking(Dispatchers.Default) {
        solve(inputFile.toReport())
    }

    override fun solveTask2(inputFile: List<String>): Long = runBlocking(Dispatchers.Default) {
        solve(inputFile.toReport(), last = false)
    }

    private suspend fun solve(report: List<History>, last: Boolean = true): Long = coroutineScope {
        report.map { h ->
            async {
                var seq = h.numbers
                val output = mutableListOf<List<Long>>()
                do {
                    output += seq
                    seq = seq.diff()
                } while (!seq.allZeros())
                output.reversed().fold(0L) { acc, items ->
                    if (last) {
                        items.last() + acc
                    } else {
                        items.first() - acc
                    }
                }
            }
        }.awaitAll().sum()
    }

    private fun List<String>.toReport(): List<History> = map { History(it.split(' ').map { it.toLong() }) }

    private fun List<Long>.diff(): List<Long> = windowed(size = 2) { (a: Long, b: Long) -> b - a }

    private fun List<Long>.allZeros(): Boolean = all { it == 0L }

    override val expectedTest1Result: Long = 114
    override val expectedTask1Result: Long = 1819125966

    override val expectedTest2Result: Long = 2
    override val expectedTask2Result: Long = 1140

}