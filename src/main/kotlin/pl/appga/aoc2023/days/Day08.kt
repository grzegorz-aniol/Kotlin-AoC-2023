package pl.appga.aoc2023.days

import kotlinx.coroutines.coroutineScope
import pl.appga.aoc2023.utils.DayTask

fun main() {
    Day08().run(
//        runTest1 = true,
//        runTask1 = true,
        sameTestFileForTest2 = false,
//        runTest2 = true,
        runTask2 = true
    )
}

class Day08 : DayTask<Long>(8) {

    private data class Node(val id: String, val leftId: String, val rightId: String) {
        var nodes: List<Node> = emptyList()
        val isEnd: Boolean = id.endsWith("Z")
        val isStart: Boolean = id.endsWith("A")
    }

    private data class Network(val directions: List<Int>, val nodes: List<Node>)

    override fun solveTask1(inputFile: List<String>): Long {
        val network = inputFile.toNetwork()
        return findWayOut(network, startingNodes = network.nodes.filter { it.isStart })
    }

    override fun solveTask2(inputFile: List<String>): Long {
        val network = inputFile.toNetwork()
        return findWayOut(network, startingNodes = network.nodes.filter { it.isStart })
    }

    private fun findWayOut(network: Network, startingNodes: List<Node>): Long {
        var step = 0L
        val nodes = startingNodes.toTypedArray()
        var ts = System.currentTimeMillis()
        var lastCnt = 0L
        do {
            val dir = network.directions[(step % network.directions.size).toInt()]
            var found = 0
            for ((index, node) in nodes.withIndex()) {
                val nextNode = node.nodes[dir]
                nodes[index] = nextNode
                if (nextNode.isEnd) {
                    ++found
                }
            }
            ++step
            if (found > 0) {
                val newTs = System.currentTimeMillis()
                if ((newTs - ts) > 5_000) {
                    val speedPerSec = 1_000 * (step - lastCnt) / (newTs - ts)
                    println("$step ${nodes.joinToString { it.id }} (speed: $speedPerSec)")
                    ts = newTs
                    lastCnt = step
                }
            }
        } while (found != nodes.size)
        return step
    }

    private fun List<String>.toNetwork(): Network {
        val regexIds = "[A-Z0-9]+".toRegex()
        val iter = iterator()
        val network = Network(
            directions = iter.next().trim().map { if (it == 'L') 0 else 1 },
            nodes = iter.asSequence()
                .filter { it.isNotBlank() }
                .map {
                    val (n1, n2, n3) = regexIds.findAll(it).map { it.value }.toList()
                    Node(n1, n2, n3)
                }.toList()
        )
        val nodesById = network.nodes.associateBy { it.id }
        network.nodes.forEach { it.nodes = listOf(nodesById[it.leftId]!!, nodesById[it.rightId]!!) }
        return network
    }

    override val expectedTest1Result: Long = 6
    override val expectedTask1Result: Long = 16043

    override val expectedTest2Result: Long = 6
    override val expectedTask2Result: Long = 999

}