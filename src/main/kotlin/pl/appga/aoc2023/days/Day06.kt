package pl.appga.aoc2023.days

import pl.appga.aoc2023.utils.DayTask
import pl.appga.aoc2023.utils.mulOfLong

fun main() {
    Day06().run(
        runTest1 = true, runTask1 = true,
        runTest2 = true, runTask2 = true
    )
}

class Day06 : DayTask<Long>(6) {
    override fun solveTask1(inputFile: List<String>): Long {
        return inputFile.toRaceRecords().mulOfLong { race ->
            (1L..race.raceTime).sumOf { time ->
                val d = raceDistance(time, race.raceTime)
                if (d > race.distanceRecord) {
                    1L
                } else {
                    0L
                }
            }
        }
    }

    override fun solveTask2(inputFile: List<String>): Long {
        return inputFile.toRaceRecords(badKerning = true)[0].let { race ->
            (1L..race.raceTime).sumOf { time ->
                val d = raceDistance(time, race.raceTime)
                if (d > race.distanceRecord) {
                    1L
                } else {
                    0L
                }
            }
        }
    }

    private fun raceDistance(chargeTime: Long, raceTime: Long): Long {
        return chargeTime * raceTime - (chargeTime * chargeTime)
    }

    private data class Race(val raceTime: Long, val distanceRecord: Long)

    private fun List<String>.toRaceRecords(badKerning: Boolean = false): List<Race> {
        fun String.getNumbers(): List<String> = "\\d+".toRegex().findAll(this).map { it.value }.toList()
        val times = get(0).getNumbers()
        val distances = get(1).getNumbers()
        return if (badKerning) {
            listOf(Race(raceTime = times.joinToString(separator = "").toLong(), distanceRecord = distances.joinToString(separator = "").toLong()))
        } else {
            times.zip(distances).map { (t,d) -> Race(raceTime = t.toLong(), distanceRecord = d.toLong() )}
        }
    }

    override val expectedTest1Result: Long = 288
    override val expectedTask1Result: Long = 781200

    override val expectedTest2Result: Long = 71503
    override val expectedTask2Result: Long = 49240091

}