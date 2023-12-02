package pl.appga.aoc2023.day01

import pl.appga.aoc2023.utils.DayTask

fun main() {
    Day02().run(
        runTest1 = true, runTask1 = true,
        runTest2 = true, runTask2 = true
    )
}

private data class Handful(val red: Int, val green: Int, val blue: Int)
private data class Game(val num: Int, val handfuls: List<Handful>)

class Day02: DayTask<Int>(2) {

    override fun solveTask1(inputFile: List<String>): Int =
        inputFile.loadGames().scoreGames { game ->
            with (game.handfuls) {
                if (maxOf { it.red } <= 12 && maxOf { it.green } <= 13 && maxOf { it.blue } <= 14) {
                    game.num
                } else {
                    0
                }
            }
        }

    override fun solveTask2(inputFile: List<String>): Int =
        inputFile.loadGames().scoreGames { game ->
            with (game.handfuls) {
                maxOf { it.red } * maxOf { it.green }  * maxOf { it.blue }
            }
        }

    private fun List<Game>.scoreGames(scoreFun: (Game) -> Int): Int = sumOf { scoreFun(it) }

    private fun List<String>.loadGames(): List<Game> {
        return map { line ->
            val (gameNum, handfuls: List<Handful>) = line.trim().split(":").let { entries ->
                val gameNum = entries[0].trim().split(" ").last().toInt()
                val handful: List<Handful> = entries[1].split(";").map { itHandful ->
                    val map: Map<String, Int> = itHandful.split(",").map { itColor ->
                        val (itemsText, colorText) = itColor.trim().split(" ")
                        colorText to itemsText.toInt()
                    }.toMap()
                    Handful(
                        red = map["red"] ?: 0,
                        green = map["green"] ?: 0,
                        blue = map["blue"] ?: 0
                    )
                }
                gameNum to handful
            }
            Game(num = gameNum, handfuls = handfuls)
        }
    }

    override val expectedTest1Result: Int = 8
    override val expectedTask1Result: Int = 2683

    override val expectedTest2Result: Int = 2286
    override val expectedTask2Result: Int = 49710

}