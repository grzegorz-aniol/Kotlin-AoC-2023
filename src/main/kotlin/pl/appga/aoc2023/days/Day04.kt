package pl.appga.aoc2023.days

import pl.appga.aoc2023.utils.DayTask
import kotlin.math.pow

fun main() {
    Day04().run(
        runTest1 = true, runTask1 = true,
        runTest2 = true, runTask2 = true
    )
}

class Day04 : DayTask<Long>(4) {

    private fun score(n: Int): Long = 2.0.pow(n.toDouble() - 1.0).toLong()

    override fun solveTask1(inputFile: List<String>): Long =
        inputFile.toCards().sumOf { score(it.winNumbers.intersect(it.ownNumbers).count()) }

    override fun solveTask2(inputFile: List<String>): Long {
        val cardCopies = mutableMapOf<Int, Long>()
        val cards = inputFile.toCards()
        return cards.sumOf { card ->
            val winPoints = card.winNumbers.intersect(card.ownNumbers).count()
            val times = 1 + cardCopies.getOrDefault(card.num, 0)
            for (nextCardNum in card.num + 1 .. (card.num + winPoints).coerceAtMost(cards.size)) {
                cardCopies.merge(nextCardNum, times) { value, current -> value + current }
            }
            times
        }
    }

    data class Card(val num: Int, val winNumbers: Set<Long>, val ownNumbers: Set<Long>)

    private fun List<String>.toCards(): List<Card> =
        map { line ->
            val parts = line.split(':', '|').map { it.trim() }
            Card(
                num = parts[0].split(" ").last().toInt(),
                winNumbers = parts[1].split(" ").filter { it.isNotBlank() }.map { it.toLong() }.toSet(),
                ownNumbers = parts[2].split(" ").filter { it.isNotBlank() }.map { it.toLong() }.toSet()
            )
        }

    override val expectedTest1Result: Long = 13
    override val expectedTask1Result: Long = 21158

    override val expectedTest2Result: Long = 30
    override val expectedTask2Result: Long = 6050769

}