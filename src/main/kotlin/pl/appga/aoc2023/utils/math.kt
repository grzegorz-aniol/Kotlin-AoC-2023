package pl.appga.aoc2023.utils

fun leastCommonMultiple(x: Long, y: Long): Long {
    if (x == y) {
        return x
    }
    val bigger = if (x > y) x else y
    val max = x * y
    var d = bigger
    while (d <= max) {
        if (d % x == 0L && d % y == 0L) {
            return d
        }
        d += bigger
    }
    return max
}

fun List<Long>.leastCommonMultiple(): Long {
    return when {
        isEmpty() -> 0
        size == 1 -> this[0]
        else -> drop(1).foldRight(this[0]) { acc, v -> leastCommonMultiple(acc,v) }
    }
}
