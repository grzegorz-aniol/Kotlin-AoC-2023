package pl.appga.aoc2023.utils

import java.io.InputStreamReader

abstract class DayTask<T>(private val day: Int) {

    abstract fun solveTask1(inputFile: List<String>): T
    abstract fun solveTask2(inputFile: List<String>): T

    abstract val expectedTest1Result: T
    abstract val expectedTest2Result: T
    abstract val expectedTask1Result: T
    abstract val expectedTask2Result: T

    fun run(runTest1: Boolean = true, runTask1: Boolean = false, runTest2: Boolean = false, runTask2: Boolean = false) {
        if (runTest1) {
            validate(solveTask1(readTestFile()), expectedTest1Result)
        }
        if (runTask1) {
            validate(solveTask1(readTaskFile()), expectedTask1Result)
        }
        if (runTest2) {
            validate(solveTask2(readTestFile(ver = "2")), expectedTest2Result)
        }
        if (runTask2) {
            validate(solveTask2(readTaskFile()), expectedTask2Result)
        }
    }

    private fun validate(actualResult: T, expectedResult: T): Boolean {
        val status = when {
            actualResult == expectedResult -> "OK"
            else -> "FAIL"
        }
        println("status: $status, result: $actualResult, expected: $expectedResult")
        return status == "OK"
    }

    private fun readTestFile(ver: String = "") =
        readFile("/Day${day.toString().padStart(2, '0')}_test${ver}.txt")

    private fun readTaskFile() =
        readFile("/Day${day.toString().padStart(2, '0')}.txt")

    private fun readFile(filePath: String) =
        InputStreamReader(this::class.java.getResource(filePath)!!.openStream())
            .readLines()

    private fun List<String>.toNumbers(): List<Int?> =
        map {
            if (it.isBlank()) {
                null
            } else {
                Integer.parseInt(it)
            }
        }

    private fun String.splitAndTrim(delimiters: String): List<String> = trim().split(delimiters).map { it.trim() }
}