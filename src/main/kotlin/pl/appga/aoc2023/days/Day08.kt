package pl.appga.aoc2023.days

import pl.appga.aoc2023.utils.DayTask
import pl.appga.aoc2023.utils.leastCommonMultiple

fun main() {
    Day08().run(
        runTest1 = true,
        runTask1 = true,
        sameTestFileForTest2 = false,
        runTest2 = true,
        runTask2 = true
    )
}

class Day08 : DayTask<Long>(8) {

    private data class Node(val id: String, val leftId: String, val rightId: String, val isStart: Boolean, val isEnd: Boolean) {
        var nodes: List<Node> = emptyList()
    }

    private data class Network(val directions: List<Int>, val nodes: List<Node>)

    override fun solveTask1(inputFile: List<String>): Long {
        val network = inputFile.toNetwork()
        return findWayOut(network, startingNodes = network.nodes.filter { it.isStart })
    }

    override fun solveTask2(inputFile: List<String>): Long {
        val network = inputFile.toNetwork(startRule = { it.endsWith("A") }, endRule = { it.endsWith("Z") })
        return findWayOut(network, startingNodes = network.nodes.filter { it.isStart })
    }

    private fun findWayOut(network: Network, startingNodes: List<Node>): Long =
        startingNodes.map {
            var node = it
            var count = 0L
            do {
                network.directions.forEach { dir ->
                    ++count
                    node = node.nodes[dir]
                }
            } while (!node.isEnd)
            count
        }.leastCommonMultiple()

    private fun List<String>.toNetwork(
        startRule: (String) -> Boolean = { it == "AAA" },
        endRule: (String) -> Boolean = { it == "ZZZ" }
    ): Network {
        val regexIds = "[A-Z0-9]+".toRegex()
        val iter = iterator()
        val network = Network(
            directions = iter.next().trim().map { if (it == 'L') 0 else 1 },
            nodes = iter.asSequence()
                .filter { it.isNotBlank() }
                .map {
                    val (n1, n2, n3) = regexIds.findAll(it).map { it.value }.toList()
                    Node(n1, n2, n3, startRule(n1), endRule(n1))
                }.toList()
        )
        val nodesById = network.nodes.associateBy { it.id }
        network.nodes.forEach { it.nodes = listOf(nodesById[it.leftId]!!, nodesById[it.rightId]!!) }
        return network
    }

    override val expectedTest1Result: Long = 6
    override val expectedTask1Result: Long = 16043

    override val expectedTest2Result: Long = 6
    override val expectedTask2Result: Long = 15726453850399

}