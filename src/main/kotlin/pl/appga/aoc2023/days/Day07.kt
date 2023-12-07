package pl.appga.aoc2023.days

import pl.appga.aoc2023.utils.DayTask

fun main() {
    Day07().run(
        runTest1 = true, runTask1 = true,
        runTest2 = true, runTask2 = true
    )
}

class Day07 : DayTask<Long>(7) {

    data class Hand(val cards: String, val bid: Long, val cardValues: String, val pattern: String)

    override fun solveTask1(inputFile: List<String>): Long {
        val hands = inputFile.toHand(::cardValueStandard, ::cardsPatternStandard)
        return evaluate(hands)
    }

    override fun solveTask2(inputFile: List<String>): Long {
        val hands = inputFile.toHand(::cardValueEx, ::cardsPatternEx)
        return evaluate(hands)
    }

    private fun evaluate(hands: List<Hand>): Long =
        hands.sortedWith(::compareHands)
            .withIndex().sumOf { (index, hand) -> (index + 1) * hand.bid }

    companion object {

        private fun compareHands(h1: Hand, h2: Hand): Int {
            return when {
                h1.pattern != h2.pattern -> h1.pattern.compareTo(h2.pattern)
                else -> h1.cardValues.compareTo(h2.cardValues)
            }
        }

        fun cardValueStandard(ch: Char): Char =
            when (ch) {
                in '2'..'9' -> ch
                'T' -> 'A'
                'J' -> 'B'
                'Q' -> 'C'
                'K' -> 'D'
                'A' -> 'E'
                else -> '0'
            }

        fun cardValueEx(ch: Char): Char =
            if (ch == 'J') {
                '0'
            } else {
                cardValueStandard(ch)
            }

        fun cardsPatternStandard(cardValues: String): String {
            return cardValues.asSequence()
                .groupBy { it }
                .map { it.value.size }
                .sortedDescending()
                .joinToString(separator = "")
                .padEnd(5, padChar = '0')
        }

        fun cardsPatternEx(cardValues: String): String {
            val jokersCount = cardValues.count { it == '0' }
            val scores = cardValues.asSequence()
                .filterNot { it == '0' } // skipJokers
                .groupBy { it }
                .map { it.value.size }
                .sortedDescending()
                .toMutableList()
            if (scores.isEmpty()) {
                scores += jokersCount
            } else {
                scores[0] += jokersCount
            }
            return scores.joinToString(separator = "")
                .padEnd(5, padChar = '0')
        }

    }

    private fun List<String>.toHand(cardValuesFun: (Char) -> Char, cardPatternFun: (String) -> String): List<Hand> {
        return map {
            val (c, b) = it.split(" ")
            val values = c.map { ch -> cardValuesFun(ch) }.joinToString("")
            Hand(cards = c, bid = b.toLong(), cardValues = values, pattern = cardPatternFun(values))
        }
    }

    override val expectedTest1Result: Long = 6440
    override val expectedTask1Result: Long = 253910319

    override val expectedTest2Result: Long = 5905
    override val expectedTask2Result: Long = 254083736

}