package pl.appga.aoc2023.days

import pl.appga.aoc2023.utils.DayTask

fun main() {
    Day05().run(
        runTest1 = true, runTask1 = true,
        runTest2 = true, runTask2 = true
    )
}

class Day05 : DayTask<Long>(5) {

    private fun solve(almanac: Almanac): Long {
        val values = almanac.seeds.map { it.start }.toMutableList()
        for (map in almanac.maps) {
            values.withIndex().forEach { (index, value) ->
                values[index] = map.rules.firstOrNull { value in it.sourceRange }
                    ?.let { it.destStart + (value - it.sourceRange.start )}
                    ?: value
            }
        }
        return values.min()
    }

    private fun solve2nd(almanac: Almanac): Long {
        var rangesBeforeMap = almanac.seeds.toMutableList()
        almanac.maps.forEach { map ->
            val transformedRanges = mutableListOf<ClosedRange<Long>>()
            var allRangesPool = rangesBeforeMap.toSet()
            map.rules.forEach { rule ->
                val nextRoundRanges = mutableSetOf<ClosedRange<Long>>()
                allRangesPool.forEach { range ->
                    val result = rule.transform(range)
                    result.transformed?.let { transformedRanges += it }
                    nextRoundRanges += result.rest
                }
                allRangesPool = nextRoundRanges
            }
            transformedRanges += allRangesPool // move the rest of ranges
            rangesBeforeMap = transformedRanges
        }
        return rangesBeforeMap.minOf { it.start }
    }

    override fun solveTask1(inputFile: List<String>): Long = solve(inputFile.toAlmanac())

    override fun solveTask2(inputFile: List<String>): Long = solve2nd(inputFile.toAlmanac(withSeedRanges = true))

    private data class MapRule(val destStart: Long, val destRange: ClosedRange<Long>, val sourceRange: ClosedRange<Long>, val length: Long) {

        operator fun ClosedRange<Long>.contains(range: ClosedRange<Long>): Boolean = range.start in this && range.endInclusive in this

        data class RangesGroups(val intersection: ClosedRange<Long>?, val rest: List<ClosedRange<Long>>)
        data class TransformationResult(val transformed: ClosedRange<Long>?, val rest: List<ClosedRange<Long>>)

        fun ClosedRange<Long>.cutBy(range: ClosedRange<Long>): RangesGroups = when {
            range in this -> RangesGroups(intersection = range, rest = listOf(start..<range.start, range.endInclusive+1..endInclusive))
            this in range -> RangesGroups(intersection = this, emptyList())
            range.start in this -> RangesGroups(intersection = range.start..endInclusive, rest = listOf(start..<range.start))
            start in range -> RangesGroups(intersection = start..range.endInclusive, rest = listOf(range.endInclusive + 1..endInclusive))
            else -> RangesGroups(intersection = null, rest = listOf(this))
        }

        fun transform(range: ClosedRange<Long>) = transformRange(range, sourceRange, destRange)

        private fun transformRange(range: ClosedRange<Long>, source: ClosedRange<Long>, target: ClosedRange<Long>): TransformationResult {
            val cutResult = range.cutBy(source)
            val transformed = cutResult.intersection?.let { intersection ->
                val delta = target.start - source.start
                intersection.start + delta..intersection.endInclusive + delta
            }
            return TransformationResult(transformed, cutResult.rest)
        }
    }

    private data class MapRules(val name: String, val rules: List<MapRule>)
    private data class Almanac(val seeds: List<ClosedRange<Long>>, val maps: List<MapRules>)

    private fun List<String>.toAlmanac(withSeedRanges: Boolean = false): Almanac {
        val digitsRegex = """\d+""".toRegex()
        val iter = iterator()
        val seeds = digitsRegex.findAll(iter.next()).map { it.value.toLong() }.toList().let {
            if (withSeedRanges) {
                it.chunked(2).map { (start, length) -> start..<start + length }
            } else {
                it.map { it..it }
            }
        }.sortedBy { it.first }
        iter.next() // read empty line
        val maps = sequence<MapRules> {
            while (iter.hasNext()) {
                val name = iter.next()
                val rules = sequence<MapRule> {
                    do {
                        val line = iter.next()
                        if (line.isNotBlank()) {
                            val (dest, source, length) = digitsRegex.findAll(line).map { it.value.toLong() }.toList()
                            yield(MapRule(dest, dest ..< dest + length, source..< source + length, length))
                        }
                    } while (iter.hasNext() && line.isNotBlank())
                }.toList().sortedBy { it.sourceRange.start }
                yield(MapRules(name, rules))
            }
        }.toList()
        return Almanac(seeds, maps)
    }

    override val expectedTest1Result: Long = 35
    override val expectedTask1Result: Long = 424490994

    override val expectedTest2Result: Long = 46
    override val expectedTask2Result: Long = 15290096

}