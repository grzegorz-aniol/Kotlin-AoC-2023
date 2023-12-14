package pl.appga.aoc2023.days

import java.util.LinkedList
import pl.appga.aoc2023.utils.DayTask

fun main() {
    Day15().run(
        runTest1 = true, runTask1 = true,
        runTest2 = true, runTask2 = true
    )
}

class Day15 : DayTask<Long>(15) {

    enum class Operation { ADD, REMOVE }
    data class Lens(val raw: String, val label: String, val op: Operation, val focal: Int?)
    data class Box(val boxIndex: Int, val items: LinkedList<Lens> = LinkedList()) {
        fun remove(lens: Lens) {
            items.indexOfFirst { it.label == lens.label }.takeIf { it != -1 }?.also{ items.removeAt(it) }
        }
        fun replace(lens: Lens) {
            val pos = items.indexOfFirst { it.label == lens.label }
            if (pos == -1) {
                items.addLast(lens)
            } else {
                items[pos] = lens
            }
        }
        fun getPower(): Long = items.withIndex().sumOf { (lensIndex, lens) ->
            (boxIndex + 1) * (lensIndex + 1) * (lens.focal?.toLong() ?: 1L)
        }
    }

    override fun solveTask1(inputFile: List<String>): Long =
        inputFile[0].asLenses().map { it.raw.getHash() }.sum().toLong()

    override fun solveTask2(inputFile: List<String>): Long {
        val boxes = Array(256) { Box(it) }
        inputFile[0].asLenses().forEach { lens ->
            val box = boxes[lens.label.getHash()]
            when (lens.op) {
                Operation.REMOVE -> box.remove(lens)
                Operation.ADD -> box.replace(lens)
            }
        }
        return boxes.sumOf { it.getPower() }
    }

    private fun String.toLens(): Lens {
        val (raw, label, oper, focal: String) = "(\\w+)([-=])(\\d+)?".toRegex().find(this)!!.groupValues
        return Lens(
            raw = this,
            label = label,
            op  = if (oper[0] == '-') Operation.REMOVE else Operation.ADD,
            focal = focal.takeIf { it.isNotBlank() }?.toInt()
        )
    }

    private fun String.asLenses() = sequence<Lens> {
        var acc = ""
        for (ch in iterator()) {
            if (ch == ',') {
                yield(acc.toLens())
                acc = ""
            } else {
                acc += ch
            }
        }
        yield(acc.toLens())
    }

    private fun String.getHash(): Int =
        fold(0) { acc, ch -> (17 * (acc + ch.code)) % 256 }

    override val expectedTest1Result: Long = 1320
    override val expectedTask1Result: Long = 514025

    override val expectedTest2Result: Long = 145
    override val expectedTask2Result: Long = 244461

}